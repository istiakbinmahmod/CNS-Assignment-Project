package net.javaguides.springboot.web;

import net.javaguides.springboot.service.ProjectService;
import net.javaguides.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.logging.Logger;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

//    @GetMapping({"/login", "/"})
//    public String login(Model model) {
//        model.addAttribute("loginInfo", new LoginInfo());
//        return "login";
//    }

//    @PostMapping("/login")
//    public String login(@ModelAttribute("loginInfo") @Valid LoginInfo info, Model model, HttpSession session) {
//        System.out.println("info: " + info);
////        System.out.println("email: " + user.getEmail());
////        System.out.println("password: " + user.getPassword());
//        User val_user = userService.login(info.getEmail(), info.getPassword());
//        System.out.println("val_user: " + val_user);
//
//        if (val_user != null) {
//            session.setAttribute("user", val_user);
//            return "redirect:/project/list";
//        } else {
//            model.addAttribute("error", "Invalid Credentials");
//            return "login";
//        }
//    }

//    @GetMapping("/register")
//    public String register(Model model) {
//        model.addAttribute("newUser", new User());
//        System.out.println("User: " + new Project());
//        return "register";
//    }

//    @PostMapping("/save-user")
//    public String saveUser(@ModelAttribute("newUser") @Valid User user, HttpSession session) {
//        Logger logger = Logger.getLogger(UserController.class.getName());
//        logger.info("User: " + user);
//        userService.saveUser(user);
//        session.setAttribute("user", user);
//        return "redirect:/user/login";
//    }

//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        System.out.println(session.getAttribute("user"));
//        session.invalidate();
//        System.out.println(session.getAttribute("user"));
//        return "redirect:/user/login";
//    }

    @GetMapping("/list")
    public String fetchUserList(Model model) {
        model.addAttribute("userList", userService.fetchUserList());
        return "user-list";
    }

//    @GetMapping("/{id}")
//    public User fetchUserById(@PathVariable("id") Long userId) {
//        return userService.fetchUserById(userId);
//    }
}