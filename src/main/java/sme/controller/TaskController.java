package sme.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sme.controller.dto.TaskRequest;
import sme.service.TaskService;

import javax.validation.Valid;

@RestController
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/tasks")
    public void createTask(@RequestBody @Valid TaskRequest taskRequest) {

   }
}
