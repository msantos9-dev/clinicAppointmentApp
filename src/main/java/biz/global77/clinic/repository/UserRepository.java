package biz.global77.clinic.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import biz.global77.clinic.model.User;


public interface UserRepository extends JpaRepository<User, Integer> {

	public boolean existsByEmail(String email);
	public User findByEmail(String email);

}