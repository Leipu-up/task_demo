package com.example.demo.exception;

/**
 * @author 作者:zhanglei
 * @version 创建时间:2026/4/14 11:07
 * @Description 描述:
 */
public class BusinessException extends RuntimeException {
	public BusinessException(String message) {
		super(message);
	}
}
