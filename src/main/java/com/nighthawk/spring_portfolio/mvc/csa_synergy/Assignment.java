package com.nighthawk.spring_portfolio.mvc.csa_synergy;


import com.vladmihalcea.hibernate.type.json.JsonType;

import java.util.List;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Convert(attributeName ="assignment", converter = JsonType.class)
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Size(min=5, max=50, message="Assignment Name (5 to 50 chars)")
    private String name;
    
    private String details;

    @OneToMany(mappedBy="assignment")
    private List<Grade> grades;

    public Assignment(String name, String details) {
        this.name = name;
        this.details = details;
    }

    // Test data 
    public static Assignment[] init() {
        return new Assignment[] {
            new Assignment("Dance Unit", "Be a little skibidi and do some dancing."),
            new Assignment("Sprint 1", "How's the website coming along?"),
            new Assignment("Sprint 2", "Please make a decent team teach"),
            new Assignment("Sprint 3", "Bro we just need some N@TM submissions that are presentable"),
        };
    }

}
