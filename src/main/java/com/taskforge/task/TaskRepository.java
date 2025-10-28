package com.taskforge.task;

import com.taskforge.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwnerOrderByCreatedAtDesc(User owner);
}


