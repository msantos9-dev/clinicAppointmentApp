package biz.global77.clinic.service;

import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;

import biz.global77.clinic.model.User;

public interface UserService {

	public User createUser(User user);

	void saveUser(User user);

	User getUserById(int id);

	public boolean checkEmail(String email);

	List<User> getAllUser();

	Page<User> findAllUser(String keyword, Pageable pageable);

	Page<User> findPaginated(String keyword, int pageNo, int pageSize, String sortField, String sortDirection);

}