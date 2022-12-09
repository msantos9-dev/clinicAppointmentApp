package biz.global77.clinic.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

	@GetMapping("/dashboard")
	public String dashboard() {

		return "admin/dashboard";
	}

	/**
	 * @param model
	 * @return
	 */
	@GetMapping({ "listOfUser" })
	public String viewUsers(Model model) {
		List<User> listUser = userService.getAllUser();

		model.addAttribute("listUser", listUser);
		return "admin/dashboard";

	}

	@GetMapping("/updateUser/{id}")
	public String editUser(@PathVariable(value = "id") int id, Model model) {

		User User = userService.getUserById(id);
		System.out.println("User" + User);
		model.addAttribute("User", User);

		return "admin/editUser";
	}

	@PostMapping("/saveUser")
	public String saveUser(@Valid User user,
			Errors errors, Model model) {
		if (null != errors && errors.getErrorCount() > 0) {
			System.out.println("Role:" + user.getRole());
			System.out.println("Password:" + user.getConfirmPassword());
			return "redirect:/";
		} else {
			System.out.println("User:" + user);
			userService.saveUser(user);
			return "admin/dashboard";
		}

	}

	/**
	 * @param pageNo
	 * @param sortField
	 * @param sortDir
	 * @param model
	 * @return
	 */
	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable(value = "pageNo") int pageNo, @RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir, Model model) {
		int pageSize = 3;

		Page<User> page = userService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<User> listUser = page.getContent();

		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());

		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

		model.addAttribute("listUser", listUser);
		return "admin/userList";
	}

}
