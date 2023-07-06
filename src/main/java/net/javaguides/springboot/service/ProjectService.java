package net.javaguides.springboot.service;

import net.javaguides.springboot.model.Project;
import net.javaguides.springboot.model.User;
import net.sf.jasperreports.engine.JRException;
import org.springframework.ui.Model;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ProjectService {

    public List<Project> fetchProjectList();

    public Project saveProject(Project project);

    public void deleteProject(Long id);

    public Project fetchProjectById(Long id);

    public void updateProject(Project project);

    public List<Project> fetchProjectListFiltered(Date startDate, Date endDate);

    public boolean addMembersToProject(Project project, Set<User> users);

    public String exportReport(String reportFormat, Date start, Date end) throws FileNotFoundException, JRException;


}