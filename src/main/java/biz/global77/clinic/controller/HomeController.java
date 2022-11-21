package biz.global77.clinic.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import biz.global77.clinic.model.User;
import biz.global77.clinic.service.UserService;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String index() {
		
		return "index";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@PostMapping("/createUser")
	public String createuser(@ModelAttribute User user, HttpSession session) {

		boolean f = userService.checkEmail(user.getEmail());

		if (f) {
			session.setAttribute("msg", "Email Id already exists");

			return "redirect:/register";
		} else {
			User userDtls = userService.createUser(user);
			if (userDtls != null) {
				session.setAttribute("msg", "Register Successfully");

			} else {
				session.setAttribute("msg", "Something wrong on server");
			}
		}

		return "redirect:/";
	}

}