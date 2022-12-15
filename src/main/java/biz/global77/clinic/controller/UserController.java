package biz.global77.clinic.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional;

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
	public String userProfile(@ModelAttribute("user") User user) {

		return "user/profile";
	}

	@GetMapping("/editprofile")
	public String userEditProfile() {
		return "user/editprofile";
	}

	// Appointment
	@GetMapping("/appointment")
	public String getAppointment(Model model, @ModelAttribute("user") User user) {
		List<Appointment> appointments = appointmentRepo.findByPatientID(user);

		appointments = appointments
				.stream()
				.filter(appointment -> (appointment.getStatus().equals("Pending") ||
						appointment.getStatus().equals("Queued")))
				.collect(Collectors.toList());

		Collections.sort(appointments, new Comparator<Appointment>() {
			public int compare(Appointment o1, Appointment o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});

		// Collections.reverse(appointments);
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
		appointment.setStatus("Pending");
		appointmentRepo.save(appointment);
		return "redirect:/user/appointment";
	}

	@GetMapping("/confirm-appointment")
	public String getConfirmAppointment() {
		return "user/confirmAppointment";
	}

	@GetMapping("/cancel-appointment/{id}")
	public String getCancelAppointment(@PathVariable(value = "id") Long id, Model model) {

		Appointment appointment = appointmentRepo.findById(id);
		model.addAttribute("cancelAppointment", appointment);

		appointment.setStatus("Cancelled");
		appointmentRepo.save(appointment);
		return "redirect:/user/appointment";
	}

}