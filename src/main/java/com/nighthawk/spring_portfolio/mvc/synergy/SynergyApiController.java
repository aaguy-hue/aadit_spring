package com.nighthawk.spring_portfolio.mvc.synergy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import java.util.Map;

@Controller
@RequestMapping("/api/synergy")
public class SynergyApiController {
    @Autowired
    private GradeJpaRepository gradeRepository;

    @Autowired
    private GradeRequestJpaRepository gradeRequestRepository;

    @Autowired
    private AssignmentJpaRepository assignmentRepository;

    @Autowired
    private PersonJpaRepository personRepository;

    @PostMapping("/update-grades")
    public String updateAllGrades(@RequestParam Map<String, String> grades) {
        for (String key : grades.keySet()) {
            String[] ids = key.replace("grades[", "").replace("]", "").split("\\[");
            Long assignmentId = Long.parseLong(ids[0]);
            Long studentId = Long.parseLong(ids[1]);
            String gradeString = grades.get(key);

            if (isNumeric(gradeString)) {
                Double gradeValue = Double.parseDouble(grades.get(key));
                Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
                Person student = personRepository.findById(studentId).orElse(null);
                
                Grade grade = gradeRepository.findByAssignmentAndStudent(assignment, student);
                if (grade == null) {
                    grade = new Grade();
                    grade.setAssignment(assignment);
                    grade.setStudent(student);
                }
                grade.setGrade(gradeValue);
                gradeRepository.save(grade);
            }
        }

        return "redirect:/mvc/synergy/edit-grades";
    }
    
    @PostMapping("/create-grade-request")
    public String createGradeRequest(
                                     @RequestParam Map<String, String> form) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Person grader = personRepository.findByEmail(email);
        if (grader == null) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "You must be a logged in user to do this"
            );
        }

        Long studentId = Long.valueOf(form.get("studentId"));
        Long assignmentId = Long.valueOf(form.get("assignmentId"));
        Double gradeSuggestion = Double.valueOf(form.get("gradeSuggestion"));
        String explanation = form.get("explanation");
    
        Person student = personRepository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + studentId));
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid assignment ID: " + assignmentId));
        
        // Create and save the GradeRequest
        GradeRequest gradeRequest = new GradeRequest(assignment, student, grader, explanation, gradeSuggestion);
        gradeRequestRepository.save(gradeRequest);

        return "redirect:/mvc/synergy/create-grade-request";
    }

    @PostMapping("/accept-request")
    public String acceptRequest(@RequestParam Long requestId) {
        GradeRequest request = gradeRequestRepository.findById(requestId).orElse(null);
        if (request == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Grade request not found"
            );
        }
        else if (request.isAccepted()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Grade request was already accepted before"
            );
        }

        Assignment assignment = request.getAssignment();
        Person student = request.getStudent();
        Grade grade = gradeRepository.findByAssignmentAndStudent(assignment, request.getStudent());
        if (grade == null) {
            grade = new Grade();
            grade.setAssignment(assignment);
            grade.setStudent(student);
        }
        grade.setGrade(request.getGradeSuggestion());
        gradeRepository.save(grade);

        request.accept();
        gradeRequestRepository.save(request);

        return "redirect:/mvc/synergy/view-requests";
    }

    @PostMapping("/reject-request")
    public String rejectRequest(@RequestParam Long requestId) {
        GradeRequest request = gradeRequestRepository.findById(requestId).orElse(null);
        if (request == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Grade request not found"
            );
        }
        else if (request.isAccepted()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Grade request was already accepted before"
            );
        }

        request.reject();
        gradeRequestRepository.save(request);

        return "redirect:/mvc/synergy/view-requests";
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

}