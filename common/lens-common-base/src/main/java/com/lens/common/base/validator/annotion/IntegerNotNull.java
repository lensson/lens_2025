package com.lens.common.base.validator.annotion;



import com.lens.common.base.validator.Messages;
import com.lens.common.base.validator.constraint.IntegerValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 判断Integer是否为空【注解】
 *
 * @author 陌溪
 * @date 2019年12月4日13:12:52
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IntegerValidator.class})
public @interface IntegerNotNull {

    boolean required() default true;

    String message() default Messages.CK_NUMERIC_DEFAULT;

    String value() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
