package net.javaguides.springboot.web;

import net.javaguides.springboot.model.Project;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.service.ProjectService;
import net.javaguides.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @GetMapping("/create")
    public String createProject(Model model) {
        Project project = new Project();
        model.addAttribute("project", project);
        List<User> userList = userService.fetchUserList();
//        projectService.viewInit(model, null);

        return "new-project";
    }

//    @GetMapping({"/", "/list"})
//    public List<Project> fetchProjectList() {
//        return projectService.fetchProjectList();
//    }

    @GetMapping({"/", "/list"})
    public String fetchProjectList(Model model) {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("loggedInUser: " + loggedInUser);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        Timestamp timestampDate = Timestamp.from(Instant.parse(formattedDate + "T00:00:00.00Z"));

        model.addAttribute("projects", projectService.fetchProjectList());
        System.out.println("projects: " + projectService.fetchProjectList());
        model.addAttribute("formattedDate", timestampDate);
        return "user/projects";
//        return projectService.fetchProjectList();
    }

//    @PostMapping("/add")
//    public Project saveProject(Project project) {
//        return projectService.saveProject(project);
//    }

    @GetMapping("/add")
    public String addProject(Model model) {
        model.addAttribute("project", new Project());
        return "user/add-project";
    }

    @PostMapping("/save-project")
    public String saveProject(@ModelAttribute("project") @Valid Project project, HttpSession session) {
        Logger logger = Logger.getLogger(ProjectController.class.getName());
        logger.info("Project: " + project);
        System.out.println("Project: " + project);
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        logger.info("loggedInUser: " + loggedInUser);
        String username = loggedInUser.getName();
        logger.info("username: " + username);
//        User owner = (User) session.getAttribute("user");
//        System.out.println("owner: " + owner);
//        project.setOwner(owner);
        projectService.saveProject(project);
        return "redirect:/project/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);
        return "redirect:/project/list";
    }

    @GetMapping("/details/{id}")
    public String projectDetails(@PathVariable("id") Long id, Model model) {
        Project project = projectService.fetchProjectById(id);
        User owner = project.getOwner();
        Set<User> userList = project.getUsers();
        // find the userNames from userList set
        // name is formed by joining firstName and lastName
        List<String> userNames = userList.stream()
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .collect(Collectors.toList());

//        List<String> userNames = userList.stream().map(user -> user.getFirstName() + " " + user.getLastName()).toList();
        model.addAttribute("project", project);
        model.addAttribute("owner", owner);
        model.addAttribute("userNames", userList);
        return "project-detail";
    }
}