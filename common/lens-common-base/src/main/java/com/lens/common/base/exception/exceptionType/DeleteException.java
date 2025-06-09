package com.lens.common.base.exception.exceptionType;


import com.lens.common.base.constant.BaseMessageConstants;
import com.lens.common.base.constant.ErrorCode;

import java.io.Serializable;

/**
 * 自定义删除操作相关的异常
 *
 * @author 陌溪
 * @date 2020年9月9日16:41:26
 */
public class DeleteException extends RuntimeException implements Serializable {

    /**
     * 异常状态码
     */
    private String code;

    public DeleteException() {
        super(BaseMessageConstants.DELETE_DEFAULT_ERROR);
        this.code = ErrorCode.DELETE_DEFAULT_ERROR;
    }

    public DeleteException(String message, Throwable cause) {
        super(message, cause);
        this.code = ErrorCode.DELETE_DEFAULT_ERROR;
    }

    public DeleteException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public DeleteException(String message) {
        super(message);
        this.code = ErrorCode.DELETE_DEFAULT_ERROR;
    }

    public DeleteException(String code, String message) {
        super(message);
        this.code = code;
    }

    public DeleteException(Throwable cause) {
        super(cause);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
