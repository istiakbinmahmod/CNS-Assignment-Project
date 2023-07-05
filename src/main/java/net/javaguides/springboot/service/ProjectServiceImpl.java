package net.javaguides.springboot.service;

import net.javaguides.springboot.model.Project;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class ProjectServiceImpl implements ProjectService{

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<Project> fetchProjectList() {
        return projectRepository.findAll();
    }

    @Override
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public Project fetchProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
//        return projectRepository.findById(id).get();
    }

    @Override
    public void updateProject(Project project) {
        projectRepository.save(project);
    }

    @Override
    public List<Project> fetchProjectListFiltered(Date startDate, Date endDate) {
        List<Project> allProjects = projectRepository.findAll();
        Iterator<Project> iterator = allProjects.iterator();

        while (iterator.hasNext()) {
            Project project = iterator.next();
            if (project.getStartDate().compareTo(startDate) < 0 || project.getEndDate().compareTo(endDate) > 0) {
                iterator.remove();
            }
        }

        return allProjects;
    }

    @Override
    public boolean addMembersToProject(Project project, Set<User> users) {
        if(users == null){
            return false;
        }
        for(User a : users) {
            if(project.getMembers() == null) {
                project.setMembers(new HashSet<>());
            } else {
                project.getMembers().add(a);
            }
            a.getProjects().add(project);
        }
        return true;
    }


}