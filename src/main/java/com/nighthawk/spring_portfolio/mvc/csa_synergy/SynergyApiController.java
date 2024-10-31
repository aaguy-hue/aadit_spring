package com.nighthawk.spring_portfolio.mvc.csa_synergy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonDetailsService;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    @PostMapping("/accept-request")
    public String acceptRequest(@RequestParam Long requestId) {
        GradeRequest request = gradeRequestRepository.findById(requestId).orElse(null);
        if (request == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Grade request not found"
            );
        }
        else if (request.getIsAccepted()) {
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

        request.setIsAccepted(true);
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
        else if (request.getIsAccepted()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Grade request was already accepted before"
            );
        }
        
        request.setIsAccepted(false);
        gradeRequestRepository.save(request);

        return "redirect:/mvc/synergy/view-requests";
    }

    // @PostMapping("/submit-grade-request")
    // public String submitGradeRequest(
    //     @RequestParam Integer assignmentId, 
    //     @RequestParam Integer studentId,
    //     @RequestParam Integer authorId,
    //     @RequestParam 
    // ) {

    // }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

}