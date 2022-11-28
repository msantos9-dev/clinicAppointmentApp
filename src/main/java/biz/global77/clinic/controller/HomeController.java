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

	@PostMapping("/createUser")
	public String createuser(@Valid User user,
			Errors errors, Model model) {

		boolean f = userService.checkEmail(user.getEmail());

		if (null != errors && errors.getErrorCount() > 0) {

			return "register";
		} else {

			return "redirect:/";
		}

	}

}