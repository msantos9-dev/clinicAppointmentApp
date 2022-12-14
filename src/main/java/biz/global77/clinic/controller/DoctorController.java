package biz.global77.clinic.controller;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import biz.global77.clinic.service.EmailSenderService;

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

	@Autowired
	private EmailSenderService emailSenderService;

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
			@PathVariable("id") int id, Principal p,
			@Valid Appointment appointment, Errors errors, BindingResult result, Model m) {

		String email = p.getName();
		User user = userRepo.findByEmail(email);

		// if (result.hasErrors()) {
		// appointment.setId(id);
		// }
		Appointment updateAppt = appointmentRepo.findById(id).orElse(null);
		updateAppt.setStatus("checked");
		updateAppt.setDoctorID(user);
		updateAppt.setDiagnosis(appointment.getDiagnosis());

		appointmentRepo.save(updateAppt);

		m.addAttribute("listOfAppointments", appointmentRepo.findByStatus("queued"));

		return "redirect:/doctor/home";

	}

	@RequestMapping(value = "/downloadCert{id}", method = RequestMethod.GET)
	public String downloadCert(@Valid MedicalCertificate medicalCertificate, @PathVariable("id") String id,
			BindingResult result, Model m) throws MessagingException {

		// MedicalCertificate info =
		// medCertRepo.findById(Integer.parseInt(id)).orElse(null);
		// String email = info.getPatientID().getEmail();
		// String fileName = info.getPatientID().getFullName().replace(" ", "")
		// + "_" + info.getId()
		// + ".pdf";
		// emailSenderService.sendMailWithAttachment(email,
		// "C:/Users/rarenilloadmin/Downloads/" + fileName);

		return "redirect:/doctor/genpdf/" + id;

	}

	@RequestMapping(value = "/sendToEmail{id}", method = RequestMethod.GET)
	public ResponseEntity<Void> sendToEmail(@Valid MedicalCertificate medicalCertificate, @PathVariable("id") String id,
			BindingResult result, Model m) throws MessagingException {

		MedicalCertificate info = medCertRepo.findById(Integer.parseInt(id)).orElse(null);
		String email = info.getPatientID().getEmail();
		String fileName = info.getPatientID().getFullName().replace(" ", "")
				+ "_" + info.getId()
				+ ".pdf";
		emailSenderService.sendMailWithAttachment(email,
				"C:/Users/rarenilloadmin/Downloads/" + fileName);
		// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL d, YYYY");

		// m.addAttribute("cert", medicalCertificate);

		// m.addAttribute("id", info.getId());
		// m.addAttribute("name", info.getPatientID().getFullName());
		// m.addAttribute("address", info.getPatientID().getAddress());
		// m.addAttribute("date", info.getDate());
		// m.addAttribute("reason", info.getReason());
		// m.addAttribute("doctor", info.getDoctorID().getFullName());
		// m.addAttribute("genDateTime", LocalDateTime.now().format(formatter));

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	}

	@RequestMapping(value = "/genCert", method = RequestMethod.POST)
	public String generateCert(@Valid MedicalCertificate medicalCertificate, BindingResult result, Principal p,
			Model m) {

		String email = p.getName();
		User user = userRepo.findByEmail(email);

		medicalCertificate.setDoctorID(user);
		medCertRepo.save(medicalCertificate);
		// m.addAttribute("cert", medicalCertificate);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL d, YYYY");
		m.addAttribute("cert", medicalCertificate);

		m.addAttribute("id", medicalCertificate.getId());
		m.addAttribute("name", medicalCertificate.getPatientID().getFullName());
		m.addAttribute("address", medicalCertificate.getPatientID().getAddress());
		m.addAttribute("date", medicalCertificate.getDate());
		m.addAttribute("reason", medicalCertificate.getReason());
		m.addAttribute("doctor", medicalCertificate.getDoctorID().getFullName());
		m.addAttribute("genDateTime", LocalDateTime.now().format(formatter));

		// var id = medicalCertificate.getId();

		return "doctor/preview";

	}

}