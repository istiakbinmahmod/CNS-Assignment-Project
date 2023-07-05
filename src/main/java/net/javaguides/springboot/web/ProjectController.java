package net.javaguides.springboot.web;

import net.javaguides.springboot.model.Project;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.service.ProjectService;
import net.javaguides.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @GetMapping("/add")
    public String createProject(Model model) {
        System.out.println("------------------");
        System.out.println("Creating Project");
        System.out.println("Current user: " + SecurityContextHolder.getContext().getAuthentication().getName());
        Project project = new Project();
        model.addAttribute("project", project);
        List<User> userList = userService.fetchUserList();
        model.addAttribute("userList", userList);
        System.out.println("------------------");
        return "user/add-project";
    }


    @GetMapping({"/", "/list"})
    public String fetchProjectList(Model model) {
        System.out.println("------------------");
        System.out.println("Fetching Project List");
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = userService.fetchUserByEmail(loggedInUser.getName()).getUserId();
//        System.out.println("loggedInUser: " + loggedInUser);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        Timestamp timestampDate = Timestamp.from(Instant.parse(formattedDate + "T00:00:00.00Z"));

        model.addAttribute("projects", projectService.fetchProjectList());
        model.addAttribute("loggedInUser", currentUserId);
        System.out.println("currentUserId: " + currentUserId);
        for (Project p : projectService.fetchProjectList()) {
            System.out.println("Project: " + p);
        }
        model.addAttribute("formattedDate", timestampDate);
        System.out.println("------------------");
        return "user/projects";
    }

    @GetMapping("/filter")
    public String filterProjects(@RequestParam("filterStartDate") String startDate,
                                 @RequestParam("filterEndDate") String endDate,
                                 Model model) {
        System.out.println("------------------");
        System.out.println("Fetching Filtered Project List");
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = userService.fetchUserByEmail(loggedInUser.getName()).getUserId();
        System.out.println("loggedInUser: " + loggedInUser);
        System.out.println("currentUserId: " + currentUserId);
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);
        Date start = new Date();
        Date end = new Date();
        try {
            start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
        } catch (Exception e) {
            System.out.println("Error parsing dates");
        }
        model.addAttribute("projects", projectService.fetchProjectListFiltered(start, end));
        model.addAttribute("loggedInUser", currentUserId);
        System.out.println("------------------");
//        return "redirect:/project/list";
        return "user/projects";
    }

    @PostMapping("/save-project")
    public String saveProject(@ModelAttribute("project") @Valid Project project,
                              @RequestParam("selectedUsers") List<User> selectedUsers,
                              HttpSession session) {
        Logger logger = Logger.getLogger(ProjectController.class.getName());
        System.out.println("Saving Project");
        System.out.println("selectedUsers: " + selectedUsers);
        System.out.println("Project: " + project);

        // Set project owner
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        User owner = userService.fetchUserByEmail(loggedInUser.getName());
        System.out.println("owner: " + owner);
        project.setOwner(owner);

        // Set project users
        Set<User> users = new HashSet<>(selectedUsers);
        project.setUsers(users);

        // Set project status based on startDate and endDate
//        Date currentDate = new Date();
//        if (currentDate.before(project.getStartDate())) {
//            project.setStatus("PRE");
//        } else if (currentDate.after(project.getStartDate()) && currentDate.before(project.getEndDate())) {
//            project.setStatus("START");
//        } else {
//            project.setStatus("END");
//        }


        // Save the project
        Project savedProject = projectService.saveProject(project);
        System.out.println("Saved project: " + savedProject);

        // Fetch the saved project
        Project fetchedProject = projectService.fetchProjectById(savedProject.getProjectId());
        System.out.println("Fetched project: " + fetchedProject);
        System.out.println("Fetched project owner: " + fetchedProject.getOwner());
        System.out.println("Fetched project users: " + fetchedProject.getUsers());

        return "redirect:/project/list";
    }


    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);
        return "redirect:/project/list";
    }

    @GetMapping("/details/{id}")
    public String projectDetails(@PathVariable("id") Long id, Model model) {
        System.out.println("------------------");
        System.out.println("Showing Project Details");
        System.out.println("Project id: " + id);
        Project project = projectService.fetchProjectById(id);
        System.out.println("Project: " + project);
//        System.out.println(project);
        User owner = project.getOwner();
        Collection<User> members = project.getUsers();
        System.out.println("members: " + members);

        List<User> userList = userService.fetchUserList();
        model.addAttribute("userList", userList);

        List<User> nonMembers = userList.stream()
                .filter(user -> !members.contains(user))
                .collect(Collectors.toList());

        int memberCount = members.size();
        boolean memberEmpty = memberCount == 0;
        System.out.println("memberCountIsZero: " + memberEmpty);
        boolean memberFull = memberCount >= 5;
        System.out.println("memberFull: " + memberFull);
        System.out.println("memberCount: " + memberCount);
        // find the userNames from userList set
        // name is formed by joining firstName and lastName
//        List<String> userNames = members.stream()
//                .map(user -> user.getFirstName() + " " + user.getLastName())
//                .collect(Collectors.toList());

//        List<String> userNames = userList.stream().map(user -> user.getFirstName() + " " + user.getLastName()).toList();
        model.addAttribute("project", project);
        model.addAttribute("owner", owner);
        model.addAttribute("members", members);
        model.addAttribute("nonMembers", nonMembers);
        model.addAttribute("memberCount", memberCount);
        model.addAttribute("memberEmpty", memberEmpty);
        model.addAttribute("memberFull", memberFull);
        System.out.println("------------------");
        return "user/project-detail";
    }

    @PostMapping("/add-member/{projectId}")
    public String addMember(@PathVariable("projectId") Long projectId, @RequestParam("selectedUser") Long userId) {
        System.out.println("------------------");
        System.out.println("Adding member to project");
        System.out.println("projectId: " + projectId);
        System.out.println("userId: " + userId);
        Project project = projectService.fetchProjectById(projectId);
        User user = userService.fetchUserById(userId);
        System.out.println("project: " + project);
        System.out.println("selectedUser: " + userId);
        Set<User> members = project.getUsers();
        members.add(user);
        project.setUsers(members);
        projectService.updateProject(project);
        Project p = projectService.fetchProjectById(projectId);
        System.out.println("Final project: " + p);
        System.out.println("------------------");
        return "redirect:/project/details/" + projectId;
    }

    @PostMapping("/delete-member/{projectId}")
    public String removeMember(@PathVariable("projectId") Long projectId, @RequestParam("deletedUser") Long userId) {
        System.out.println("------------------");
        System.out.println("Removing member from project");
        System.out.println("projectId: " + projectId);
        System.out.println("userId: " + userId);
        Project project = projectService.fetchProjectById(projectId);
        User user = userService.fetchUserById(userId);
        System.out.println("project: " + project);
        System.out.println("selectedUser: " + userId);
        Set<User> members = project.getUsers();
        members.remove(user);
        project.setUsers(members);
        projectService.updateProject(project);
        Project p = projectService.fetchProjectById(projectId);
        System.out.println("Final project: " + p);
        System.out.println("------------------");
        return "redirect:/project/details/" + projectId;
    }

    @GetMapping("/edit/{id}")
    public String editProject(@PathVariable("id") Long id, Model model) {
        System.out.println("------------------");
        System.out.println("Editing Project");
        System.out.println("Project id: " + id);
        Project project = projectService.fetchProjectById(id);
        System.out.println("Project: " + project);
        model.addAttribute("project", project);
        System.out.println("------------------");
        return "user/edit-project";
    }

    //    update project's name, description, and start and end dates
    @PostMapping("/update-project/{id}")
    public String updateProject(@PathVariable("id") Long id, @RequestParam("projectName") String projectName,
                                @RequestParam("projectIntro") String projectIntro,
                                @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                Model model) {
        System.out.println("------------------");
        System.out.println("Updating Project");
        System.out.println("Project id: " + id);
        System.out.println("Project name: " + projectName);
        System.out.println("Project intro: " + projectIntro);
        System.out.println("Project start date: " + startDate);
        System.out.println("Project end date: " + endDate);
        Project project = projectService.fetchProjectById(id);
        System.out.println("Project: " + project);
        project.setProjectName(projectName);
        project.setProjectIntro(projectIntro);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        projectService.updateProject(project);
//        System.out.println("Project: " + project);
//        if (result.hasErrors()) {
//            System.out.println("Errors: " + result.getAllErrors());
//            return "user/edit-project";
//        }
//        projectService.updateProject(project);
        System.out.println("------------------");
        return "redirect:/project/details/" + id;
    }
}