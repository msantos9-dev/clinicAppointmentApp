package biz.global77.clinic.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
	private UserService userService;

	@ModelAttribute
	private void userDetails(Model m, Principal p) {
		String email = p.getName();
		User user = userRepo.findByEmail(email);

		m.addAttribute("user", user);
	}

	@GetMapping("/")
	public String home() {
		return "admin/home";
	}

	// @GetMapping("/addUser")
	// public String addUser(Model model) {
	// User user = new User();
	// model.addAttribute("User", user);
	// return "admin/addUser";
	// }

	@GetMapping("/addUser")
	public String addUser(Model model) {
		User user = new User();
		model.addAttribute("User", user);
		return "admin/addUser";
	}

	/**
	 * @param model
	 * @return
	 */

	@GetMapping({ "listOfUser" })
	public String viewUsers(Model model, @Param("keyword") String keyword) {

		List<User> listUser = userService.findWithSearch(keyword).stream().sorted(Comparator.comparing(User::getId))
				.collect(Collectors.toList());
		model.addAttribute("listUser", listUser);
		model.addAttribute("keyword", keyword);

		// return "admin/dashboard";

		return findPaginated(1, "id", "asc", model);
	}

	// @GetMapping("{listOfUser}")
	// public String viewHomePage(Model model) {
	// return findPaginated(1, "id", "asc", model);
	// }

	@GetMapping("/updateUser/{id}")
	public String editUser(@PathVariable(value = "id") int id, Model model) {

		User selectedUser = userService.getUserById(id);
		System.out.println("User" + selectedUser);
		model.addAttribute("selectedUser", selectedUser);

		return "admin/editUser";
	}

	@PostMapping("/saveUser")
	public String saveUser(@Valid @ModelAttribute("User") User selectedUser,
			Errors errors, Model model) {
		// boolean emailExist = userService.checkEmail(selectedUser.getEmail());
		// long phoneExist = userService.getAllUser().stream()
		// .filter(i ->
		// i.getContactNumber().equals(selectedUser.getContactNumber())).count();
		// System.out.println("Email exist:" + emailExist);
		// System.out.println("Phone exist:" + phoneExist);
		if (errors.hasErrors()) {
			System.out.println("Role:" + selectedUser.getRole());
			System.out.println("Password:" + selectedUser.getConfirmPassword());
			System.out.println(selectedUser);
			return "redirect:addUser";
		} else
			System.out.println("User:" + selectedUser);
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
			@RequestParam("sortDir") String sortDir, Model model) {
		int pageSize = 10;

		Page<User> page = userService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<User> listUser = page.getContent();
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("listUser", listUser);
		return "admin/dashboard";
	}

}
