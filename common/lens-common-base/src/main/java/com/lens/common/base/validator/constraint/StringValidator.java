package com.lens.common.base.validator.constraint;


import com.lens.common.base.utils.BaseStringUtils;
import com.lens.common.base.validator.annotion.NotBlank;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 判断是否为空字符串【校验器】
 *
 * @author 陌溪
 * @date 2019年12月4日13:17:17
 */
public class StringValidator implements ConstraintValidator<NotBlank, String> {
    @Override
    public void initialize(NotBlank constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || BaseStringUtils.isBlank(value) || BaseStringUtils.isEmpty(value.trim())) {
            return false;
        }
        return true;
    }
}
