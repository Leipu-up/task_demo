package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 作者:zhanglei
 * @version 创建时间:2026/4/14 11:08
 * @Description 描述:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {
	private String title;
	private String description;
	private Long approverId;   // 指定的审批人ID
}
