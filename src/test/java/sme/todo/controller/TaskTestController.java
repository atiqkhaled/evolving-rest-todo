package sme.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sme.todo.controller.dto.TaskRequest;
import sme.todo.model.Task;
import sme.todo.model._enum.PriorityEnum;
import sme.todo.model._enum.StatusEnum;
import sme.todo.service.TaskService;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@WebMvcTest(TaskController.class)
public class TaskTestController {
    @MockBean
    TaskService taskService;
    @Autowired
    MockMvc mockMvc;
    String taskJson = "{\"description\" : \"test 5\",\"priority\" : \"high\"}";
    Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1);
        task.setPriority(PriorityEnum.high);
        task.setStatus(StatusEnum.Pending);
        task.setCreatedAt(new Timestamp(new Date().getTime()));
        task.setUpdatedAt(new Timestamp(new Date().getTime()));
    }

    @Test
    void addTask() throws Exception {
        when(taskService.addTask(any(TaskRequest.class))).thenReturn(task);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tasks")
                .accept(MediaType.APPLICATION_JSON).content(taskJson)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals("http://localhost/tasks/1",
                response.getHeader(HttpHeaders.LOCATION));

    }

    @Test
    void checkUpdateTask() throws Exception {
        when(taskService.updateTask(Mockito.any(TaskRequest.class),Mockito.anyLong())).thenReturn(task);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/tasks/1")
                .accept(MediaType.APPLICATION_JSON).content(taskJson)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void checkMarkDone() throws Exception {
        when(taskService.markAsDone(Mockito.anyLong())).thenReturn(task);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/tasks/1/mark-done/")
                .accept(MediaType.APPLICATION_JSON).content(taskJson)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }
    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(Mockito.anyLong());
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/tasks/1");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

}
