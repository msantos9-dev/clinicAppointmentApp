package biz.global77.clinic.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.twilio.rest.monitor.v1.Alert;

import biz.global77.clinic.model.Code;
import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.UserRepository;
import biz.global77.clinic.service.CodeService;
import biz.global77.clinic.service.EmailSenderService;
import biz.global77.clinic.service.MessageSenderService;
import biz.global77.clinic.service.UserService;

import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private CodeService codeService;

	@Autowired
	private EmailSenderService emailSenderService;

	@Autowired
	private MessageSenderService messageSenderService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncode;

	@GetMapping({ "/" })
	public String index() {

		return "login";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("accessDenied")
	public String accessDenied() {
		return "accessDenied";
	}

	@GetMapping("/register")
	public String showForm(User user) {

		return "register";
	}

	@GetMapping("/forgotPassword")
	public String forgotPassword() {

		return "forgotPassword";
	}

	@GetMapping("/passChangeSuccess")
	public String passwordChangeSuccess() {

		return "passChangeSuccess";
	}

	@GetMapping("/forgotPassword/{code}/{id}")
	public String changePassword(@PathVariable(value = "code") String code, @PathVariable(value = "id") int id,
			User user,
			Model model) {

		long codeExist = codeService.getAllCode().stream()
				.filter(i -> i.getCode().equals(code)).count();
		User selectedUser = userService.getUserById(id);

		if (codeExist > 0) {
			System.out.println("codeExist");
			return "resetPassword";
			// messageSenderService.sendMessage("Your account has been verified. You may now
			// sign-in to your account. \n\n Here are your account details:"
			// + "\nFullname: " + selectedUser.getFullName()
			// + "\n Address: " + selectedUser.getAddress()
			// + "\n Phone number:" + selectedUser.getContactNumber()
			// + "\n Email: " + selectedUser.getEmail()
			// + "\n Password: " + selectedUser.getPassword()
			// + "\n\n Reminder: Never share this to anyone as they can get access to your
			// account. "
			// );
		} else {
			System.out.println("invalid code");
			return "invalidLink";
		}

	}

	@GetMapping("/resetPassword")
	public String resetPassword(User user, Model model) {

		User currentUser = userService.getUserById(user.getId());

		System.out.println(currentUser.getEmail());

		if (user.getPassword().equals(user.getConfirmPassword())) {
			currentUser.setPassword(passwordEncode.encode(user.getPassword()));
			currentUser.setConfirmPassword(passwordEncode.encode(user.getConfirmPassword()));
			userService.saveUser(currentUser);
			return "passChangeSuccess";
		} else {
			model.addAttribute("PasswordNotMatch", "Password do not match");
			return "resetPassword";
		}

	}

	@GetMapping("/forgotPass")
	public String forgotPass(User user, Model model, Code code) throws MessagingException {

		long userExist = userService.getAllUser().stream()
				.filter(i -> i.getEmail().equals(user.getEmail())).count();
		System.out.println(userExist);

		User user2 = userRepository.findByEmail(user.getEmail());

		if (userExist > 0) {
			code.setPatientID(user2);

			String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
			String numbers = "0123456789";

			String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;
			StringBuilder sb = new StringBuilder();
			Random random = new Random();

			int length = 30;

			for (int i = 0; i < length; i++) {

				int index = random.nextInt(alphaNumeric.length());
				char randomChar = alphaNumeric.charAt(index);

				sb.append(randomChar);
			}

			String randomString = sb.toString();

			code.setCode(randomString);
			codeService.saveCode(code);

			String recipient = code.getPatientID().getEmail();
			String message = "Please click the link to change your account password:\n http://localhost:6969/forgotPassword/"
					+ code.getCode() + "/" + code.getPatientID().getId();
			String subject = "Change Password";

			emailSenderService.sendMailWithoutAttachment(recipient, message, subject);

			return "passwordChange";

		} else {
			model.addAttribute("Invalid", "Email is not a valid email or is not registered");
		}

		return "forgotPassword";
	}

	@GetMapping("/unverifiedAccount")
	public String showUnverifiedAccount() {

		return "unverifiedAccount";
	}

	@GetMapping("/verifyAccount/{code}/{id}")
	public String showVerify(@PathVariable(value = "code") String code, @PathVariable(value = "id") int id, User user,
			Model model) {

		long codeExist = codeService.getAllCode().stream()
				.filter(i -> i.getCode().equals(code)).count();
		User selectedUser = userService.getUserById(id);

		if (codeExist > 0) {
			System.out.println("codeExist");
			selectedUser.setRole("ROLE_USER");
			selectedUser.setEnabled(true);
			userService.saveUser(selectedUser);
			messageSenderService.sendMessage(
					"Your account has been verified. You may now sign-in to your account. \n\n Here are your account details:"
							+ "\n Fullname: " + selectedUser.getFullName()
							+ "\n Address: " + selectedUser.getAddress()
							+ "\n Phone number:" + selectedUser.getContactNumber()
							+ "\n Email: " + selectedUser.getEmail()
							+ "\n\n Reminder: Never share this to anyone as they can get access to your account. ");
		} else {
			System.out.println("invalid code");
			return "register";
		}
		return "verifyAccount";
	}

	@PostMapping("/createUser")
	public String createuser(@Valid User user, BindingResult bindingResult, Code code,
			Model model) throws MessagingException {

		boolean emailExist = userService.checkEmail(user.getEmail());
		boolean passwordNotMatch = user.getPassword().equals(user.getConfirmPassword());
		long phoneExist = userService.getAllUser().stream()
				.filter(i -> i.getContactNumber().equals(user.getContactNumber())).count();
		System.out.println("Email exist:" + emailExist);
		System.out.println("Phone exist:" + phoneExist);
		System.out.println("Password match:" + passwordNotMatch);

		if (bindingResult.hasErrors()) {
			return "register";
		} else {
			if (phoneExist > 0) {
				model.addAttribute("phoneExist", "This phone is already registered");
				return "register";
			} else if (emailExist) {
				model.addAttribute("emailExist", "This email is already registered");
				return "register";
			} else if (!passwordNotMatch) {
				model.addAttribute("passwordNotMatch", "Password do not match");
				return "register";
			}
			model.addAttribute("loading", "loading");

			userService.createUser(user);

			code.setPatientID(user);

			String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
			String numbers = "0123456789";

			String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;
			StringBuilder sb = new StringBuilder();
			Random random = new Random();

			int length = 30;

			for (int i = 0; i < length; i++) {

				int index = random.nextInt(alphaNumeric.length());
				char randomChar = alphaNumeric.charAt(index);

				sb.append(randomChar);
			}

			String randomString = sb.toString();

			code.setCode(randomString);
			codeService.saveCode(code);

			String recipient = code.getPatientID().getEmail();
			String message = "Please click the link to verify your account:\n http://localhost:6969/verifyAccount/"
					+ code.getCode() + "/" + code.getPatientID().getId();
			String subject = "Account verification";

			emailSenderService.sendMailWithoutAttachment(recipient, message, subject);
			messageSenderService.sendMessage(
					"An email has been sent to " + recipient + ". Please click the link to verify your account");

			return "redirect:/";
		}

	}

}