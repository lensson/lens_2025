package com.lens.common.base.validator.constraint;


import com.lens.common.base.validator.annotion.LongNotNull;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 判断Long是否为空【校验器】
 *
 * @author 陌溪
 * @date 2019年12月4日13:16:06
 */
public class LongValidator implements ConstraintValidator<LongNotNull, Long> {


    @Override
    public void initialize(LongNotNull constraintAnnotation) {

    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return true;
    }
}
