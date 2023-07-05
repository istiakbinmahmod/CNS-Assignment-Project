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
                              @RequestParam(value = "selectedUsers", required = false) List<Long> selectedUsers) {

        // Set project owner
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        User owner = userService.fetchUserByEmail(loggedInUser.getName());
        project.setOwner(owner);

        System.out.println(project.getId());

        // Set project members
//        Set<User> users = new HashSet<>(userService.fetchAllUsersById(selectedUsers));
//        projectService.addMembersToProject(project, users);
        if (selectedUsers != null && !selectedUsers.isEmpty()) {
            Set<User> users = new HashSet<>(userService.fetchAllUsersById(selectedUsers));
            projectService.addMembersToProject(project, users);
        }

        // Save the project
        Project savedProject = projectService.saveProject(project);
        System.out.println("Saved project: " + savedProject);

        return "redirect:/project/list";
    }

    @GetMapping("/filter")
    public String filterProjects(@RequestParam("filterStartDate") String startDate,
                                 @RequestParam("filterEndDate") String endDate,
                                 Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = userService.fetchUserByEmail(loggedInUser.getName()).getId();

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
//        return "redirect:/project/list";
        return "user/projects";
    }


    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id) {
        if (!checkIfAuthorized(id)) {
            return "user/error";
        }

        Project project = projectService.fetchProjectById(id);
        if (project == null) {
            return "user/error";
        }

        userService.deleteProjectById(id);
        projectService.deleteProject(id);
        return "redirect:/project/list";
    }

    @GetMapping("/details/{id}")
    public String projectDetails(@PathVariable("id") Long id, Model model) {

        Project project = projectService.fetchProjectById(id);

        if (project == null) {
            return "user/error";
        }

        User owner = project.getOwner();
        Collection<User> members = project.getMembers();

        List<User> userList = userService.fetchUserList();
        model.addAttribute("userList", userList);

        List<User> nonMembers = userList.stream()
                .filter(user -> !members.contains(user))
                .collect(Collectors.toList());

        int memberCount = members.size();
        boolean memberEmpty = memberCount == 0;
        boolean memberFull = memberCount >= 5;

        model.addAttribute("project", project);
        model.addAttribute("owner", owner);
        model.addAttribute("members", members);
        model.addAttribute("nonMembers", nonMembers);
        model.addAttribute("memberEmpty", memberEmpty);
        model.addAttribute("memberFull", memberFull);
        model.addAttribute("editPermission", checkIfAuthorized(id));

        return "user/project-detail";
    }

    @PostMapping("/add-member/{projectId}")
    public String addMember(@PathVariable("projectId") Long projectId, @RequestParam("selectedUser") Long userId) {

        if (!checkIfAuthorized(projectId)) return "user/error";

        Project project = projectService.fetchProjectById(projectId);
        if(project == null) return "user/error";

        User user = userService.fetchUserById(userId);
        user.getProjects().add(project);
        userService.saveUser(user);
        return "redirect:/project/details/" + projectId;
    }

    @PostMapping("/delete-member/{projectId}")
    public String removeMember(@PathVariable("projectId") Long projectId, @RequestParam("deletedUser") Long userId) {

        if (!checkIfAuthorized(projectId)) return "user/error";

        Project project = projectService.fetchProjectById(projectId);
        if(project == null) return "user/error";

        User user = userService.fetchUserById(userId);

        user.getProjects().remove(project);
        userService.saveUser(user);

        return "redirect:/project/details/" + projectId;
    }

    @GetMapping("/edit/{id}")
    public String editProject(@PathVariable("id") Long id, Model model) {

        if (!checkIfAuthorized(id)) return "user/error";

        Project project = projectService.fetchProjectById(id);
        if (project == null) return "user/error";

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

        if (!checkIfAuthorized(id)) return "user/error";

        Project project = projectService.fetchProjectById(id);
        project.setName(projectName);
        project.setIntro(projectIntro);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        projectService.updateProject(project);
        return "redirect:/project/details/" + id;
    }

    //    Checks whether the user is authorized to modify the project
    private boolean checkIfAuthorized(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Project proj = projectService.fetchProjectById(id);
        if(proj == null) return false;
        if (proj.getOwner().getEmail().equals(email)) {
            return true;
        }
        return false;
    }
}