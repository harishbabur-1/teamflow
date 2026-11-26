package com.teamflow.task.service;

import com.teamflow.project.entity.Project;
import com.teamflow.project.repository.ProjectMemberRepository;
import com.teamflow.project.repository.ProjectRepository;
import com.teamflow.task.dto.*;
import com.teamflow.task.entity.Task;
import com.teamflow.task.entity.TaskComment;
import com.teamflow.task.entity.TaskPriority;
import com.teamflow.task.entity.TaskStatus;
import com.teamflow.task.repository.TaskCommentRepository;
import com.teamflow.task.repository.TaskRepository;
import com.teamflow.user.entity.User;
import com.teamflow.user.repository.UserRepository;

import com.teamflow.workspace.entity.WorkspaceMembership;
import com.teamflow.workspace.entity.WorkspaceRole;
import com.teamflow.workspace.repository.WorkspaceMembershipRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final WorkspaceMembershipRepository workspaceMembershipRepository;
    private final TaskCommentRepository taskCommentRepository;

    @Transactional
    public TaskResponse createTask(
            Long projectId,
            CreateTaskRequest request,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setProject(project);
        task.setCreatedBy(user);
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        Task savedTask = taskRepository.save(task);

        return new TaskResponse(
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getProject().getId(),
                savedTask.getCreatedBy().getEmail(),
                savedTask.getPriority(),
                savedTask.getStatus(),
                savedTask.getDueDate(),
                savedTask.getCreatedAt(),
                savedTask.getAssignedTo() != null
                        ? task.getAssignedTo().getEmail()
                        : null
        );
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getProjectTasks(
            Long projectId,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        List<Task> tasks = taskRepository.findByProjectId(projectId);

        return tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getProject().getId(),
                        task.getCreatedBy().getEmail(),
                        task.getPriority(),
                        task.getStatus(),
                        task.getDueDate(),
                        task.getCreatedAt(),
                        task.getAssignedTo() != null
                                ? task.getAssignedTo().getEmail()
                                : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(
            Long projectId,
            Long taskId,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getProject().getId(),
                task.getCreatedBy().getEmail(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getAssignedTo() != null
                        ? task.getAssignedTo().getEmail()
                        : null
        );
    }

    @Transactional
    public TaskResponse updateTask(
            Long projectId,
            Long taskId,
            UpdateTaskRequest request,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        WorkspaceMembership membership =
                workspaceMembershipRepository
                        .findByWorkspaceIdAndUserEmail(
                                project.getWorkspace().getId(),
                                currentUserEmail
                        )
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "You are not a member of this workspace"
                        ));


        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }


        boolean isTaskCreator =
                task.getCreatedBy().getEmail().equals(currentUserEmail);

        boolean isProjectCreator =
                project.getCreatedBy().getEmail().equals(currentUserEmail);

        WorkspaceRole role = membership.getRole();

        boolean isWorkspaceAdmin =
                role == WorkspaceRole.OWNER
                        || role == WorkspaceRole.MANAGER;

        if (!isTaskCreator && !isProjectCreator && !isWorkspaceAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to update this task"
            );
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);

        return new TaskResponse(
                updatedTask.getId(),
                updatedTask.getTitle(),
                updatedTask.getDescription(),
                updatedTask.getProject().getId(),
                updatedTask.getCreatedBy().getEmail(),
                updatedTask.getPriority(),
                updatedTask.getStatus(),
                updatedTask.getDueDate(),
                updatedTask.getCreatedAt(),
                task.getAssignedTo() != null
                        ? task.getAssignedTo().getEmail()
                        : null
        );
    }

    @Transactional
    public TaskResponse updateTaskStatus(
            Long projectId,
            Long taskId,
            UpdateTaskStatusRequest request,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));

        task.setStatus(request.getStatus());

        Task updatedTask = taskRepository.save(task);

        return new TaskResponse(
                updatedTask.getId(),
                updatedTask.getTitle(),
                updatedTask.getDescription(),
                project.getId(),
                updatedTask.getCreatedBy().getEmail(),
                updatedTask.getPriority(),
                updatedTask.getStatus(),
                updatedTask.getDueDate(),
                updatedTask.getCreatedAt(),
                task.getAssignedTo() != null
                        ? task.getAssignedTo().getEmail()
                        : null
        );
    }

    @Transactional
    public void deleteTask(
            Long projectId,
            Long taskId,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));

        WorkspaceMembership membership =
                workspaceMembershipRepository
                        .findByWorkspaceIdAndUserEmail(
                                project.getWorkspace().getId(),
                                currentUserEmail
                        )
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "You are not a member of this workspace"
                        ));

        boolean isTaskCreator =
                task.getCreatedBy().getEmail().equals(currentUserEmail);

        boolean isProjectCreator =
                project.getCreatedBy().getEmail().equals(currentUserEmail);

        WorkspaceRole role = membership.getRole();

        boolean isWorkspaceAdmin =
                role == WorkspaceRole.OWNER
                        || role == WorkspaceRole.MANAGER;

        if (!isTaskCreator && !isProjectCreator && !isWorkspaceAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to delete this task"
            );
        }

        taskRepository.delete(task);
    }

    @Transactional
    public void assignTask(
            Long projectId,
            Long taskId,
            AssignTaskRequest request,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));

        WorkspaceMembership membership =
                workspaceMembershipRepository
                        .findByWorkspaceIdAndUserEmail(
                                project.getWorkspace().getId(),
                                currentUserEmail
                        )
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "You are not a member of this workspace"
                        ));

        boolean isTaskCreator =
                task.getCreatedBy().getEmail().equals(currentUserEmail);

        boolean isProjectCreator =
                project.getCreatedBy().getEmail().equals(currentUserEmail);

        WorkspaceRole role = membership.getRole();

        boolean isWorkspaceAdmin =
                role == WorkspaceRole.OWNER
                        || role == WorkspaceRole.MANAGER;

        if (!isTaskCreator && !isProjectCreator && !isWorkspaceAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to assign tasks"
            );
        }

        User assignedUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        assignedUser.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Assigned user is not a member of this project"
            );
        }

        task.setAssignedTo(assignedUser);

        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getMyAssignedTasks(Authentication authentication) {

        String email = authentication.getName();

        userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        List<Task> tasks = taskRepository.findByAssignedToEmail(email);

        return tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getProject().getId(),
                        task.getCreatedBy().getEmail(),
                        task.getPriority(),
                        task.getStatus(),
                        task.getDueDate(),
                        task.getCreatedAt(),
                        task.getAssignedTo() != null
                                ? task.getAssignedTo().getEmail()
                                : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getMyCreatedTasks(Authentication authentication) {

        String email = authentication.getName();

        userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        List<Task> tasks = taskRepository.findByCreatedByEmail(email);

        return tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getProject().getId(),
                        task.getCreatedBy().getEmail(),
                        task.getPriority(),
                        task.getStatus(),
                        task.getDueDate(),
                        task.getCreatedAt(),
                        task.getAssignedTo() != null
                                ? task.getAssignedTo().getEmail()
                                : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(
            Long projectId,
            TaskStatus status,
            Authentication authentication) {

        String email = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        List<Task> tasks =
                taskRepository.findByProjectIdAndStatus(projectId, status);

        return tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        project.getId(),
                        task.getCreatedBy().getEmail(),
                        task.getPriority(),
                        task.getStatus(),
                        task.getDueDate(),
                        task.getCreatedAt(),
                        task.getAssignedTo() != null
                                ? task.getAssignedTo().getEmail()
                                : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByPriority(
            Long projectId,
            TaskPriority priority,
            Authentication authentication) {

        String email = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        List<Task> tasks =
                taskRepository.findByProjectIdAndPriority(projectId, priority);

        return tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        project.getId(),
                        task.getCreatedBy().getEmail(),
                        task.getPriority(),
                        task.getStatus(),
                        task.getDueDate(),
                        task.getCreatedAt(),
                        task.getAssignedTo() != null
                                ? task.getAssignedTo().getEmail()
                                : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByAssignee(
            Long projectId,
            Long userId,
            Authentication authentication) {

        String email = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        currentUser.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        User assignedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Assigned user not found"
                ));

        boolean isAssignedUserProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        assignedUser.getId()
                );

        if (!isAssignedUserProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User is not a member of this project"
            );
        }

        List<Task> tasks =
                taskRepository.findByProjectIdAndAssignedToId(projectId, userId);

        return tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        project.getId(),
                        task.getCreatedBy().getEmail(),
                        task.getPriority(),
                        task.getStatus(),
                        task.getDueDate(),
                        task.getCreatedAt(),
                        task.getAssignedTo() != null
                                ? task.getAssignedTo().getEmail()
                                : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks(
            Long projectId,
            Authentication authentication) {

        String email = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        List<Task> tasks =
                taskRepository.findByProjectIdAndDueDateBeforeAndStatusNot(
                        projectId,
                        LocalDate.now(),
                        TaskStatus.DONE
                );

        return tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        project.getId(),
                        task.getCreatedBy().getEmail(),
                        task.getPriority(),
                        task.getStatus(),
                        task.getDueDate(),
                        task.getCreatedAt(),
                        task.getAssignedTo() != null
                                ? task.getAssignedTo().getEmail()
                                : null
                ))
                .toList();
    }

    @Transactional
    public TaskCommentResponse addComment(
            Long projectId,
            Long taskId,
            CreateCommentRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));

        TaskComment comment = new TaskComment();
        comment.setContent(request.getContent());
        comment.setTask(task);
        comment.setUser(user);

        TaskComment savedComment = taskCommentRepository.save(comment);

        return new TaskCommentResponse(
                savedComment.getId(),
                savedComment.getContent(),
                task.getId(),
                user.getEmail(),
                savedComment.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<TaskCommentResponse> getTaskComments(
            Long projectId,
            Long taskId,
            Authentication authentication) {

        String email = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));

        return taskCommentRepository
                .findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(comment -> new TaskCommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        taskId,
                        comment.getUser().getEmail(),
                        comment.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public TaskCommentResponse updateComment(
            Long projectId,
            Long taskId,
            Long commentId,
            CreateCommentRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));

        TaskComment comment = taskCommentRepository
                .findByIdAndUserEmail(commentId, email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Comment not found"
                ));

        if (!comment.getTask().getId().equals(taskId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Comment not found for this task"
            );
        }

        comment.setContent(request.getContent());

        TaskComment updatedComment = taskCommentRepository.save(comment);

        return new TaskCommentResponse(
                updatedComment.getId(),
                updatedComment.getContent(),
                taskId,
                email,
                updatedComment.getCreatedAt()
        );
    }

    @Transactional
    public void deleteComment(
            Long projectId,
            Long taskId,
            Long commentId,
            Authentication authentication) {

        String email = authentication.getName();

        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isProjectMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isProjectMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this project"
            );
        }

        taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));

        TaskComment comment = taskCommentRepository
                .findByIdAndTaskIdAndUserEmail(commentId, taskId, email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Comment not found"
                ));

        taskCommentRepository.delete(comment);
    }
}
