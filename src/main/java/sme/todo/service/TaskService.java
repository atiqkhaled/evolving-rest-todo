package sme.todo.service;
import org.springframework.stereotype.Service;
import sme.todo.controller.dto.TaskRequest;
import sme.todo.model.Task;
import sme.todo.model._enum.PriorityEnum;
import sme.todo.model._enum.StatusEnum;
import sme.todo.repository.TaskRepository;
import sme.todo.exceptions.BadRequestException;
import sme.todo.exceptions.BusinessNotFoundException;
import sme.todo.exceptions.BusinessServiceUnavailableException;
import sme.todo.exceptions.InternalServerException;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task addTask(TaskRequest taskRequest) {
        Task dbTask = null;
        try {
            if (taskRequest == null)
                throw new BadRequestException();
            Task task = new Task();
            task.setDescription(taskRequest.getDescription());
            task.setPriority(PriorityEnum.valueOf(taskRequest.getPriority()));
            task.setStatus(StatusEnum.Pending);
            dbTask = taskRepository.save(task);
        } catch (BadRequestException bre) {
            throw bre;
        } catch (Exception ex) {
            throw new InternalServerException();
        }
        return dbTask;
    }

    public Task getTask(long id) {
        try {
            Optional<Task> optionalTask = taskRepository.findById(id);
            if (!optionalTask.isPresent())
                throw new BusinessNotFoundException();
            Task dbTask = optionalTask.get();
            return dbTask;
        } catch (BusinessNotFoundException bnf) {
            throw bnf;
        } catch (Exception ex) {
            throw new InternalServerException();
        }
    }

    public Task updateTask(TaskRequest taskRequest,long id) {
        Task updateTask = null;
        try {
            Optional<Task> optionalTask = taskRepository.findById(id);
            if (!optionalTask.isPresent())
                throw new BusinessNotFoundException();
            updateTask = optionalTask.get().copy(taskRequest);
            updateTask = taskRepository.save(updateTask);
        } catch (BusinessNotFoundException bnf) {
            throw bnf;
        } catch (Exception ex) {
            throw new InternalServerException();
        }
        return updateTask;
    }

    public void deleteTask(long id) {
        try {
            Optional<Task> optionalTask = taskRepository.findById(id);
            if (!optionalTask.isPresent())
                throw new BusinessNotFoundException();
            taskRepository.delete(optionalTask.get());
        }catch (BusinessNotFoundException bnf) {
            throw bnf;
        } catch (Exception ex) {
            throw new BusinessServiceUnavailableException();
        }
    }

    public Task markAsDone(long id) {
        Task dbTask = null;
        try {
            Optional<Task> optionalTask = taskRepository.findById(id);
            if (!optionalTask.isPresent()) {
                throw new BusinessNotFoundException();
            }
            dbTask = optionalTask.get();
            dbTask.setStatus(StatusEnum.Done);
            dbTask = taskRepository.save(dbTask);
        }catch (BusinessNotFoundException bnf) {
            throw bnf;
        }catch (Exception ex) {
            throw new InternalServerException();
        }
        return dbTask;
    }

    public List<Task> getDoneTasks() {
        List<Task> tasks = null;
        try {
            tasks = taskRepository.findByStatus(StatusEnum.Done);
        }catch (Exception ex) {
            throw new InternalServerException();
        }
        return tasks;
    }
    // get tasks with high order priority
    public List<Task> getTasks() {
        List<Task> allByOrderByPriorityAsc;
        try {
            allByOrderByPriorityAsc = taskRepository.findAllByOrderByPriorityAsc();
        } catch (Exception ex) {
            throw new InternalServerException();
        }
        return allByOrderByPriorityAsc;
    }
}
