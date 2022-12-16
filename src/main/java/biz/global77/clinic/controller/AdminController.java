package biz.global77.clinic.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.thymeleaf.expression.Lists;

import biz.global77.clinic.model.Activity;
import biz.global77.clinic.model.Appointment;
import biz.global77.clinic.model.Code;
import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.ActivityRepository;
import biz.global77.clinic.repository.AppointmentRepository;
import biz.global77.clinic.repository.UserRepository;
import biz.global77.clinic.service.CodeService;
import biz.global77.clinic.service.EmailSenderService;
import biz.global77.clinic.service.UserService;
import biz.global77.clinic.service.UserServiceImpl;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.messaging.Redirect;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private ActivityRepository activityRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private CodeService codeService;

	@Autowired
	private EmailSenderService emailSenderService;

	@Autowired
	private UserServiceImpl userServiceImpl;

	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	LocalDate now = LocalDate.now();

	@ModelAttribute
	private void userDetails(Model m, Principal p) {
		String email = p.getName();
		User user = userRepo.findByEmail(email);
		m.addAttribute("user", user);
	}

	@GetMapping("/")
	public String home(Model model) {
		long doctors = userRepo.users("ROLE_DOCTOR");
		long nurses = userRepo.users("ROLE_NURSE");
		long patients = userRepo.users("ROLE_USER");
		long appointmentsCount = appointmentRepo.appointments();
		List<Activity> activities = activityRepo.findAll();

		Collections.sort(activities, new Comparator<Activity>() {
			@Override
			public int compare(Activity o1, Activity o2) {
				return o2.getId() - o1.getId();
			}
		});

		activities = activities.stream().limit(4).collect(Collectors.toList());
		model.addAttribute("activities", activities);
		model.addAttribute("nurses", nurses);
		model.addAttribute("doctors", doctors);
		model.addAttribute("patients", patients);
		model.addAttribute("appointmentsCount", appointmentsCount);
		return "admin/home";
	}

	@GetMapping("/addUser")
	public String addUser(Model model) {
		model.addAttribute("User", new User());
		return "/admin/addUser";
	}

	/**
	 * @param model
	 * @return
	 */

	@GetMapping({ "listOfUser" })
	public String viewUsers(Model model, @Param("keyword") String keyword) {
		keyword = keyword != null ? keyword.toUpperCase() : "";
		return findPaginated(1, "id", "asc", keyword, model);
	}

	@GetMapping("/updateUser/{id}")
	public String editUser(@PathVariable(value = "id") int id, Model model) {

		if (!model.containsAttribute("selectedUser")) {
			User selectedUser = userService.getUserById(id);
			model.addAttribute("selectedUser", selectedUser);
		}

		return "admin/editUser";
	}

	@GetMapping("/profile/{id}")
	public String profile(@PathVariable(value = "id") int id, Model model) {
		User selectedUser = userService.getUserById(id);
		System.out.println("User" + selectedUser);
		model.addAttribute("selectedUser", selectedUser);
		return "admin/profile";
	}

	@PostMapping("/saveUser")
	public String saveUser(@Valid @ModelAttribute("User") User selectedUser, Code code,
			BindingResult bindingResult, Model model) {
		boolean emailExist = userService.checkEmail(selectedUser.getEmail());
		long phoneExist = userService.getAllUser().stream()
				.filter(i -> i.getContactNumber().equals(selectedUser.getContactNumber())).count();
		boolean passwordNotMatch = selectedUser.getPassword().equals(selectedUser.getConfirmPassword());

		if (bindingResult.hasErrors()) {
			System.out.println("Role:" + selectedUser.getRole());
			System.out.println("Password:" + selectedUser.getConfirmPassword());
			System.out.println(selectedUser);
			return "admin/addUser";
		} else {
			if (phoneExist > 0) {
				model.addAttribute("phoneExist", "This phone is already registered");
				return "admin/addUser";
			} else if (emailExist) {
				model.addAttribute("emailExist", "This email is already registered");
				return "admin/addUser";
			} else if (!passwordNotMatch) {
				model.addAttribute("passwordNotMatch", "Password do not match");
				return "admin/addUser";
			}
			System.out.println("User:" + selectedUser);
		}
		userServiceImpl.createUser(selectedUser);
		code.setPatientID(selectedUser);

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

		String recipient = code.getPatientID().getEmail();
		String message = "Please click the link to verify your account:\n http://localhost:6969/verifyAccount/"
				+ code.getCode() + "/" + code.getPatientID().getId();
		String subject = "Account verification";
		try {
			emailSenderService.sendMailWithoutAttachment(recipient, message, subject);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Activity activity = new Activity();
		activity.setUserId(selectedUser.getId());
		activity.setAction("Created new account");
		activity.setMessage("New account for " + selectedUser.getRole().toUpperCase().toString().substring(5) + " "
				+ selectedUser.getFullName() + " has been created.");
		activity.setSendDate(now);
		activityRepo.save(activity);
		return "redirect:listOfUser";
	}

	@PostMapping("/saveUpdate")
	public String saveUpdate(@ModelAttribute("selectedUser") @Valid User selectedUser,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addAttribute("id", selectedUser.getId());
			redirectAttributes.addFlashAttribute("selectedUser", selectedUser);
			return "redirect:/admin/updateUser/{id}";
		}
		userServiceImpl.saveUser(selectedUser);
		String recipient = selectedUser.getEmail();
		String message = "Hi Ms/Mr. " + selectedUser.getFullName()
				+ ", this is the Love Clinic and we are notifying that you the update on your account has been successful!";
		String subject = "Account Update";
		try {
			emailSenderService.sendMailWithoutAttachment(recipient, message, subject);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Activity activity = new Activity();
		activity.setUserId(selectedUser.getId());
		activity.setAction("Updated account");
		activity.setSendDate(now);
		activity.setMessage(selectedUser.getRole().toUpperCase().toString().substring(5) + " "
				+ selectedUser.getFullName() + " had an update on his/her account.");
		activityRepo.save(activity);
		return "redirect:listOfUser";
	}

	/**
	 * @param pageNo
	 * @param sortField
	 * @param sortDir
	 * @param model
	 * @return
	 */
	@GetMapping("page/{pageNo}")
	public String findPaginated(@PathVariable(value = "pageNo") int pageNo, @RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir, @Param("keyword") String keyword, Model model) {
		int pageSize = 7;
		Page<User> pages = userService.findPaginated(keyword != null ? keyword.toUpperCase() : "", pageNo, pageSize,
				sortField, sortDir);
		List<User> listUser = pages.getContent();
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", pages.getTotalPages());
		model.addAttribute("totalItems", pages.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("listUser", listUser);
		model.addAttribute("keyword", keyword);

		return "admin/dashboard";
	}

}
