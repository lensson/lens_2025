package com.lens.common.base.validator.constraint;


import com.lens.common.base.utils.BaseStringUtils;
import com.lens.common.base.validator.annotion.Range;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 字符串范围约束，限制长度【校验器】
 *
 * @author 陌溪
 * @date 2019年12月4日13:17:03
 */
public class RangValidator implements ConstraintValidator<Range, String> {
    private long min;
    private long max;

    @Override
    public void initialize(Range constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (null == value || BaseStringUtils.isBlank(value)) {
            return min == 0;
        }
        return value.length() >= min && value.length() <= max;
    }
}
