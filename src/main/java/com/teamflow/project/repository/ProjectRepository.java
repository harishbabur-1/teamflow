
package com.teamflow.project.repository;

import com.teamflow.project.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @EntityGraph(attributePaths = {"workspace", "createdBy"})
    List<Project> findByWorkspaceId(Long workspaceId);

    @EntityGraph(attributePaths = {"workspace", "createdBy"})
    Optional<Project> findById(Long id);

}
