package sme.todo.controller.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class TaskRequest {
    @NotNull(message = "key should be description")
    private String description;
    @NotNull(message = "key should be priority")
    @Pattern(regexp = "^(\\s*|high|medium|low)$",
            message = "status must be high|medium|low")
    private String priority;

    public TaskRequest() {
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
