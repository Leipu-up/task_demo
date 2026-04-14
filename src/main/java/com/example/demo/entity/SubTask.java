package com.example.demo.entity;

import com.example.demo.enums.SubTaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author 作者:zhanglei
 * @version 创建时间:2026/4/14 10:52
 * @Description 描述:
 */
@Entity
@Table(name = "sub_task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubTask {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long parentTaskId;
	private Long assigneeId;
	@Enumerated(EnumType.STRING)
	private SubTaskStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		status = SubTaskStatus.PENDING;
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
