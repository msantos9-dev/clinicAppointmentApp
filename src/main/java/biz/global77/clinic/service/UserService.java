package biz.global77.clinic.service;

import biz.global77.clinic.model.User;

public interface UserService {

	public User createUser(User user);

	public boolean checkEmail(String email);

}