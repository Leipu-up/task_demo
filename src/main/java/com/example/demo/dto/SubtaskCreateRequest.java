package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 作者:zhanglei
 * @version 创建时间:2026/4/14 11:08
 * @Description 描述:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskCreateRequest {
	private List<Long> assigneeIds;   // 子任务处理人ID列表，数量2~3
}
