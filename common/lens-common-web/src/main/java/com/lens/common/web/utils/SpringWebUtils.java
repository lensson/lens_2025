package com.lens.common.web.utils;


import com.lens.common.core.utils.SpringUtils;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;
import java.util.Locale;

/**
 * @Author zhenac
 * @Created 6/5/25 1:07 PM
 */

public class SpringWebUtils extends SpringUtils {

    public static HttpServletRequest getCurrentReq() {
        ServletRequestAttributes requestAttrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttrs == null) {
            return null;
        }
        return requestAttrs.getRequest();
    }

    public static String getMessage(String code, Object... args) {
        LocaleResolver localeResolver = getBean(LocaleResolver.class);
        Locale locale = localeResolver.resolveLocale(getCurrentReq());
        return applicationContext.getMessage(code, args, locale);
    }
}
