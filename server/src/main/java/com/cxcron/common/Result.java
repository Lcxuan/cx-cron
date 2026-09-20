package com.cxcron.common;

import com.cxcron.enums.exception.ErrorCode;
import com.cxcron.enums.exception.GlobalErrorCodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "统一返回参数")
public class Result<T> {
    @Schema(description = "状态码")
    private String code;

    @Schema(description = "描述信息")
    private String msg;

    @Schema(description = "返回数据")
    private T data;

    public static <T> Result<T> build(String code, String msg, T data) {
        Result<T> result = new Result<>();
        result.code = code;
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static <T> Result<T> success(T data) {
        return build(GlobalErrorCodeConstants.SUCCESS.getCode(), GlobalErrorCodeConstants.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(String code, String msg) {
        return build(code, msg, null);
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }
}
