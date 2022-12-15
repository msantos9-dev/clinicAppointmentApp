package biz.global77.clinic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import biz.global77.clinic.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	public boolean existsByEmail(String email);

	public User findByEmail(String email);

	public Optional<User> findById(Long id);

	// For searching
	@Query("select u from User u where upper(CONCAT(u.fullName,'',u.email,'',u.role))  LIKE %?1%")
	public Page<User> search(String keyword, Pageable pageable);

	@Query("SELECT COUNT(u) FROM User u WHERE u.role LIKE %?1%")
	public long users(String identifier);

	public List<User> findByFullNameContaining(String fullName);

	public Page<User> findByRole(boolean published, Pageable pageable);

	public Page<User> findByFullNameContaining(String fullName, Pageable pageable);

	public List<User> findByFullNameContaining(String fullName, Sort sort);
}