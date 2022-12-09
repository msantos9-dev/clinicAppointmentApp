package biz.global77.clinic.controller;

import java.security.Principal;

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
import biz.global77.clinic.model.Queue;
import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.AppointmentRepository;
import biz.global77.clinic.repository.QueueRepository;
import biz.global77.clinic.repository.UserRepository;
import biz.global77.clinic.service.QueueServiceImpl;
import biz.global77.clinic.service.UserService;

@Controller
@RequestMapping("/nurse")
public class NurseController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private QueueRepository queueRepo;

    @Autowired
    private QueueServiceImpl queueService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    private void userDetails(Model m, Principal p) {
        String email = p.getName();
        User user = userRepo.findByEmail(email);

        m.addAttribute("user", user);

        User createUser = new User();

        m.addAttribute("createUser", createUser);

    }

    @GetMapping(value = { "/", "/home" })
    public String home(ModelMap m, Principal p) {
        m.addAttribute("queueList", queueRepo.findAll());

        return "nurse/home";
    }

    @GetMapping("/appointment")
    public String appointment(ModelMap m, Principal p) {

        m.addAttribute("listOfAppointments", appointmentRepo.findByStatus("registered"));

        return "nurse/appointment";
    }

    @RequestMapping(value = "/addToQueue{id}", method = RequestMethod.GET)
    public String addQueue(@PathVariable("id") int id, @Valid Queue queue, Model m, Principal p) {

        Appointment appointment = appointmentRepo.findById(id).orElseThrow(IllegalArgumentException::new);

        String email = p.getName();
        User user = userRepo.findByEmail(email);

        appointment.setStatus("queued");
        appointmentRepo.save(appointment);

        queueService.createQueue(queue, appointment, user);

        return "redirect:/nurse/home";
    }

    @RequestMapping(value = "/cancelAppt{id}", method = RequestMethod.GET)
    public String cancelAppt(@PathVariable("id") int id, @Valid Queue queue, Model m) {

        Appointment appointment = appointmentRepo.findById(id).orElseThrow(IllegalArgumentException::new);

        appointment.setStatus("cancelled");
        appointmentRepo.save(appointment);

        return "redirect:/nurse/appointment";
    }

    @GetMapping("/addUser")
    public String registerUser(ModelMap m, Principal p) {

        return "nurse/addUser";
    }

    @PostMapping("/createUser")
    public String createuser(@Valid User createUser,
            Errors errors, Model model, BindingResult result) {

        boolean emailExist = userService.checkEmail(createUser.getEmail());
        long phoneExist = userService.getAllUser().stream()
                .filter(i -> i.getContactNumber().equals(createUser.getContactNumber())).count();
        // System.out.println("Email exist:" + emailExist);
        // System.out.println("Phone exist:" + phoneExist);
        if (null != errors && errors.getErrorCount() > 0) {
            model.addAttribute("createUser", createUser);
            return "nurse/addUser";
        } else {
            if (phoneExist > 0) {
                model.addAttribute("phoneExist", "This phone is already registered");
                model.addAttribute("createUser", createUser);

                return "nurse/addUser";
            } else if (emailExist) {
                model.addAttribute("emailExist", "This email is already registered");
                model.addAttribute("createUser", createUser);

                return "nurse/addUser";
            }
            userService.createUser(createUser);
            return "redirect:/nurse/home";
        }

    }

}
