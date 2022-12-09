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
import org.springframework.web.bind.annotation.PostMapping;

import com.twilio.rest.monitor.v1.Alert;

import biz.global77.clinic.model.User;
import biz.global77.clinic.service.UserService;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

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

	@GetMapping("/verifyAccount")
	public String showVerify() {

		return "verifyAccount";
	}

	@PostMapping("/createUser")
	public String createuser(@Valid User user,
			Errors errors, Model model) {

		boolean emailExist = userService.checkEmail(user.getEmail());
		long phoneExist = userService.getAllUser().stream()
				.filter(i -> i.getContactNumber().equals(user.getContactNumber())).count();
		System.out.println("Email exist:" + emailExist);
		System.out.println("Phone exist:" + phoneExist);
		if (null != errors && errors.getErrorCount() > 0) {

			return "register";
		} else {
			if (phoneExist > 0) {
				model.addAttribute("phoneExist", "This phone is already registered");
				return "register";
			} else if (emailExist) {
				model.addAttribute("emailExist", "This email is already registered");
				return "register";
			}
			userService.createUser(user);
			return "redirect:/";
		}

	}

}