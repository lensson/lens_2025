package com.lens.common.web.config;

import com.lens.common.base.constant.BaseSysConstants;
import com.lens.common.base.constant.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.micrometer.core.instrument.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @Author zhenac
 * @Created 5/30/25 6:55 AM
 */

public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        // 获取Http请求
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }

        // 获取token，放入到feign的请求头
        String token = null;
        if (request != null) {
            if (request.getParameter(BaseSysConstants.TOKEN) != null) {
                token = request.getParameter(BaseSysConstants.TOKEN);
            } else if (request.getAttribute(BaseSysConstants.TOKEN) != null) {
                token = request.getAttribute(BaseSysConstants.TOKEN).toString();
            }
        }

        if (StringUtils.isNotEmpty(token)) {
            // 如果带有？说明还带有其它参数，我们只截取到token即可
            if (token.indexOf(Constants.SYMBOL_QUESTION) != -1) {
                String[] params = token.split("\\?url=");
                token = params[0];
            }
            requestTemplate.header(BaseSysConstants.PICTURE_TOKEN, token);
        }
    }
}
