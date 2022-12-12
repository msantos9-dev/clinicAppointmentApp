package biz.global77.clinic.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import biz.global77.clinic.model.Appointment;
import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.AppointmentRepository;
import biz.global77.clinic.repository.UserRepository;
import biz.global77.clinic.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private UserService userService;

	@ModelAttribute
	private void userDetails(Model m, Principal p) {
		String email = p.getName();
		User user = userRepo.findByEmail(email);

		m.addAttribute("user", user);
	}

	@GetMapping("/")
	public String home(User user) {
		System.out.println(user.isEnabled());
		return "user/home";
	}

	@GetMapping("/profile")
	public String userProfile() {

		return "user/profile";
	}

	@GetMapping("/editprofile")
	public String userEditProfile() {
		return "user/editprofile";
	}

	@GetMapping("appointment")
	public String getAppointment(Model model, @ModelAttribute("user") User user) {
		List<Appointment> appointments = appointmentRepo.findByPatientID(user);
		model.addAttribute("appointments", appointments);
		return "user/appointment";
	}

	@GetMapping("/set-appointment")
	public String setAppointment(Model model) {
		Appointment appointment = new Appointment();
		model.addAttribute("appointment", appointment);
		return "user/setAppointment";
	}

	@PostMapping("/process-appointment")
	public String processAppointment(Appointment appointment, @ModelAttribute("user") User user) {
		appointment.setPatientID(user);
		appointmentRepo.save(appointment);
		return "redirect:/user/appointment";
	}

	@GetMapping("confirm-appointment")
	public String getConfirmAppointment() {
		return "user/confirmAppointment";
	}
}