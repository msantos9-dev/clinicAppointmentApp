package biz.global77.clinic.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.AppointmentRepository;
import biz.global77.clinic.repository.UserRepository;
import biz.global77.clinic.service.UserService;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Controller
@RequestMapping("/admin")
public class AdminController {

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
	public String home(Model model) {
		long doctors = userRepo.users("ROLE_DOCTOR");
		long nurses = userRepo.users("ROLE_NURSE");
		long patients = userRepo.users("ROLE_USER");
		long appointments = appointmentRepo.appointments();

		model.addAttribute("nurses", nurses);
		model.addAttribute("doctors", doctors);
		model.addAttribute("patients", patients);
		model.addAttribute("appointments", appointments);
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

		User selectedUser = userService.getUserById(id);
		System.out.println("User" + selectedUser);
		model.addAttribute("selectedUser", selectedUser);

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
	public String saveUser(@Valid @ModelAttribute("User") User selectedUser,
			BindingResult bindingResult, Model model) {
		boolean emailExist = userService.checkEmail(selectedUser.getEmail());
		long phoneExist = userService.getAllUser().stream()
				.filter(i -> i.getContactNumber().equals(selectedUser.getContactNumber())).count();
		boolean passwordNotMatch = selectedUser.getPassword().equals(selectedUser.getConfirmPassword());
		// System.out.println("Email exist:" + emailExist);
		// System.out.println("Phone exist:" + phoneExist);
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
		userService.createUser(selectedUser);
		return "redirect:listOfUser";
	}

	@PostMapping("/saveUpdate")
	public String saveUpdate(@ModelAttribute("selectedUser") @Valid User selectedUser,
			Errors errors, Model model) {
		userService.saveUser(selectedUser);
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
