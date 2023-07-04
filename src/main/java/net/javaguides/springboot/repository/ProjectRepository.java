package net.javaguides.springboot.repository;

import net.javaguides.springboot.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    public Project findByProjectName(String projectName);

    public Project findByProjectNameIgnoreCase(String projectName);
}
