package com.nighthawk.spring_portfolio.mvc.synergy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonDetailsService;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/mvc/synergy")
public class SynergyViewController {
    @Autowired
    private GradeJpaRepository gradeRepository;
    
    @Autowired
    private GradeRequestJpaRepository gradeRequestRepository;

    @Autowired
    private AssignmentJpaRepository assignmentRepository;

    @Autowired
    private PersonJpaRepository personRepository;

    @GetMapping("/view-grades")
    public String viewStudentGrades(@AuthenticationPrincipal Person student, Model model) {
        List<Grade> grades = gradeRepository.findByStudent(student);
        List<Assignment> assignments = assignmentRepository.findAll();
    
        Map<Long, Grade> assignmentGrades = new HashMap<>();
        for (Grade grade : grades) {
            assignmentGrades.put(grade.getAssignment().getId(), grade);
        }
    
        model.addAttribute("assignments", assignments);
        model.addAttribute("assignmentGrades", assignmentGrades);
        return "synergy/view_student_grades";
    }

    @GetMapping("/edit-grades")
    public String editGrades(Model model) {
        List<Assignment> assignments = assignmentRepository.findAll();
        List<Person> students = personRepository.findPeopleWithRole("ROLE_STUDENT");
        List<Grade> gradesList = gradeRepository.findAll();

        Map<Long, Map<Long, Double>> grades = createGradesMap(gradesList, assignments, students);

        model.addAttribute("assignments", assignments);
        model.addAttribute("students", students);
        model.addAttribute("grades", grades);

        return "synergy/edit_grades";
    }

    private Map<Long, Map<Long, Double>> createGradesMap(List<Grade> gradesList, List<Assignment> assignments, List<Person> students) {
        Map<Long, Map<Long, Double>> gradesMap = new HashMap<>();

        // Ok so these are the default vals
        for (Assignment assignment : assignments) {
            gradesMap.put(assignment.getId(), new HashMap<>());
            for (Person student : students) {
                gradesMap.get(assignment.getId()).put(student.getId(), null);
            }
        }

        for (Grade grade : gradesList) {
            if (gradesMap.containsKey(grade.getAssignment().getId())) {
                gradesMap.get(grade.getAssignment().getId()).put(grade.getStudent().getId(), grade.getGrade());
            }
        }

        return gradesMap;
    }

    @GetMapping("/view-requests")
    public String viewRequests(Model model) {
        List<GradeRequest> requests = gradeRequestRepository.findAll();
        System.out.println("Fetched requests: " + requests);
        model.addAttribute("requests", requests);
        return "synergy/view_grade_requests";
    }

    @GetMapping("/create-grade-request")
    public String createGradeRequest(Model model) {
        List<Assignment> assignments = assignmentRepository.findAll();
        List<Person> students = personRepository.findPeopleWithRole("ROLE_STUDENT");

        model.addAttribute("assignments", assignments);
        model.addAttribute("students", students);

        return "synergy/create_grade_request";
    }
}
