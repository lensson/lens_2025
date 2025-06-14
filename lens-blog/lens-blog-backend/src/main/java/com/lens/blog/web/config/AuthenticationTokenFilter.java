package com.lens.blog.web.config;


import com.lens.blog.web.constant.RedisConstants;
import com.lens.blog.web.constant.SysConstants;
import com.lens.common.base.constant.Constants;
import com.lens.common.base.utils.JsonUtils;
import com.lens.common.core.utils.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Map;

/**
 * 拦截器
 */
@Component
@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        //得到请求头信息authorization信息
        String accessToken = request.getHeader("Authorization");

        if (accessToken != null) {
            //从Redis中获取内容
            String userInfo = stringRedisTemplate.opsForValue().get(RedisConstants.USER_TOKEN + Constants.SYMBOL_COLON + accessToken);
            if (!StringUtils.isEmpty(userInfo)) {
                Map<String, Object> map = JsonUtils.jsonToMap(userInfo);
                //把userUid存储到 request中
                request.setAttribute(SysConstants.TOKEN, accessToken);
                request.setAttribute(SysConstants.USER_UID, map.get(SysConstants.UID));
                request.setAttribute(SysConstants.USER_NAME, map.get(SysConstants.NICK_NAME));
                log.info("解析出来的用户:{}", map.get(SysConstants.NICK_NAME));
            }
        }
        chain.doFilter(request, response);
    }
}
		

