package com.example.laborhiring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.laborhiring.model.Labour;
import com.example.laborhiring.model.User;
import com.example.laborhiring.repository.LabourRepository;
import com.example.laborhiring.repository.UserRepository;
import com.example.laborhiring.service.EmailService;
import com.example.laborhiring.service.UserService;

@Controller
public class UserController {

    @Autowired
    UserService uService;

    @Autowired
    UserRepository uRepo;

    @Autowired
    EmailService eService;

    @Autowired
    LabourRepository lRepo;

    @GetMapping("/getUserPage")
    public String getUserPage() {
        return "User/getUserPage";
    }

    @PostMapping("/add-User-Details")
    public String addUsers(@ModelAttribute User users) {
        User u = validUser(users);
        if (u.getEmail() == null) {
            return "User/alreadyExistUser";
        } else {
            uService.adduser(u);
        }
        return "User/login";
    }

    private User validUser(User users) {
        String email = users.getEmail();
        List<User> list = uRepo.getByEmailUser(email);
        User error = new User();
        if (!list.isEmpty()) {
            return error;
        }
        return users;
    }

    @GetMapping("/get-user-page")
    public String backToUserPage() {
        return "User/getUserPage";
    }

    @GetMapping("/get-user-login")
    public String userLogin() {
        return "User/UserLogin";
    }

    @GetMapping("/user-forgotPassword")
    public String userPassword() {
        return "User/userForgotPassword";
    }

    @PostMapping("/user-send-mail")
    public String getEmail(@ModelAttribute User mail) {
        String sendMail = mail.getEmail();
        List<User> list = uRepo.getByEmailUser(sendMail);
        if (list.isEmpty()) {
            return "User/enterValid";
        }
        String password = list.get(0).getPassword();
        eService.sendMailPass(sendMail, password);
        return "User/userLogin";

    }

    static String email = "";

    @PostMapping("/user-email-password")
    public String checkMailPassword(@ModelAttribute("details") User details, User lab, Model model) {
        email = details.getEmail();
        String pass = details.getPassword();
        List<User> list = uRepo.getByEmailUser(email);
        if (list.isEmpty()) {
            return "User/userLoginValidation";
        } else {
            if (pass.equals(list.get(0).getPassword())) {
                User l = uRepo.getByEmailUserList(email);
                model.addAttribute("UserPage", l);
                return "User/UserPage";
            }
        }
        return "User/userLoginValidation";
    }

    @PostMapping("/User-edit-details")
    public String editDetails(@ModelAttribute @RequestBody User details) {
        String name = details.getName();
        String phNum = details.getPhNum();
        String mail = details.getEmail();
        String location = details.getLocation();

        User lab = uRepo.getByEmailUserList(email);
        if (name != "") {
            lab.setName(name);
        } else {
            lab.setName(lab.getName());
        }

        if (phNum != "") {
            lab.setPhNum(phNum);
        } else {
            lab.setPhNum(lab.getPhNum());
        }

        if (mail != "") {
            lab.setEmail(mail);
        } else {
            lab.setEmail(lab.getEmail());
        }

        if (location != "") {
            lab.setLocation(location);
        } else {
            lab.setLocation(lab.getLocation());
        }

        uService.adduser(lab);
        return "redirect:/get-user-login";

    }

    @PostMapping("/User-change-password")
    public String changePassword(@ModelAttribute @RequestBody User pass) {
        String newPass = pass.getPassword();
        User lab = uRepo.getByEmailUserList(email);
        lab.setPassword(newPass);
        uService.adduser(lab);
        return "redirect:/get-user-login";
    }

    @GetMapping("/user-changePassword")
    public String changePassword() {
        return "User/UserChangePassword";
    }

    @GetMapping("/user-editDetails")
    public String editDetails() {
        return "User/UserEditPage";
    }

    @GetMapping("/MainPage")
    public String mainPage() {
        return "mainPage";
    }

    @PostMapping("/getDetails")
    public String getDetails(Model model) {
        User l = uRepo.getByEmailUserList(email);
        model.addAttribute("UserPage", l);
        return "userPersonalDetails";
    }

    @PostMapping("/getLabDetails")
    public String getLabDetails(@ModelAttribute Labour labour, Model model) {
        String location = labour.getLocation();
        String skill = labour.getSkill();

        List<Labour> l = lRepo.getLabour(location, skill);
        model.addAttribute("l", l);
        return "labDetailsToUser";
    }

    @PostMapping("view-labour-details")
    public String getDetailsByid(@ModelAttribute Labour labId, Model model) {
        Labour labDetails = lRepo.getLabById(labId.getId());
        Integer count = labDetails.getCount();
        labDetails.setCount(count + 1);
        lRepo.save(labDetails);
        model.addAttribute("labDetails", labDetails);
        return "workerDetails";
    }

    @GetMapping("/forgotuser")
    public String forgotPassword() {
        return "User/userForgotPassword";
    }
}
