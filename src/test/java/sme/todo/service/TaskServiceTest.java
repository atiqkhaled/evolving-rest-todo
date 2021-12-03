package sme.todo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sme.todo.controller.dto.TaskRequest;
import sme.todo.model.Task;
import sme.todo.model._enum.PriorityEnum;
import sme.todo.model._enum.StatusEnum;
import sme.todo.repository.TaskRepository;
import sme.todo.exceptions.BadRequestException;
import sme.todo.exceptions.BusinessNotFoundException;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    private TaskService taskService;
    Task mockTask;
    TaskRequest taskReq;
    List<Task> tasks = new ArrayList<>();
    @BeforeEach
    void setUp() {
        // for list task
        Task task1 = new Task();
        task1.setPriority(PriorityEnum.high);
        tasks.add(task1);
        task1.setStatus(StatusEnum.Done);
        Task task2 = new Task();
        task2.setPriority(PriorityEnum.high);
        task2.setStatus(StatusEnum.Done);
        tasks.add(task2);
        // single task
        mockTask = new Task();
        mockTask.setId(1);
        mockTask.setDescription("test");
        mockTask.setPriority(PriorityEnum.high);
        mockTask.setStatus(StatusEnum.Pending);
        mockTask.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        mockTask.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        // task request
        taskReq = new TaskRequest();
        taskReq.setPriority(PriorityEnum.medium.name());
        taskReq.setDescription("test 2");
        // initialize taskService with mock repo.
        taskService = new TaskService(taskRepository);
    }

    @DisplayName("( When Create Task ) Check BadReqException")
    @Test
    void shouldTestReqExceptionForMalformedReq() {
         TaskRequest taskRequest = new TaskRequest();
         Assertions.assertThrows(BadRequestException.class,()-> taskService.addTask(taskRequest));
    }

    @DisplayName("( Create Task ) Check Task Added")
    @Test
    void ShouldCreateTask() {
        Task task = new Task();
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        Task actualTask = taskService.addTask(taskReq);
        Assertions.assertNotNull(actualTask.getId());
    }

    @DisplayName("( When Get Task ) Check Task Not Found")
    @Test
    void taskNotFound() {
        when(taskRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BusinessNotFoundException.class,() -> {
            taskService.getTask(5l);
        });
    }

    @DisplayName("( When Get Task ) Check Task Found")
    @Test
    void testFindTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        Task actualTask = taskService.getTask(1l);
        Assertions.assertEquals(mockTask.getId(),actualTask.getId());
    }

    @DisplayName("( When Update Task ) Check Task Not Found")
    @Test
    void updateTaskNotFound() {
        when(taskRepository.findById(5l)).thenReturn(Optional.empty());
        Assertions.assertThrows(BusinessNotFoundException.class,() -> {
            taskService.updateTask(taskReq, 5L);
        });
    }

    @DisplayName("( Update Task ) Check Task updated")
    @Test
    void updateTask() {
        when(taskRepository.findById(5l)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(mockTask)).thenReturn(mockTask);
        Task actualTask = taskService.updateTask(taskReq,5l);
        Assertions.assertAll(() -> Assertions.assertEquals(taskReq.getDescription(),actualTask.getDescription()),
                () -> Assertions.assertEquals(taskReq.getPriority(),actualTask.getPriority().name()),
                () -> Assertions.assertEquals(mockTask.getId(),actualTask.getId()),
                () -> Assertions.assertEquals(mockTask.getUpdatedAt().getTime(),
                        actualTask.getUpdatedAt().getTime()),
                () -> Assertions.assertEquals(mockTask.getCreatedAt().getTime(),
                        actualTask.getCreatedAt().getTime()));
    }

    @DisplayName("( When Delete Task ) Check Task Not Found")
    @Test
    void deleteTaskNotFound() {
        when(taskRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BusinessNotFoundException.class,() -> {
            taskService.deleteTask(5l);
        });
    }
    @DisplayName("( When Delete Task ) Check Task Deleted")
    @Test
    void deleteTask() {
        when(taskRepository.findById(5l)).thenReturn(Optional.of(mockTask));
        taskService.deleteTask(5l);
        verify(taskRepository, times(1)).delete(mockTask);
    }



    @DisplayName("( When MarkAsDone ) Check task not found")
    @Test
    void taskNotFoundForMarkAsDone() {
        when(taskRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BusinessNotFoundException.class,() -> taskService.markAsDone(5l));
    }

    @DisplayName("( When MarkAsDone ) Check Mark task as done")
    @Test
    void testMarkTaskAsDone() {
        Task task = new Task();
        task.setStatus(StatusEnum.Pending);
        when(taskRepository.findById(5l)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        Task actual = taskService.markAsDone(5l);
        Assertions.assertEquals(StatusEnum.Done,actual.getStatus());
    }

    @DisplayName("( When GetDoneTasks ) Check All Tasks Which Are Done !")
    @Test
    void getAllTasksWhichDone() {
        when(taskRepository.findByStatus(StatusEnum.Done)).thenReturn(tasks);
        List<Task> actual = taskService.getDoneTasks();
        Assertions.assertEquals(tasks,actual);
        Assertions.assertEquals(tasks.size(),actual.size());
        Assertions.assertEquals(tasks.iterator().next().getStatus().name(),
                actual.iterator().next().getStatus().name());
    }

    @DisplayName("( When Get Tasks ) Check All Tasks with Priority High Order !")
    @Test
    void getAllTasksHigherOrder() {
        when(taskRepository.findAllByOrderByPriorityAsc()).thenReturn(tasks);
        List<Task> actual = taskService.getTasks();
        Assertions.assertEquals(tasks.size(),actual.size());
        Assertions.assertEquals(tasks.iterator().next().getPriority().name(),
                actual.iterator().next().getPriority().name());
    }





}
