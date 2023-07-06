package net.javaguides.springboot.service;

import net.javaguides.springboot.model.Project;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.ProjectRepository;
import net.javaguides.springboot.web.dto.ProjectInfoDto;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
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

    @Override
    public String exportReport(String reportFormat) throws FileNotFoundException, JRException {
        List<Project> allProjects = projectRepository.findAll();
        List<ProjectInfoDto> projectInfoDtoList = new ArrayList<>();
        for(Project project : allProjects) {
            ProjectInfoDto projectInfoDto = new ProjectInfoDto();
            projectInfoDto.setId(project.getId());
            projectInfoDto.setName(project.getName());
            projectInfoDto.setIntro(project.getIntro());
            projectInfoDto.setStatus(project.getStatus());
            projectInfoDto.setOwner_email(project.getOwner().getEmail());
            projectInfoDto.setStartDate(new SimpleDateFormat("dd/MM/yyyy").format(project.getStartDate()));
            projectInfoDto.setEndDate(new SimpleDateFormat("dd/MM/yyyy").format(project.getEndDate()));
            projectInfoDtoList.add(projectInfoDto);
        }
        //load file and compile it
        File file = ResourceUtils.getFile("classpath:projects.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(projectInfoDtoList);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "CNS Limited");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        if(reportFormat.equalsIgnoreCase("html")) {
            JasperExportManager.exportReportToHtmlFile(jasperPrint, "E:\\Reports\\projects.html");
        }
        if(reportFormat.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, "E:\\Reports\\projects.pdf");
        }
        return "report generated in path : " + "E:\\Reports\\";
    }


}