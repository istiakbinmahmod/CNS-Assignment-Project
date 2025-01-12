package net.javaguides.springboot.service;

import net.javaguides.springboot.model.Project;
import net.javaguides.springboot.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
        System.out.println("save er age project: " + project);
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public Project fetchProjectById(Long id) {
        return projectRepository.findById(id).get();
    }

    @Override
    public void updateProject(Project project) {
        projectRepository.save(project);
    }

//    @Override
//    public List<Project> fetchProjectListFiltered(Date startDate, Date endDate) {
//        List<Project> allProjects = projectRepository.findAll();
//        Iterator<Project> iterator = allProjects.iterator();
//
//        while (iterator.hasNext()) {
//            Project project = iterator.next();
//            if (project.getStartDate().before(startDate) || project.getEndDate().after(endDate)) {
//                iterator.remove();
//            }
//        }
//
//        return allProjects;
//    }

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



}