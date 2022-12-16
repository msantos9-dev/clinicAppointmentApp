package biz.global77.clinic.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.Validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import biz.global77.clinic.model.Appointment;
import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.AppointmentRepository;
import biz.global77.clinic.repository.UserRepository;
import biz.global77.clinic.service.EmailSenderService;
import biz.global77.clinic.service.UserService;
import biz.global77.clinic.service.UserServiceImpl;
import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private BCryptPasswordEncoder passwordEncode;

	@Autowired
	private EmailSenderService emailSenderService;

	@ModelAttribute
	private void userDetails(Model m, Principal p) {
		String email = p.getName();
		User user = userRepo.findByEmail(email);

		m.addAttribute("user", user);
	}

	@GetMapping("/")
	public String home(User user) {
		System.out.println(user.isEnabled());
		return "redirect:/user/appointment";
		// return "user/home";
	}

	@GetMapping("/profile")
	public String userProfile(@ModelAttribute("user") User user) {

		return "user/profile";
	}

	@GetMapping("/editprofile")
	public String userEditProfile(Model model, @ModelAttribute("user") User user) {
		User edituser = new User();
		edituser.setPassword("");
		edituser.setConfirmPassword("");
		model.addAttribute("edituser", edituser);

		return "user/editprofile";
	}

	@GetMapping("/editpassword")
	public String userEditPassword(Model model, @ModelAttribute("user") User user) {

		User edituser = user;
		edituser.setPassword("");
		edituser.setConfirmPassword("");
		model.addAttribute("edituser", edituser);

		return "user/editpassword";
	}

	@PostMapping("/savePassword")
	public String savePassword(@ModelAttribute("edituser") User user,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addAttribute("id", user.getId());
			redirectAttributes.addFlashAttribute("editUser", user);
			return "redirect:/user/editpassword";
		}

		User currentUser = user;
		currentUser.setPassword(user.getPassword());
		currentUser.setConfirmPassword(user.getConfirmPassword());

		user = userRepo.findByEmail(currentUser.getEmail());
		user.setPassword(passwordEncode.encode(currentUser.getPassword()));
		user.setConfirmPassword(passwordEncode.encode(currentUser.getConfirmPassword()));
		userServiceImpl.saveUser(user);

		String recipient = user.getEmail();
		String message = "Hi Ms/Mr. " + user.getFullName()
				+ ", this is the Love Clinic and we are notifying that you the update on your account has been successful!";
		String subject = "Account Update";
		try {
			emailSenderService.sendMailWithoutAttachment(recipient, message, subject);
		} catch (MessagingException e) {

			e.printStackTrace();
		}

		return "redirect:/user/profile";

	}

	@PostMapping("/saveUpdate")
	public String saveUpdate(@ModelAttribute("user") @Valid User user,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			redirectAttributes.addAttribute("id", user.getId());
			redirectAttributes.addFlashAttribute("user", user);
			return "redirect:/user/editprofile";
		}
		userServiceImpl.saveUser(user);
		String recipient = user.getEmail();
		String message = "Hi Ms/Mr. " + user.getFullName()
				+ ", this is the Love Clinic and we are notifying that you the update on your account has been successful!";
		String subject = "Account Update";
		try {
			emailSenderService.sendMailWithoutAttachment(recipient, message, subject);
		} catch (MessagingException e) {

			e.printStackTrace();
		}

		return "redirect:/user/profile";
	}

	// Appointment
	@GetMapping({ "appointment" })
	public String getAppointment(Model model, @ModelAttribute("user") User user,
			@Param("keyword") String keyword) {

		model.addAttribute("keyword", keyword);

		List<Appointment> appointments = appointmentRepo.findByPatientID(user);

		if (keyword == null) {
			appointments = appointments
					.stream()
					.filter(appointment -> (appointment.getStatus().equals("Pending")) ||
							(appointment.getStatus().equals("Queued")))
					.collect(Collectors.toList());
		} else {
			appointments = appointments
					.stream()
					.filter(appointment -> (appointment.getStatus().equals(keyword)))
					.collect(Collectors.toList());
		}

		Collections.sort(appointments, new Comparator<Appointment>() {
			public int compare(Appointment o1, Appointment o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});

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
		String recipient = user.getEmail();
		String message = "Hi Ms/Mr. " + user.getFullName()
				+ ", this is the Love Clinic and we are notifying that you have successfully booked an appointment. Kindly wait for an approval regarding your appointment.";
		String subject = "Appointment creation";
		try {
			emailSenderService.sendMailWithoutAttachment(recipient, message, subject);
		} catch (MessagingException e) {

			e.printStackTrace();
		}

		return "redirect:/user/appointment";
	}

	@GetMapping("/confirm-appointment")
	public String getConfirmAppointment() {
		return "user/confirmAppointment";
	}

	@GetMapping("/cancel-appointment/{id}")
	public String getCancelAppointment(@PathVariable(value = "id") int id, Model model) {

		Appointment appointment = appointmentRepo.findById(id);
		model.addAttribute("cancelAppointment", appointment);

		appointment.setStatus("Cancelled");
		appointmentRepo.save(appointment);
		return "redirect:/user/appointment";
	}

}