package sme.todo.service;

import org.springframework.stereotype.Service;
import sme.todo.controller.dto.TaskRequest;
import sme.todo.exceptions.BusinessNotFoundException;
import sme.todo.model.Task;
import sme.todo.model._enum.PriorityEnum;
import sme.todo.model._enum.StatusEnum;
import sme.todo.repository.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task addTask(TaskRequest taskRequest) {
        Task task = new Task();
        task.setDescription(taskRequest.getDescription());
        task.setPriority(PriorityEnum.valueOf(taskRequest.getPriority()));
        task.setStatus(StatusEnum.Pending);
        return taskRepository.save(task);
    }

    public Task getTask(long id) {
        var task = findTask(id);
        return task;
    }

    public Task updateTask(TaskRequest taskRequest, long id) {
        var task = findTask(id);
        requestUpdate(task,taskRequest);
        return taskRepository.save(task);
    }

    public void deleteTask(long id) {
        var dbTask = findTask(id);
        taskRepository.delete(dbTask);
    }

    public Task markAsDone(long id) {
        var dbTask = findTask(id);
        dbTask.setStatus(StatusEnum.Done);
        return taskRepository.save(dbTask);
    }

    public List<Task> getDoneTasks() {
        return taskRepository.findByStatus(StatusEnum.Done);
    }

    // get tasks with high order priority
    public List<Task> getTasks() {
        return taskRepository.findAllByOrderByPriorityAsc();
    }

    private Task findTask(long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException("task not found for id : " + id));
    }

    private void requestUpdate(Task task, TaskRequest taskRequest) {
        task.setPriority(PriorityEnum.valueOf(taskRequest.getPriority()));
        task.setDescription(taskRequest.getDescription());
    }
}
