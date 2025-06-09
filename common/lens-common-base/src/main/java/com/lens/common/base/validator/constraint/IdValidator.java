package com.lens.common.base.validator.constraint;


import com.lens.common.base.constant.Constants;
import com.lens.common.base.utils.BaseStringUtils;
import com.lens.common.base.validator.annotion.IdValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * ID校验器，主要判断是否为空，并且长度是否为32
 *
 * @author 陌溪
 * @date 2019年12月4日22:48:43
 */
public class IdValidator implements ConstraintValidator<IdValid, String> {


    @Override
    public void initialize(IdValid constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || BaseStringUtils.isBlank(value) || BaseStringUtils.isEmpty(value.trim()) || value.length() != Constants.THIRTY_TWO) {
            return false;
        }
        return true;
    }
}
