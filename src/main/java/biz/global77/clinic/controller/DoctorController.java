package biz.global77.clinic.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.UserRepository;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
    @Autowired
	private UserRepository userRepo;

	@ModelAttribute
	private void userDetails(Model m, Principal p) {
		String email = p.getName();
		User user = userRepo.findByEmail(email);

		m.addAttribute("user", user);
	}

    @GetMapping("/")
    public String home() {
        return "doctor/home";
    }
}
