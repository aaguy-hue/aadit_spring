package com.nighthawk.spring_portfolio.mvc.csa_synergy;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentJpaRepository extends JpaRepository<Assignment, Long> {
    Assignment findByName(String name);
}