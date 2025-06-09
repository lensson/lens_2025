package com.lens.common.core.exception;


import com.lens.common.base.constant.ErrorCode;
import com.lens.common.base.exception.BaseExceptionUtil;
import com.lens.common.base.exception.exceptionType.BusinessException;
import com.lens.common.base.utils.BaseStringUtils;
import com.lens.common.core.vo.Result;

/**
 * 异常工具类
 *
 * @Author: 陌溪
 * @Date: 2019年12月4日22:47:08
 */
public class ExceptionUtil extends BaseExceptionUtil {



    /**
     * 业务不需回滚，设置result返回
     *
     * @param message
     */
    public static Result setResult(String message) {
        return Result.createWithErrorMessage(BaseStringUtils.isBlank(message) ? "系统异常，请稍候..." : message, ErrorCode.OPERATION_FAIL);
    }
}
