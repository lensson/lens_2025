package com.lens.blog.admin.security;

import com.alibaba.fastjson.JSONObject;
import com.lens.common.base.constant.Constants;
import com.lens.common.base.constant.ECode;
import com.lens.common.core.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt 认证进入点 【认证失败处理类 返回未授权】
 *
 * @author 陌溪
 * @date 2020年9月19日10:04:54
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;



    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        response.setStatus(ECode.SUCCESS);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        Map<String, Object> result = new HashMap<>(Constants.NUM_THREE);
        result.put("code", ECode.UNAUTHORIZED);
        result.put("msg", msg);
        result.put("data", "token无效或过期,请重新登录");
        response.getWriter().write(JSONObject.toJSONString(result));
    }


}

