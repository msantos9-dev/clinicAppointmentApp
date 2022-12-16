package biz.global77.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import biz.global77.clinic.model.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {

}
