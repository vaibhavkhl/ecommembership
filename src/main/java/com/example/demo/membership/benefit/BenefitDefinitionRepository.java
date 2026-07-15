package com.example.demo.membership.benefit;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenefitDefinitionRepository extends JpaRepository<BenefitDefinition, Long> {

    Optional<BenefitDefinition> findByCode(String code);
}
