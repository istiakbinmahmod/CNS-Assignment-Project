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
import java.text.SimpleDateFormat;
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

    @GetMapping({"/", "/list"})
    public String fetchProjectList(Model model) {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = userService.fetchUserByEmail(loggedInUser.getName()).getId();

        model.addAttribute("projects", projectService.fetchProjectList());
        model.addAttribute("loggedInUser", currentUserId);

        return "user/projects";
    }

    @GetMapping("/add")
    public String createProject(Model model) {
        Project project = new Project();
        model.addAttribute("project", project);
        List<User> userList = userService.fetchUserList();
        model.addAttribute("userList", userList);
        return "user/add-project";
    }

    @PostMapping("/save-project")
    public String saveProject(@ModelAttribute("project") Project project,
                              @RequestParam("selectedUsers") List<Long> selectedUsers) {

        // Set project owner
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        User owner = userService.fetchUserByEmail(loggedInUser.getName());
        project.setOwner(owner);

        System.out.println(project.getId());

        // Set project users
        Set<User> users = new HashSet<>(userService.fetchAllUsersById(selectedUsers));
//        project.setMembers(users);

        projectService.addMembersToProject(project, users);

        // Save the project
        Project savedProject = projectService.saveProject(project);
        System.out.println("Saved project: " + savedProject);

        return "redirect:/project/list";
    }

    @GetMapping("/filter")
    public String filterProjects(@RequestParam("filterStartDate") String startDate,
                                 @RequestParam("filterEndDate") String endDate,
                                 Model model) {
        System.out.println("------------------");
        System.out.println("Fetching Filtered Project List");
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = userService.fetchUserByEmail(loggedInUser.getName()).getId();
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


    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id) {
        userService.deleteProjectById(id);
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
        Collection<User> members = project.getMembers();
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

        Project project = projectService.fetchProjectById(projectId);
        User user = userService.fetchUserById(userId);
//        Set<User> members = project.getMembers();
//        members.add(user);
//        project.setMembers(members);
//        projectService.updateProject(project);
//        Project p = projectService.fetchProjectById(projectId);
//        System.out.println("Final project: " + p);
//        System.out.println("------------------");
        user.getProjects().add(project);
        userService.saveUser(user);
        return "redirect:/project/details/" + projectId;
    }

    @PostMapping("/delete-member/{projectId}")
    public String removeMember(@PathVariable("projectId") Long projectId, @RequestParam("deletedUser") Long userId) {

        Project project = projectService.fetchProjectById(projectId);
        User user = userService.fetchUserById(userId);

        user.getProjects().remove(project);
        userService.saveUser(user);

//        Set<User> members = project.getMembers();
//        members.remove(user);
//        project.setMembers(members);
//        projectService.updateProject(project);

        return "redirect:/project/details/" + projectId;
    }

    @GetMapping("/edit/{id}")
    public String editProject(@PathVariable("id") Long id, Model model) {

        if(checkIfAuthorized(id)) return "error";

        Project project = projectService.fetchProjectById(id);
        model.addAttribute("project", project);
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
        project.setName(projectName);
        project.setIntro(projectIntro);
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

    private boolean checkIfAuthorized(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        if(projectService.fetchProjectById(id).getOwner().getEmail().equals(email)) {
            return false;
        }
        return true;
    }
}