package com.example.taskmanagerproject.repository;

import com.example.taskmanagerproject.model.Task;
import com.example.taskmanagerproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByAuthor(User author, Pageable pageable);
    Page<Task> findByAssignee(User assignee, Pageable pageable);
    Page<Task> findByAuthorAndAssignee(User author, User assignee, Pageable pageable);

}
