package biz.global77.clinic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.UserRepository;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncode;

	@Override
	public User createUser(User user) {

		user.setPassword(passwordEncode.encode(user.getPassword()));
		user.setRole("ROLE_USER");
		
		return userRepo.save(user);
	}

	@Override
	public boolean checkEmail(String email) {

		return userRepo.existsByEmail(email);
	}

}