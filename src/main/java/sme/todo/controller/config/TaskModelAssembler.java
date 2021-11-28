package sme.todo.controller.config;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import sme.todo.controller.TaskController;
import sme.todo.model.Task;
import sme.todo.model._enum.StatusEnum;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TaskModelAssembler implements RepresentationModelAssembler<Task,EntityModel<Task>> {
    private final static String TASKS = "tasks";
    @Override
    public EntityModel<Task> toModel(Task task) {
        // Unconditional links to single-item resource and aggregate root
        EntityModel<Task> taskModel = EntityModel.of(task,
                linkTo(methodOn(TaskController.class).one(task.getId())).withSelfRel(),
                linkTo(methodOn(TaskController.class).all()).withRel(TASKS));
        // Conditional links based on state of the task
        return taskModel;
    }
}
