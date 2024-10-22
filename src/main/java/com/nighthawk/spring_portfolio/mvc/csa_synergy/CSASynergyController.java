package com.nighthawk.spring_portfolio.mvc.csa_synergy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/mvc/csa-synergy")
public class CSASynergyController {
    @Autowired
    private GradeJpaRepository gradeRepository;

    @Autowired
    private AssignmentJpaRepository assignmentRepository;

    @Autowired
    private PersonJpaRepository personRepository; 

    @GetMapping("/view-grades")
    public String viewGrades(Model model) {
        List<Assignment> assignments = assignmentRepository.findAll();
        List<Person> students = personRepository.findAll(); 
        List<Grade> gradesList = gradeRepository.findAll();

        Map<Long, Map<Long, Double>> grades = prepareGradesMap(gradesList, assignments, students);

        model.addAttribute("assignments", assignments);
        model.addAttribute("students", students);
        model.addAttribute("grades", grades);

        return "csa_synergy/view_grades";
    }

    private Map<Long, Map<Long, Double>> prepareGradesMap(List<Grade> gradesList, List<Assignment> assignments, List<Person> students) {
        Map<Long, Map<Long, Double>> gradesMap = new HashMap<>();

        // Default vals
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

        return "redirect:/mvc/csa-synergy/view-grades";  // Redirect back to the grades view
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

}