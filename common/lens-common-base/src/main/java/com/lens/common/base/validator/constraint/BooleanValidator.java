package com.lens.common.base.validator.constraint;


import com.lens.common.base.validator.annotion.BooleanNotNULL;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 判断Boolean类型是否为空【校验器】
 *
 * @author 陌溪
 * @date 2019年12月4日13:16:06
 */
public class BooleanValidator implements ConstraintValidator<BooleanNotNULL, Boolean> {

    @Override
    public void initialize(BooleanNotNULL constraintAnnotation) {

    }

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return true;
    }
}
