package com.nighthawk.spring_portfolio.mvc.csa_synergy;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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

    @NotEmpty
    @OneToOne
    private Grade grade;

    private String explanation;

    public GradeRequest(Grade grade, String explanation) {
        this.grade = grade;
        this.explanation = explanation;
    }
}
