package net.javaguides.springboot.service;

import net.javaguides.springboot.model.Project;
import org.springframework.ui.Model;

import java.util.Date;
import java.util.List;

public interface ProjectService {

    public List<Project> fetchProjectList();

    public Project saveProject(Project project);

    public void deleteProject(Long id);

    public Project fetchProjectById(Long id);

    public void updateProject(Project project);

    public List<Project> fetchProjectListFiltered(Date startDate, Date endDate);
}