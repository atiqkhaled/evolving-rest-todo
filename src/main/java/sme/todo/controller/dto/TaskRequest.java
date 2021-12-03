package sme.todo.controller.dto;

public class TaskRequest {
    private String description;
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
