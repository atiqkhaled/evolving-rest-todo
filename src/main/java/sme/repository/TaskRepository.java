package sme.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sme.model.Task;
import sme.model._enum.StatusEnum;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task,Long> {
    List<Task> findByStatus(StatusEnum statusEnum);
    List<Task> findAllByOrderByPriorityAsc();
}
