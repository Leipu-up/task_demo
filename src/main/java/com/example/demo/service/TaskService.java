package com.example.demo.service;

import com.example.demo.dto.TaskCreateRequest;
import com.example.demo.entity.SubTask;
import com.example.demo.entity.Task;
import com.example.demo.enums.SubTaskStatus;
import com.example.demo.enums.TaskStatus;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.SubTaskRepository;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 作者:zhanglei
 * @version 创建时间:2026/4/14 11:02
 * @Description 描述:
 */
@Service
@Transactional
public class TaskService {
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private SubTaskRepository subTaskRepository;
	@Autowired
	private SimpMessagingTemplate messagingTemplate; // 用于WebSocket

	// 创建任务（状态为DRAFT）
	public Task createTask(TaskCreateRequest request, Long creatorId) {
		Task task = new Task();
		task.setTitle(request.getTitle());
		task.setDescription(request.getDescription());
		task.setCreatorId(creatorId);
		task.setApproverId(request.getApproverId());
		task.setStatus(TaskStatus.DRAFT);
		return taskRepository.save(task);
	}

	// 提交任务 DRAFT -> PENDING
	public Task submitTask(Long taskId, Long userId) {
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new BusinessException("任务不存在"));
		if (!task.getCreatorId().equals(userId)) {
			throw new BusinessException("只有创建者可以提交任务");
		}
		if (task.getStatus() != TaskStatus.DRAFT) {
			throw new BusinessException("只有草稿状态的任务可以提交");
		}
		task.setStatus(TaskStatus.PENDING);
		Task saved = taskRepository.save(task);
		// WebSocket推送
		messagingTemplate.convertAndSend("/topic/task/" + taskId, "任务状态变更为PENDING");
		return saved;
	}

	// 批准
	public Task approveTask(Long taskId, Long approverId) {
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new BusinessException("任务不存在"));
		if (!task.getApproverId().equals(approverId)) {
			throw new BusinessException("您不是该任务的审批人");
		}
		if (task.getStatus() != TaskStatus.PENDING) {
			throw new BusinessException("只有待审批状态的任务可以批准");
		}
		task.setStatus(TaskStatus.APPROVED);
		Task saved = taskRepository.save(task);
		messagingTemplate.convertAndSend("/topic/task/" + taskId, "任务已批准");
		return saved;
	}

	// 驳回
	public Task rejectTask(Long taskId, Long approverId) {
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new BusinessException("任务不存在"));
		if (!task.getApproverId().equals(approverId)) {
			throw new BusinessException("您不是该任务的审批人");
		}
		if (task.getStatus() != TaskStatus.PENDING) {
			throw new BusinessException("只有待审批状态的任务可以驳回");
		}
		task.setStatus(TaskStatus.DRAFT);
		Task saved = taskRepository.save(task);
		messagingTemplate.convertAndSend("/topic/task/" + taskId, "任务被驳回，状态变更为DRAFT");
		return saved;
	}

	// 拆分子任务（父任务必须是APPROVED，且由创建者操作）
	public List<SubTask> splitSubtasks(Long parentTaskId, List<Long> assigneeIds, Long creatorId) {
		Task parentTask = taskRepository.findById(parentTaskId)
				.orElseThrow(() -> new BusinessException("父任务不存在"));
		if (!parentTask.getCreatorId().equals(creatorId)) {
			throw new BusinessException("只有创建者可以拆分子任务");
		}
		if (parentTask.getStatus() != TaskStatus.APPROVED) {
			throw new BusinessException("只有已批准的任务才能拆分子任务");
		}
		if (assigneeIds.size() < 2 || assigneeIds.size() > 3) {
			throw new BusinessException("子任务数量必须为2-3个");
		}
		List<SubTask> subTasks = new ArrayList<>();
		for (Long assigneeId : assigneeIds) {
			SubTask subTask = new SubTask();
			subTask.setParentTaskId(parentTaskId);
			subTask.setAssigneeId(assigneeId);
			subTask.setStatus(SubTaskStatus.PENDING);
			subTasks.add(subTaskRepository.save(subTask));
		}
		return subTasks;
	}

	// 完成子任务
	public SubTask completeSubTask(Long subTaskId, Long assigneeId) {
		SubTask subTask = subTaskRepository.findById(subTaskId)
				.orElseThrow(() -> new BusinessException("子任务不存在"));
		if (!subTask.getAssigneeId().equals(assigneeId)) {
			throw new BusinessException("您不是该子任务的处理人");
		}
		if (subTask.getStatus() == SubTaskStatus.COMPLETED) {
			throw new BusinessException("子任务已完成，不能重复完成");
		}
		subTask.setStatus(SubTaskStatus.COMPLETED);
		SubTask saved = subTaskRepository.save(subTask);

		// 检查父任务的所有子任务是否都已完成
		Long parentTaskId = saved.getParentTaskId();
		long completedCount = subTaskRepository.countByParentTaskIdAndStatus(parentTaskId, SubTaskStatus.COMPLETED);
		long total = subTaskRepository.findByParentTaskId(parentTaskId).size();
		if (completedCount == total) {
			Task parentTask = taskRepository.findById(parentTaskId).get();
			parentTask.setStatus(TaskStatus.COMPLETED);
			taskRepository.save(parentTask);
			messagingTemplate.convertAndSend("/topic/task/" + parentTaskId, "所有子任务完成，父任务已完成");
		}
		return saved;
	}
}
