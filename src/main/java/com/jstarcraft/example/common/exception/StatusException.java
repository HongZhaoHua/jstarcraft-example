package com.jstarcraft.example.common.exception;

/**
 * 状态异常
 * 
 * @author Birdy
 */
public class StatusException extends RuntimeException {

    private static final long serialVersionUID = -6811467494695446778L;

    private int status;

    protected StatusException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
