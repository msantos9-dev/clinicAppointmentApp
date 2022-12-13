package biz.global77.clinic.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
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
		user.setEnabled(true);
		user.setAccountNonLocked(true);
		if (user.getRole() == null) {
			user.setRole("ROLE_UNVERIFIED");
		}
		user.setRegistrationTime(dtf.format(now));

		// final String ACCOUNT_SID = "AC6e468560159566ea34eb3c8e50c49c40";
		// final String AUTH_TOKEN = "9638bf6cd3a97a41a45b30bc8d22243a";

		// Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		// Message message = Message.creator(
		// new com.twilio.type.PhoneNumber("+639610768081"),
		// "MGc03f15fa700ba449638ca4d8682c793b",
		// "Thank you for registering to Love Clinic App. \n You have registered with
		// the ff credentials:\n "
		// + "Fullname: " + user.getFullName()
		// + "\n Address: " + user.getAddress()
		// + "\nEmail: " + user.getEmail()
		// + "\nPhone number" + user.getContactNumber())

		// .create();

		// System.out.println(message.getSid());

		return userRepo.save(user);
	}

	// @Override
	// public List<User> findByFullNameContaining(String fullName) {
	// // TODO Auto-generated method stub
	// return userRepo.findByFullNameContaining(fullName);
	// }

	// @Override
	// public Page<User> findWithSearch(String keyword) {
	// // TODO Auto-generated method stub

	// if (keyword != null && keyword != "") {
	// keyword = keyword.toUpperCase();
	// return userRepo.search(keyword);
	// }

	// return userRepo.findAllUser(keyword);
	// }

	@Override
	public Page<User> findAllUser(String keyword,
			org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable pageable) {

		return null;
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
	public Page<User> findPaginated(String keyword, int pageNo, int pageSize, String sortField, String sortDirection) {
		// TODO Auto-generated method stub
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.userRepo.search(keyword,pageable);
	}

	@Override
	public User getUserById(int id) {
		// TODO Auto-generated method stub
		Optional<User> optional = userRepo.findById(id);
		User user = null;
		if (optional.isPresent()) {
			user = optional.get();
		} else {
			throw new RuntimeException(" User not found for id :: " + id);
		}
		return user;
	}

	@Override
	public void saveUser(User user) {
		// TODO Auto-generated method stub
		this.userRepo.save(user);
	}

}