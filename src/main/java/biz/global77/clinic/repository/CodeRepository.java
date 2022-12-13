package biz.global77.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import biz.global77.clinic.model.Code;

public interface CodeRepository extends JpaRepository<Code, Integer> {
    
}
