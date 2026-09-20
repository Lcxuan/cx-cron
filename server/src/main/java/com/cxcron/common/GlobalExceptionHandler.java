package com.cxcron.common;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.cxcron.enums.exception.BusinessException;
import com.cxcron.enums.exception.GlobalErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器，统一返回 Result 响应结构。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常，返回业务定义的错误码。
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException exception) {
        log.warn("业务异常：{}", exception.getMessage());
        return Result.error(exception.getErrorCode());
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleNotLoginException(NotLoginException exception) {
        log.warn("未登录访问：{}", exception.getMessage());
        return Result.error(GlobalErrorCodeConstants.UNAUTHORIZED);
    }

    @ExceptionHandler({NotRoleException.class, NotPermissionException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleForbiddenException(Exception exception) {
        log.warn("无权访问：{}", exception.getMessage());
        return Result.error(GlobalErrorCodeConstants.FORBIDDEN);
    }

    /**
     * 处理请求参数校验或绑定失败。
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(Exception exception) {
        log.warn("请求参数校验失败：{}", exception.getMessage());
        BindingResult bindingResult = exception instanceof MethodArgumentNotValidException methodException
                ? methodException.getBindingResult()
                : ((BindException) exception).getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String message = fieldError == null
                ? GlobalErrorCodeConstants.BAD_REQUEST.getMsg()
                : fieldError.getDefaultMessage();
        return Result.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理不支持的 HTTP 请求方法。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception) {
        log.warn("不支持的请求方法：{}", exception.getMessage());
        return Result.error(GlobalErrorCodeConstants.METHOD_NOT_ALLOWED);
    }

    /**
     * 处理不存在的静态资源请求。
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoResourceFoundException(NoResourceFoundException exception) {
        return Result.error(GlobalErrorCodeConstants.NOT_FOUND);
    }

    /**
     * 兜底处理未预期的系统异常。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception exception) {
        log.error("未处理的系统异常", exception);
        return Result.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
    }
}
