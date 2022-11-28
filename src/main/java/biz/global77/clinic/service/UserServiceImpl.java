package biz.global77.clinic.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.expression.Dates;

import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.UserRepository;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
public class UserServiceImpl implements UserService {

	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncode;

	@Override
	public User createUser(User user) {

		user.setPassword(passwordEncode.encode(user.getPassword()));
		user.setConfirmPassword(passwordEncode.encode(user.getConfirmPassword()));
		user.setEnabled(false);
		user.setAccountNonLocked(true);
		user.setRole("ROLE_USER");
		user.setRegistrationTime(dtf.format(now));

		// final String ACCOUNT_SID = "AC6e468560159566ea34eb3c8e50c49c40";
		// final String AUTH_TOKEN = "9638bf6cd3a97a41a45b30bc8d22243a";

		// Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		// Message message = Message.creator(
		// 		new com.twilio.type.PhoneNumber("+639610768081"),
		// 		"MGc03f15fa700ba449638ca4d8682c793b",
		// 		"Thank you for registering to Love Clinic App. \n You have registered with the ff credentials:\n "
		// 				+ "Fullname: " + user.getFullName()
		// 				+ "\n Address: " + user.getAddress()
		// 				+ "\nEmail: " + user.getEmail()
		// 				+ "\nPhone number" + user.getContactNumber())

		// 		.create();

		// System.out.println(message.getSid());

		return userRepo.save(user);
	}

	@Override
	public boolean checkEmail(String email) {

		return userRepo.existsByEmail(email);
	}

	@Override
	public List<User> getAllUser() {
		// TODO Auto-generated method stub
		return userRepo.findAll();
	}

	@Override
	public Page<User> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		// TODO Auto-generated method stub
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.userRepo.findAll(pageable);
	}

}