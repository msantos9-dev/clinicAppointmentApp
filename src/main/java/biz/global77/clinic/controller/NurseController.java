package biz.global77.clinic.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
        m.addAttribute("queueList", queueRepo.findByStatusOrderById("queue"));
        m.addAttribute("checkIn", queueRepo.findByStatus("check"));

        return "nurse/home";
    }

    @GetMapping("/appointment")
    public String appointment(ModelMap m, Principal p) {

        Date date = new Date();
        m.addAttribute("listOfAppointments", appointmentRepo.findByStatusAndDate("pending", date));

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

    @RequestMapping(value = "/cancelApptQueue{id}", method = RequestMethod.GET)
    public String cancelApptQueue(@PathVariable("id") int id, @Valid Queue queue, Model m) {

        Appointment appointment = appointmentRepo.findById(id).orElseThrow(IllegalArgumentException::new);
        appointment.setStatus("cancelled");
        appointmentRepo.save(appointment);

        Queue queue2 = queueRepo.findByAppointment(appointment);
        queue2.setStatus("cancel");
        queueRepo.save(queue2);

        return "redirect:/nurse/home";
    }

    @GetMapping("/addUser")
    public String registerUser(Model model) {
        User user = new User();
        model.addAttribute("User", user);
        return "nurse/addUser";
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
        return "redirect:/nurse/home";
    }

    @GetMapping("/check{id}")
    public String updateAppointment(
            @PathVariable("id") int id, Principal p,
            Model m) {

        Queue queue = queueRepo.findById(id).orElse(null);

        queue.setStatus("check");

        queueRepo.save(queue);

        m.addAttribute("queueList", queueRepo.findByStatusOrderById("queue"));
        m.addAttribute("checkIn", queueRepo.findByStatus("check"));

        return "redirect:/nurse/home";

    }

    @GetMapping("/done{id}")
    public String finishAppointment(
            @PathVariable("id") int id, Principal p,
            Model m) {

        Queue queue = queueRepo.findById(id).orElse(null);

        queue.setStatus("done");
        queue.setEndTme(LocalTime.now());

        queueRepo.save(queue);

        m.addAttribute("queueList", queueRepo.findByStatusOrderById("queue"));
        m.addAttribute("checkIn", queueRepo.findByStatus("check"));

        return "redirect:/nurse/home";

    }

    @PostMapping("/processAppointment")
    public String processAppointment(Appointment appointment,
            @ModelAttribute("user") User user, Model model) {

        // User user2 =
        // userRepo.findById(Integer.parseInt(appointment.getPatientsID())).orElse(null);
        User user2 = userRepo.findByEmail(appointment.getPatientsEmail());

        if (user2 == null) {
            model.addAttribute("appointment", appointment);
            return "redirect:/nurse/addAppointment";

        }

        appointment.setPatientID(user2);
        appointment.setStatus("pending");

        appointmentRepo.save(appointment);
        return "redirect:/nurse/appointment";
    }

    @GetMapping("/addAppointment")
    public String setAppointment(Model model) {
        Appointment appointment = new Appointment();
        model.addAttribute("appointment", appointment);
        return "nurse/addAppointment";
    }

}
