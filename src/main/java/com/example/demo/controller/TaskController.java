package com.example.demo.controller;

import com.example.demo.dto.SubtaskCreateRequest;
import com.example.demo.dto.TaskCreateRequest;
import com.example.demo.entity.SubTask;
import com.example.demo.entity.Task;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 作者:zhanglei
 * @version 创建时间:2026/4/14 11:03
 * @Description 描述:
 */
@RestController
@RequestMapping("/api")
public class TaskController {

	@Autowired
	private TaskService taskService;

	// 用户名到用户ID的映射（实际项目应从数据库查询）
	private static final Map<String, Long> USERNAME_TO_ID = new HashMap<>();
	static {
		USERNAME_TO_ID.put("user1", 1L);
		USERNAME_TO_ID.put("user2", 2L);
		USERNAME_TO_ID.put("user3", 3L);
		USERNAME_TO_ID.put("user4", 4L);
		USERNAME_TO_ID.put("admin", 999L);
	}

	/**
	 * 获取当前登录用户的ID
	 */
	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new BusinessException("用户未认证");
		}
		String username = authentication.getName();
		Long userId = USERNAME_TO_ID.get(username);
		if (userId == null) {
			throw new BusinessException("未知用户: " + username);
		}
		return userId;
	}

	// 1. 创建任务
	@PostMapping("/tasks")
	public ResponseEntity<Task> createTask(@RequestBody TaskCreateRequest request) {
		Long creatorId = getCurrentUserId();
		Task task = taskService.createTask(request, creatorId);
		return ResponseEntity.ok(task);
	}

	// 2. 提交任务（草稿 -> 待审批）
	@PutMapping("/tasks/{id}/submit")
	public ResponseEntity<Task> submitTask(@PathVariable Long id) {
		Long userId = getCurrentUserId();
		Task task = taskService.submitTask(id, userId);
		return ResponseEntity.ok(task);
	}

	// 3. 批准任务
	@PutMapping("/tasks/{id}/approve")
	public ResponseEntity<Task> approveTask(@PathVariable Long id) {
		Long approverId = getCurrentUserId();
		Task task = taskService.approveTask(id, approverId);
		return ResponseEntity.ok(task);
	}

	// 4. 驳回任务
	@PutMapping("/tasks/{id}/reject")
	public ResponseEntity<Task> rejectTask(@PathVariable Long id) {
		Long approverId = getCurrentUserId();
		Task task = taskService.rejectTask(id, approverId);
		return ResponseEntity.ok(task);
	}

	// 5. 拆分子任务
	@PostMapping("/tasks/{id}/subtasks")
	public ResponseEntity<List<SubTask>> createSubtasks(@PathVariable Long id,
														@RequestBody SubtaskCreateRequest request) {
		Long userId = getCurrentUserId();
		List<SubTask> subTasks = taskService.splitSubtasks(id, request.getAssigneeIds(), userId);
		return ResponseEntity.ok(subTasks);
	}

	// 6. 完成子任务
	@PutMapping("/subtasks/{id}/complete")
	public ResponseEntity<SubTask> completeSubTask(@PathVariable Long id) {
		Long assigneeId = getCurrentUserId();
		SubTask subTask = taskService.completeSubTask(id, assigneeId);
		return ResponseEntity.ok(subTask);
	}
}
