package sme.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sme.controller.dto.TaskRequest;
import sme.model._enum.PriorityEnum;
import sme.model._enum.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private String description;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PriorityEnum priority;
    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    @NotNull
    @CreationTimestamp
    private Timestamp createdAt;
    @NotNull
    @UpdateTimestamp
    private Timestamp updatedAt;

    public Task() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PriorityEnum getPriority() {
        return priority;
    }

    public void setPriority(PriorityEnum priority) {
        this.priority = priority;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Task copy(TaskRequest taskRequest) {
        this.setPriority(PriorityEnum.valueOf(taskRequest.getPriority()));
        this.setStatus(StatusEnum.valueOf(taskRequest.getStatus()));
        this.setDescription(taskRequest.getDescription());
        return this;
    }

}
