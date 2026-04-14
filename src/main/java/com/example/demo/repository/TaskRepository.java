package com.example.demo.repository;

import com.example.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author 作者:zhanglei
 * @version 创建时间:2026/4/14 11:00
 * @Description 描述:
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
	List<Task> findByParentId(Long parentId);
	Optional<Task> findByIdAndCreatorId(Long id, Long creatorId);
}
