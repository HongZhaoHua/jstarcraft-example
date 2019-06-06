package com.jstarcraft.example;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jstarcraft.example.common.exception.StatusException;
import com.jstarcraft.example.common.output.AbnormalOutput;

/**
 * 统一异常处理器
 * 
 * @author Birdy
 *
 */
@ControllerAdvice
public class ExampleExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = StatusException.class)
    public AbnormalOutput exceptionHandler(StatusException exception) {
        AbnormalOutput message = new AbnormalOutput(exception);
        return message;
    }

}
