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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    private TaskService taskService;
    @BeforeEach
    void setUp() {
      taskService = new TaskService(taskRepository);
    }

    @DisplayName("( Create Task ) Should Test BadReqException For Malformed Task")
    @Test
    void shouldTestReqExceptionForMalformedReq() {
         TaskRequest taskRequest = null;
         Assertions.assertThrows(BadRequestException.class,()-> taskService.add(taskRequest));
    }

    @DisplayName("( Create Task ) Should Test InternalServerException For Unhandled Case")
    @Test
    void shouldTestInternalServerException() {
        when(taskRepository.save(any(Task.class))).thenThrow(InternalServerException.class);
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setPriority("High");
        taskRequest.setDescription("test");
        Assertions.assertThrows(InternalServerException.class,()-> taskService.add(taskRequest));
    }

    @DisplayName("( Create Task )Should Create Task")
    @Test
    void ShouldCreateTask() {
        Task task = new Task();
        task.setId(1);
        task.setDescription("test");
        task.setPriority(PriorityEnum.High);
        task.setStatus(StatusEnum.Pending);
        task.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setPriority("High");
        taskRequest.setDescription("test");
        Task dbTask = taskService.add(taskRequest);
        Assertions.assertAll(() -> Assertions.assertEquals(task.getId(),dbTask.getId()),
                () -> Assertions.assertEquals(task.getDescription(),dbTask.getDescription()),
                () -> Assertions.assertEquals(task.getPriority(),dbTask.getPriority()),
                () -> Assertions.assertEquals(task.getStatus(),dbTask.getStatus()),
                () -> Assertions.assertEquals(task.getCreatedAt(),dbTask.getCreatedAt()),
                () -> Assertions.assertEquals(task.getUpdatedAt(),dbTask.getUpdatedAt()));
    }

    @DisplayName("( Find Task ) Test Task Not Found")
    @Test
    void taskNotFound() {
        when(taskRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BusinessNotFoundException.class,() -> {
            taskService.getTask(5l);
        });
    }

    @DisplayName("( Find Task ) Test InternalSeverException")
    @Test
    void testInternalServerException() {
        when(taskRepository.findById(any())).thenThrow(RuntimeException.class);
        Assertions.assertThrows(InternalServerException.class,() -> {
            taskService.getTask(-1l);
        });
    }

    @DisplayName("( Find Task ) Test Find Task")
    @Test
    void testFindTask() {
        Task task = new Task();
        task.setId(1);
        task.setDescription("test");
        task.setPriority(PriorityEnum.High);
        task.setStatus(StatusEnum.Pending);
        task.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Assertions.assertEquals(task,taskService.getTask(1l));
    }




}