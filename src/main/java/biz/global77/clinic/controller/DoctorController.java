package biz.global77.clinic.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import biz.global77.clinic.model.Appointment;
import biz.global77.clinic.model.MedicalCertificate;
import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.AppointmentRepository;
import biz.global77.clinic.repository.MedicalCertificateRepository;
import biz.global77.clinic.repository.UserRepository;
import biz.global77.clinic.service.AppointmentService;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private MedicalCertificateRepository medCertRepo;

	@Autowired
	private AppointmentService appointmentService;

	@ModelAttribute
	private void userDetails(Model m, Principal p) {
		String email = p.getName();
		User user = userRepo.findByEmail(email);

		m.addAttribute("user", user);
	}

	@GetMapping(value = { "/", "/home" })
	public String home(ModelMap m) {

		// m.addAttribute("listOfAppointments",
		// appointmentRepo.findByHasArrived(false));
		m.addAttribute("listOfAppointments", appointmentRepo.findByStatusOrderById("queued"));

		return "doctor/home";
	}

	@GetMapping("/history")
	public String history(ModelMap m, Principal p, @ModelAttribute("user") User user) {

		m.addAttribute("listOfAppointments", appointmentRepo.findByStatusAndDoctorIDOrderById("checked", user));

		return "doctor/history";
	}

	@RequestMapping(value = "/history{id}", method = RequestMethod.GET)
	public String historyParam(@PathVariable("id") int id, ModelMap m) {
		Optional<User> opUser = userRepo.findById(id);
		User user = opUser.get();
		// m.addAttribute("listOfAppointments",
		// appointmentRepo.findByHasArrivedAndPatientID(true, user));
		m.addAttribute("check", "yes");

		m.addAttribute("listOfAppointments", appointmentRepo.findByStatusAndPatientID("checked", user));

		return "doctor/history";
	}

	@RequestMapping(value = "/appointment{id}", method = RequestMethod.GET)
	public String appointment(@PathVariable("id") int id, ModelMap m) {
		appointmentRepo.findById(id).ifPresent(o -> m.addAttribute("appointment", o));
		return "doctor/appointment";
	}

	@RequestMapping(value = "/certificate{id}", method = RequestMethod.GET)
	public String certificate(@PathVariable("id") int id, ModelMap m, MedicalCertificate medCert) {
		appointmentRepo.findById(id).ifPresent(o -> m.addAttribute("appointment", o));
		return "doctor/certificate";
	}

	@GetMapping("/printed")
	public String printed(ModelMap m) {

		return "doctor/print_mc";
	}

	@PostMapping("/updateReport{id}")
	public String updateAppointment(
			@PathVariable("id") int id,
			@Valid Appointment appointment, Errors errors, BindingResult result, Model m) {

		if (result.hasErrors()) {
			appointment.setId(id);
		}

		// appointment.setHasArrived(true);
		appointment.setStatus("checked");
		appointmentRepo.save(appointment);

		// m.addAttribute("listOfAppointments",
		// appointmentRepo.findByHasArrived(false));
		m.addAttribute("listOfAppointments", appointmentRepo.findByStatus("queued"));

		return "redirect:/doctor/home";

	}

	@RequestMapping(value = "/downloadCert{id}", method = RequestMethod.GET)
	public String downloadCert(@Valid MedicalCertificate medicalCertificate, @PathVariable("id") String id,
			BindingResult result, Model m) {

		// medCertRepo.save(medicalCertificate);
		// m.addAttribute("cert", medicalCertificate);
		// medCertRepo.findById(id).ifPresent(o -> m.addAttribute("cert", o));

		// var id = medicalCertificate.getId();

		return "redirect:/doctor/genpdf/" + id;

	}

	@RequestMapping(value = "/genCert", method = RequestMethod.POST)
	public String generateCert(@Valid MedicalCertificate medicalCertificate, BindingResult result, Model m) {

		medCertRepo.save(medicalCertificate);
		// m.addAttribute("cert", medicalCertificate);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL d, YYYY");
		m.addAttribute("cert", medicalCertificate);

		m.addAttribute("id", medicalCertificate.getId());
		m.addAttribute("name", medicalCertificate.getPatientID().getFullName());
		m.addAttribute("address", medicalCertificate.getPatientID().getAddress());
		m.addAttribute("date", medicalCertificate.getDate().format(formatter));
		m.addAttribute("reason", medicalCertificate.getReason());
		m.addAttribute("doctor", medicalCertificate.getDoctorID().getFullName());
		m.addAttribute("genDateTime", LocalDateTime.now().format(formatter));

		// var id = medicalCertificate.getId();

		return "doctor/preview";

	}

}