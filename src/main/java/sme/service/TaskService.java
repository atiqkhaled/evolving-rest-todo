package sme.service;

import org.springframework.stereotype.Service;
import sme.controller.dto.TaskRequest;
import sme.model.Task;
import sme.model._enum.PriorityEnum;
import sme.model._enum.StatusEnum;
import sme.repository.TaskRepository;
import sme.util.exceptions.BadRequestException;
import sme.util.exceptions.BusinessNotFoundException;
import sme.util.exceptions.InternalServerException;

import java.util.Optional;

@Service
public class TaskService {
    TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task add(TaskRequest taskRequest) {
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
}
