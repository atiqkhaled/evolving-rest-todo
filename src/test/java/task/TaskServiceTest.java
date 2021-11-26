package task;

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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    private TaskService taskService;
    Task task;
    TaskRequest expectedReq;
    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1);
        task.setDescription("test");
        task.setPriority(PriorityEnum.High);
        task.setStatus(StatusEnum.Pending);
        task.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        expectedReq = new TaskRequest();
        expectedReq.setPriority(PriorityEnum.Medium.name());
        expectedReq.setDescription("test 2");
        expectedReq.setStatus(StatusEnum.Done.name());
        taskService = new TaskService(taskRepository);
    }

    @DisplayName("( Create Task ) Should Test BadReqException For Malformed Task")
    @Test
    void shouldTestReqExceptionForMalformedReq() {
         TaskRequest taskRequest = null;
         Assertions.assertThrows(BadRequestException.class,()-> taskService.add(taskRequest));
    }

    @DisplayName("( Create Task )Should Create Task")
    @Test
    void ShouldCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        Task actualTask = taskService.add(expectedReq);
        Assertions.assertAll(() -> Assertions.assertEquals(task.getId(),actualTask.getId()),
                () -> Assertions.assertEquals(task.getDescription(),actualTask.getDescription()),
                () -> Assertions.assertEquals(task.getPriority(),actualTask.getPriority()),
                () -> Assertions.assertEquals(task.getStatus(),actualTask.getStatus()),
                () -> Assertions.assertEquals(task.getCreatedAt(),actualTask.getCreatedAt()),
                () -> Assertions.assertEquals(task.getUpdatedAt(),actualTask.getUpdatedAt()));
    }

    @DisplayName("( Create Task ) Test InternalServerException For Unhandled Case")
    @Test
    void shouldTestInternalServerException() {
        when(taskRepository.save(task)).thenThrow(InternalServerException.class);
        Assertions.assertThrows(InternalServerException.class,()-> taskService.add(expectedReq));
    }

    @DisplayName("( Get Task ) Task Not Found")
    @Test
    void taskNotFound() {
        when(taskRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BusinessNotFoundException.class,() -> {
            taskService.getTask(5l);
        });
    }

    @DisplayName("( Get Task ) Test InternalSeverException")
    @Test
    void testInternalServerException() {
        when(taskRepository.findById(any())).thenThrow(RuntimeException.class);
        Assertions.assertThrows(InternalServerException.class,() -> {
            taskService.getTask(-1l);
        });
    }

    @DisplayName("( Get Task ) Find Task")
    @Test
    void testFindTask() {
        when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Task actualTask = taskService.getTask(1l);
        Assertions.assertEquals(task.getId(),actualTask.getId());
    }

    @DisplayName("( Update Task ) Task Not Found")
    @Test
    void updateTaskNotFound() {
        when(taskRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BusinessNotFoundException.class,() -> {
            taskService.updateTask(expectedReq,5l);
        });
    }
    @DisplayName("( Update Task ) Test InternalServerException while update task")
    @Test
    void updateTaskInternalServerException() {
        when(taskRepository.findById(5l)).thenThrow(RuntimeException.class);
        Assertions.assertThrows(InternalServerException.class,() -> {
            taskService.updateTask(expectedReq,5l);
        });
    }

    @DisplayName("( Update Task ) test copy of Task")
    @Test
    void testTaskCopy() {
        Task actualTask = task.copy(expectedReq);
        Assertions.assertAll(() -> Assertions.assertEquals(expectedReq.getStatus(),actualTask.getStatus().name()),
                () -> Assertions.assertEquals(expectedReq.getDescription(),actualTask.getDescription()),
                () -> Assertions.assertEquals(expectedReq.getPriority(),actualTask.getPriority().name()),
                () -> Assertions.assertEquals(task.getId(),actualTask.getId()));

    }

    @DisplayName("( Update Task ) Test Task updated")
    @Test
    void updateTask() {
        when(taskRepository.findById(5l)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        Task actualTask = taskService.updateTask(expectedReq,5l);
        Assertions.assertAll(() -> Assertions.assertEquals(expectedReq.getStatus(),actualTask.getStatus().name()),
                () -> Assertions.assertEquals(expectedReq.getDescription(),actualTask.getDescription()),
                () -> Assertions.assertEquals(expectedReq.getPriority(),actualTask.getPriority().name()),
                () -> Assertions.assertEquals(task.getId(),actualTask.getId()));
    }

    @DisplayName("( Delete Task ) Test Task Not Found for delete")
    @Test
    void deleteTaskNotFound() {
        when(taskRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BusinessNotFoundException.class,() -> {
            taskService.delete(5l);
        });
    }
    @DisplayName("( Delete Task ) Delete Task")
    @Test
    void deleteTask() {
        when(taskRepository.findById(5l)).thenReturn(Optional.of(task));
        taskService.delete(5l);
        verify(taskRepository, times(1)).delete(task);
    }

    @DisplayName("( MarkAsDone ) task not found")
    @Test
    void taskNotFoundForMarkAsDone() {
        when(taskRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BusinessNotFoundException.class,() -> taskService.markAsDone(5l));
    }

    @DisplayName("( MarkAsDone ) Mark task as done")
    @Test
    void testMarkTaskAsDone() {
        when(taskRepository.findById(5l)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        Task actual = taskService.markAsDone(5l);
        Assertions.assertEquals(StatusEnum.Done,actual.getStatus());
    }




}
