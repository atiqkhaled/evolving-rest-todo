package sme.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sme.controller.dto.TaskRequest;
import sme.model.Task;
import sme.model._enum.PriorityEnum;
import sme.model._enum.StatusEnum;
import sme.repository.TaskRepository;
import sme.service.TaskService;
import sme.util.exceptions.BadRequestException;
import sme.util.exceptions.BusinessNotFoundException;
import sme.util.exceptions.InternalServerException;

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
        task1.setPriority(PriorityEnum.High);
        tasks.add(task1);
        task1.setStatus(StatusEnum.Done);
        Task task2 = new Task();
        task2.setPriority(PriorityEnum.High);
        task2.setStatus(StatusEnum.Done);
        tasks.add(task2);
        // single task
        mockTask = new Task();
        mockTask.setId(1);
        mockTask.setDescription("test");
        mockTask.setPriority(PriorityEnum.High);
        mockTask.setStatus(StatusEnum.Pending);
        mockTask.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        mockTask.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        // task request
        taskReq = new TaskRequest();
        taskReq.setPriority(PriorityEnum.Medium.name());
        taskReq.setDescription("test 2");
        taskReq.setStatus(StatusEnum.Done.name());
        // construct taskService with mock repo.
        taskService = new TaskService(taskRepository);
    }

    @DisplayName("( When Create Task ) Check BadReqException")
    @Test
    void shouldTestReqExceptionForMalformedReq() {
         TaskRequest taskRequest = null;
         Assertions.assertThrows(BadRequestException.class,()-> taskService.add(taskRequest));
    }

    @DisplayName("( Create Task ) Check Task Added")
    @Test
    void ShouldCreateTask() {
        Task task = new Task();
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        Task actualTask = taskService.add(taskReq);
        Assertions.assertNotNull(actualTask.getId());
    }

    @DisplayName("( When Create Task ) Check InternalServerException")
    @Test
    void shouldTestInternalServerException() {
        when(taskRepository.save(mockTask)).thenThrow(InternalServerException.class);
        Assertions.assertThrows(InternalServerException.class,()-> taskService.add(taskReq));
    }

    @DisplayName("( When Get Task ) Check Task Not Found")
    @Test
    void taskNotFound() {
        when(taskRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BusinessNotFoundException.class,() -> {
            taskService.getTask(5l);
        });
    }

    @DisplayName("( When Get Task ) Check InternalSeverException")
    @Test
    void testInternalServerException() {
        when(taskRepository.findById(any())).thenThrow(RuntimeException.class);
        Assertions.assertThrows(InternalServerException.class,() -> {
            taskService.getTask(-1l);
        });
    }

    @DisplayName("( When Get Task ) Check Task Found")
    @Test
    void testFindTask() {
        when(taskRepository.findById(1l)).thenReturn(Optional.of(mockTask));
        Task actualTask = taskService.getTask(1l);
        Assertions.assertEquals(mockTask.getId(),actualTask.getId());
    }

    @DisplayName("( When Update Task ) Check Task Not Found")
    @Test
    void updateTaskNotFound() {
        when(taskRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BusinessNotFoundException.class,() -> {
            taskService.updateTask(taskReq,5l);
        });
    }
    @DisplayName("( When Update Task ) Check InternalServerException")
    @Test
    void updateTaskInternalServerException() {
        when(taskRepository.findById(5l)).thenThrow(RuntimeException.class);
        Assertions.assertThrows(InternalServerException.class,() -> {
            taskService.updateTask(taskReq,5l);
        });
    }

    @DisplayName("( Update Task ) test copy of Task")
    @Test
    void testTaskCopy() {
        Task actualTask = mockTask.copy(taskReq);
        Assertions.assertAll(() -> Assertions.assertEquals(taskReq.getStatus(),actualTask.getStatus().name()),
                () -> Assertions.assertEquals(taskReq.getDescription(),actualTask.getDescription()),
                () -> Assertions.assertEquals(taskReq.getPriority(),actualTask.getPriority().name()),
                () -> Assertions.assertEquals(mockTask.getId(),actualTask.getId()));

    }

    @DisplayName("( Update Task ) Check Task updated")
    @Test
    void updateTask() {
        when(taskRepository.findById(5l)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(mockTask)).thenReturn(mockTask);
        Task actualTask = taskService.updateTask(taskReq,5l);
        Assertions.assertAll(() -> Assertions.assertEquals(taskReq.getStatus(),actualTask.getStatus().name()),
                () -> Assertions.assertEquals(taskReq.getDescription(),actualTask.getDescription()),
                () -> Assertions.assertEquals(taskReq.getPriority(),actualTask.getPriority().name()),
                () -> Assertions.assertEquals(mockTask.getId(),actualTask.getId()));
    }

    @DisplayName("( When Delete Task ) Check Task Not Found")
    @Test
    void deleteTaskNotFound() {
        when(taskRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BusinessNotFoundException.class,() -> {
            taskService.delete(5l);
        });
    }
    @DisplayName("( When Delete Task ) Check Task Deleted")
    @Test
    void deleteTask() {
        when(taskRepository.findById(5l)).thenReturn(Optional.of(mockTask));
        taskService.delete(5l);
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
    @DisplayName("( When MarkAsDone ) Check InternalServerException")
    @Test
    void internalServerExceptionForMarkAsDone() {
        when(taskRepository.findById(any())).thenThrow(RuntimeException.class);
        Assertions.assertThrows(InternalServerException.class,() -> taskService.markAsDone(5l));
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
    @DisplayName("( When GetDoneTasks ) Check InternalServerException")
    @Test
    void internalServerExceptionForGetDoneTasks() {
        when(taskRepository.findByStatus(any())).thenThrow(RuntimeException.class);
        Assertions.assertThrows(InternalServerException.class,() -> taskService.getDoneTasks());
    }

    @DisplayName("( When Get Tasks ) Check All Tasks with Priority High Order !")
    @Test
    void getAllTasksHigherOrder() {
        when(taskRepository.findAllByOrderByPriorityAsc()).thenReturn(tasks);
        List<Task> actual = taskService.getTasks();
        Assertions.assertEquals(tasks,actual);
        Assertions.assertEquals(tasks.size(),actual.size());
        Assertions.assertEquals(tasks.iterator().next().getPriority().name(),
                actual.iterator().next().getPriority().name());
    }

    @DisplayName("( When Get Tasks ) Check InternalServerException")
    @Test
    void internalServerExceptionForGetTasks() {
        when(taskRepository.findAllByOrderByPriorityAsc()).thenThrow(RuntimeException.class);
        Assertions.assertThrows(InternalServerException.class,() -> taskService.getTasks());
    }





}
