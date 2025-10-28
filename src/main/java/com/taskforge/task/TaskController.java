package com.taskforge.task;

import com.taskforge.user.User;
import com.taskforge.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public static class CreateTaskRequest {
        @NotBlank public String title;
        public String description;
    }

    @GetMapping
    public List<Task> list(@AuthenticationPrincipal UserDetails principal) {
        User owner = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        return taskRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    @PostMapping
    public Task create(@AuthenticationPrincipal UserDetails principal, @Valid @RequestBody CreateTaskRequest req) {
        User owner = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Task t = new Task();
        t.setTitle(req.title);
        t.setDescription(req.description);
        t.setOwner(owner);
        return taskRepository.save(t);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id, @RequestBody Map<String, Object> patch) {
        User owner = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        return taskRepository.findById(id)
                .filter(t -> t.getOwner().getId().equals(owner.getId()))
                .map(t -> {
                    if (patch.containsKey("title")) t.setTitle((String) patch.get("title"));
                    if (patch.containsKey("description")) t.setDescription((String) patch.get("description"));
                    if (patch.containsKey("completed")) t.setCompleted(Boolean.parseBoolean(patch.get("completed").toString()));
                    taskRepository.save(t);
                    return ResponseEntity.ok(t);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
        User owner = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        return taskRepository.findById(id)
                .filter(t -> t.getOwner().getId().equals(owner.getId()))
                .map(t -> {
                    taskRepository.delete(t);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}


