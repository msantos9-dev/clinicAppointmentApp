package biz.global77.clinic.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import biz.global77.clinic.service.UserService;

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

	// @GetMapping("/register")
	// public String register() {
	// return "register";
	// }

	@GetMapping("/register")
	public String showForm(User user) {

		return "register";
	}

	@GetMapping("/verifyAccount/{code}/{id}")
	public String showVerify(@PathVariable(value = "code") String code, @PathVariable(value = "id") int id, User user,
			Model model) {

		long codeExist = codeService.getAllCode().stream()
				.filter(i -> i.getCode().equals(code)).count();
		User selectedUser = userService.getUserById(id);

		if (codeExist > 0) {
			System.out.println("codeExist");

		} else {
			System.out.println("invalid code");
		}
		return "verifyAccount";
	}

	@PostMapping("/createUser")
	public String createuser(@Valid User user, Code code,
			Errors errors, Model model) {

		boolean emailExist = userService.checkEmail(user.getEmail());
		boolean passwordNotMatch = user.getPassword().equals(user.getConfirmPassword());
		long phoneExist = userService.getAllUser().stream()
				.filter(i -> i.getContactNumber().equals(user.getContactNumber())).count();
		System.out.println("Email exist:" + emailExist);
		System.out.println("Phone exist:" + phoneExist);
		System.out.println("Password match:" + passwordNotMatch);
		if (null != errors && errors.getErrorCount() > 0) {

			return "register";
		} else {
			if (phoneExist > 0) {
				model.addAttribute("phoneExist", "This phone is already registered");
				return "register";
			} else if (emailExist) {
				model.addAttribute("emailExist", "This email is already registered");
				return "register";
			} else if (!passwordNotMatch){
				model.addAttribute("passwordNotMatch", "Password do not match");
				return "register";
			}
			userService.createUser(user);
			code.setPatientID(user);

			// create a string of uppercase and lowercase characters and numbers
			String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
			String numbers = "0123456789";

			// combine all strings
			String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

			// create random string builder
			StringBuilder sb = new StringBuilder();

			// create an object of Random class
			Random random = new Random();

			// specify length of random string
			int length = 30;

			for (int i = 0; i < length; i++) {

				// generate random index number
				int index = random.nextInt(alphaNumeric.length());

				// get character specified by index
				// from the string
				char randomChar = alphaNumeric.charAt(index);

				// append the character to string builder
				sb.append(randomChar);
			}

			String randomString = sb.toString();

			code.setCode(randomString);
			codeService.saveCode(code);

			try {
				final String username = "loveclinicphilippinesofficial@gmail.com";// email id of sender
				final String password = "karsjazvgbmgftvk";// application password of Gmail , I dont know how to
															// generate
															// watch
															// this https://bit.ly/3PY4IeS
				Properties props = new Properties();
				props.put("mail.smtp.auth", true);
				props.put("mail.smtp.starttls.enable", true);
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.port", "587");

				Session session = Session.getInstance(props,
						new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(username, password);
							}
						});
				// Create a default MimeMessage object.
				Message message = new MimeMessage(session);

				// Set From: header field of the header.
				message.setFrom(new InternetAddress(username));

				// Set To: header field of the header.
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(code.getPatientID().getEmail()));

				// Set Subject: header field
				message.setSubject("Love Clinic Account Verification Email");

				// Create the message part
				BodyPart messageBodyPart = new MimeBodyPart();

				// Now set the actual message
				messageBodyPart
						.setText("Please click the link to verify your account:\n http://localhost:6969/verifyAccount/"
								+ code.getCode() + "/" + code.getPatientID().getId());

				// Create a multipar message
				Multipart multipart = new MimeMultipart();

				// Set text message part
				multipart.addBodyPart(messageBodyPart);

				// Part two is attachment
				messageBodyPart = new MimeBodyPart();

				// Send the complete message parts
				message.setContent(multipart);

				Transport.send(message);

				System.out.println("Sent message successfully....");

			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "redirect:/";
		}

	}

}