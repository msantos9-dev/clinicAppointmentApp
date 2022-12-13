package biz.global77.clinic.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import biz.global77.clinic.model.Queue;

public interface QueueRepository extends JpaRepository<Queue, Integer> {

    public List<Queue> findByStatus(String status);

    public List<Queue> findByStatusOrderById(String status);

}
