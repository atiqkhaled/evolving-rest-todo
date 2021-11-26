package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sme.controller.dto.TaskRequest;
import sme.model.Task;
import sme.model._enum.PriorityEnum;
import sme.model._enum.StatusEnum;
import sme.repository.TaskRepository;
import sme.service.TaskService;
import sme.util.exceptions.BadRequestException;
import sme.util.exceptions.InterServerException;
import java.sql.Timestamp;
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

    @DisplayName("Should through badReqException for malformed task")
    @Test
    void shouldThroughBadReqExceptionForMalformedReq() {
         TaskRequest taskRequest = null;
         Assertions.assertThrows(BadRequestException.class,()-> taskService.add(taskRequest));
    }

    @DisplayName("Should through internal server exception for unhandled case")
    @Test
    void shouldThrowInternalServerException() {
        when(taskRepository.save(Mockito.any(Task.class))).thenThrow(InterServerException.class);
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setPriority("High");
        taskRequest.setDescription("test");
        Assertions.assertThrows(InterServerException.class,()-> taskService.add(taskRequest));
    }

    @DisplayName("create task")
    @Test
    void createTask() {
        Task task = new Task();
        task.setId(1);
        task.setDescription("test");
        task.setPriority(PriorityEnum.High);
        task.setStatus(StatusEnum.Pending);
        task.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);
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

}
