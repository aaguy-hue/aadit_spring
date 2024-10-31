package com.nighthawk.spring_portfolio.mvc.csa_synergy;

import com.nighthawk.spring_portfolio.mvc.person.Person;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GradeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Boolean isAccepted;
    
    private String explanation;

    @NotEmpty
    private Double gradeSuggestion;

    @NotEmpty
    @ManyToOne
    private Person grader;
    
    @NotEmpty
    @ManyToOne
    private Person student;

    @NotEmpty
    @ManyToOne
    private Assignment assignment;


    public GradeRequest(Assignment assignment, Person student, Person grader, String explanation, Double gradeSuggestion) {
        this.gradeSuggestion = gradeSuggestion;
        this.explanation = explanation;
        this.grader = grader;
        this.student = student;
        this.assignment = assignment;
        this.isAccepted = null;
    }
}
