package com.lens.common.base.validator.annotion;


import com.lens.common.base.validator.Messages;
import com.lens.common.base.validator.constraint.StringValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 判断字符串是否为空【注解】
 *
 * @author: 陌溪
 * @date: 2019年12月4日13:12:52
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StringValidator.class})
public @interface NotBlank {

    boolean required() default true;

    String message() default Messages.CK_NOT_BLANK_DEFAULT;

    String value() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
