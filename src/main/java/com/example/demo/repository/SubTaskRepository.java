package com.example.demo.repository;

import com.example.demo.entity.SubTask;
import com.example.demo.enums.SubTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 作者:zhanglei
 * @version 创建时间:2026/4/14 11:01
 * @Description 描述:
 */
public interface SubTaskRepository extends JpaRepository<SubTask, Long> {
	List<SubTask> findByParentTaskId(Long parentTaskId);
	long countByParentTaskIdAndStatus(Long parentTaskId, SubTaskStatus status);
}
