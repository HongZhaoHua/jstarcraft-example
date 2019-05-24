package com.jstarcraft.example.common.output;

/**
 * 正常消息
 * 
 * @author Birdy
 *
 * @param <T>
 */
public class NormalOutput<T> {

	private T content;

	NormalOutput() {
	}

	public NormalOutput(T content) {
		this.content = content;
	}

	public T getContent() {
		return content;
	}

}
