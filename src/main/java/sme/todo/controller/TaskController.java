package sme.todo.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import sme.todo.controller.config.TaskModelAssembler;
import sme.todo.controller.dto.TaskRequest;
import sme.todo.exceptions.BadRequestException;
import sme.todo.model.Task;
import sme.todo.model._enum.PriorityEnum;
import sme.todo.model._enum.StatusEnum;
import sme.todo.service.TaskService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TaskController {
    private static final String UPDATE = "update";
    private static final String DONETASKS = "doneTasks";
    private static final String DONE = "done";
    private static final String DELETE = "delete";
    private final TaskService taskService;
    private final TaskModelAssembler assembler;

    public TaskController(TaskService taskService, TaskModelAssembler assembler) {
        this.taskService = taskService;
        this.assembler = assembler;
    }

    @PostMapping("/tasks")
    public ResponseEntity<EntityModel<Task>> create(@RequestBody @Valid TaskRequest taskRequest, Errors error) {
      handleRequestValidation(error);
      Task task = taskService.addTask(taskRequest);
      EntityModel<Task> entityModel = assembler.toModel(task);
      return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<EntityModel<Task>> one(@PathVariable long id) {
        Errors error = null;
        Task task =  taskService.getTask(id);
        EntityModel<Task> entityModel = assembler.toModel(task);
        entityModel.add(linkTo(methodOn(TaskController.class).update(task.getId(),
                new TaskRequest(),error)).withRel(UPDATE));
        entityModel.add(linkTo(methodOn(TaskController.class).delete(task.getId())).withRel(DELETE));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);

    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<EntityModel<Task>> update(@PathVariable long id,
                                                    @RequestBody @Valid TaskRequest taskRequest,Errors error) {
        handleRequestValidation(error);
        Task task = taskService.updateTask(taskRequest, id);
        EntityModel<Task> entityModel = assembler.toModel(task);
        return ResponseEntity
                .ok()
                .body(entityModel);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // get all tasks with high priority order
    @GetMapping("/tasks")
    public CollectionModel<EntityModel<Task>> all() {
        List<EntityModel<Task>> tasks = taskService.getTasks()
                .stream()
                .map(task -> {
                    EntityModel<Task> entityModel = assembler.toModel(task);
                    if (task.getStatus() == StatusEnum.Pending) {
                        entityModel.add(linkTo(methodOn(TaskController.class).doneTask(task.getId())).withRel(DONE));
                    } else {
                        entityModel.add(linkTo(methodOn(TaskController.class).allDone()).withRel(DONETASKS));
                    }
                    entityModel.add(linkTo(methodOn(TaskController.class).delete(task.getId())).withRel(DELETE));
                    return entityModel;
                })
                .collect(Collectors.toList());
        return CollectionModel.of(tasks, linkTo(methodOn(TaskController.class).all()).withSelfRel());
    }

    @PutMapping("/tasks/{id}/mark-done")
    public ResponseEntity<EntityModel<Task>> doneTask(@PathVariable long id) {
        Task task = taskService.markAsDone(id);
        EntityModel<Task> entityModel = assembler.toModel(task);
        entityModel.add(linkTo(methodOn(TaskController.class).allDone()).withRel(DONETASKS));
        return ResponseEntity
                .ok()
                .body(entityModel);
    }

    @GetMapping("/tasks/mark-done")
    public CollectionModel<EntityModel<Task>> allDone() {
        List<EntityModel<Task>> tasks = taskService.getDoneTasks()
                .stream()
                .map(task -> {
                    EntityModel<Task> entityModel = assembler.toModel(task);
                    entityModel.add(linkTo(methodOn(TaskController.class).delete(task.getId())).withRel(DELETE));
                    return entityModel;
                })
                .collect(Collectors.toList());
        return CollectionModel.of(tasks, linkTo(methodOn(TaskController.class).allDone()).withSelfRel());
    }

    @GetMapping("tasks/priorities")
    public PriorityEnum[] allPriorites() {
        return PriorityEnum.values();
    }

    private void handleRequestValidation(Errors error) {
        if(error.hasErrors())
            throw new BadRequestException(error.getFieldError().getDefaultMessage());
    }

}
