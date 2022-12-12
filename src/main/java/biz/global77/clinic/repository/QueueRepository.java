package biz.global77.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import biz.global77.clinic.model.Queue;

public interface QueueRepository extends JpaRepository<Queue, Integer> {
    
}
