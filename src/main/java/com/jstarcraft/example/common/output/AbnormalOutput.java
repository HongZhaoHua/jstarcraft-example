package com.jstarcraft.example.common.output;

import com.jstarcraft.example.common.exception.StatusException;

/**
 * 异常消息
 * 
 * @author Birdy
 *
 */
public class AbnormalOutput {

	private String clazz;

	private int status;

	private String description;

	public AbnormalOutput(StatusException exception) {
		this.clazz = exception.getClass().getSimpleName();
		this.status = exception.getStatus();
		this.description = exception.getMessage();
	}

	public String getClazz() {
		return clazz;
	}

	public int getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

}
