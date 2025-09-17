package com.lens.blog.web.restapi;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lens.blog.entity.SystemConfig;
import com.lens.blog.entity.User;
import com.lens.blog.vo.UserVO;
import com.lens.blog.web.constant.MessageConstants;
import com.lens.blog.web.constant.RedisConstants;
import com.lens.blog.web.constant.SQLConstants;
import com.lens.blog.web.constant.SysConstants;
import com.lens.blog.xo.service.SystemConfigService;
import com.lens.blog.xo.service.UserService;
import com.lens.blog.xo.service.WebConfigService;
import com.lens.blog.xo.utils.RabbitMqUtil;
import com.lens.blog.xo.utils.WebUtil;
import com.lens.common.base.constant.Constants;
import com.lens.common.base.enums.EOpenStatus;
import com.lens.common.base.enums.EStatus;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.utils.JsonUtils;
import com.lens.common.base.validator.group.GetOne;
import com.lens.common.base.validator.group.Insert;
import com.lens.common.core.utils.IpUtils;
import com.lens.common.core.utils.MD5Utils;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.redis.utils.RedisUtil;
import com.lens.common.web.feign.PictureFeignClient;
import com.lens.common.web.holder.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户登录RestApi，系统自带的登录注册功能
 * 第三方登录请移步AuthRestApi
 *
 * @author 陌溪
 * @date 2020年5月6日17:50:23
 */
@RestController
@RefreshScope
@RequestMapping("/login")
@Tag(name = "登录管理相关接口", description = "登录管理相关接口")
@Slf4j
public class LoginRestApi {

    @Autowired
    private RabbitMqUtil rabbitMqUtil;
    @Autowired
    private WebConfigService webConfigService;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SystemConfigService systemConfigService;
    @Value(value = "${BLOG.USER_TOKEN_SURVIVAL_TIME}")
    private Long userTokenSurvivalTime;

    @Operation(summary = "用户登录", description = "用户登录")
    @PostMapping("/login")
    public String login(@Validated({GetOne.class}) @RequestBody UserVO userVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        Boolean isOpenLoginType = webConfigService.isOpenLoginType(RedisConstants.PASSWORD);
        if (!isOpenLoginType) {
            return ResultUtil.result(SysConstants.ERROR, "后台未开启该登录方式!");
        }
        String userName = userVO.getUserName();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(SQLConstants.USER_NAME, userName).or().eq(SQLConstants.EMAIL, userName));
        queryWrapper.last(SysConstants.LIMIT_ONE);
        User user = userService.getOne(queryWrapper);
        if (user == null || EStatus.DISABLED == user.getStatus()) {
            return ResultUtil.result(SysConstants.ERROR, "用户不存在");
        }
        if (EStatus.FREEZE == user.getStatus()) {
            return ResultUtil.result(SysConstants.ERROR, "用户账号未激活");
        }
        if (StringUtils.isNotEmpty(user.getPassWord()) && user.getPassWord().equals(MD5Utils.string2MD5(userVO.getPassWord()))) {
            // 更新登录信息
            HttpServletRequest request = RequestHolder.getRequest();
            String ip = IpUtils.getIpAddr(request);
            Map<String, String> userMap = IpUtils.getOsAndBrowserInfo(request);
            user.setBrowser(userMap.get(SysConstants.BROWSER));
            user.setOs(userMap.get(SysConstants.OS));
            user.setLastLoginIp(ip);
            user.setLastLoginTime(new Date());
            // 登录成功后，次数+1
            Integer count = user.getLoginCount() + 1;
            user.setLoginCount(count);
            user.updateById();
            // 获取用户头像
            if (!StringUtils.isEmpty(user.getAvatar())) {
                String avatarResult = pictureFeignClient.getPicture(user.getAvatar(), ",");
                List<String> picList = webUtil.getPicture(avatarResult);
                if (picList != null && picList.size() > 0) {
                    user.setPhotoUrl(webUtil.getPicture(avatarResult).get(0));
                }
            }
            // 生成token
            String token = StringUtils.getUUID();
            // 过滤密码
            user.setPassWord("");
            //将从数据库查询的数据缓存到redis中
            redisUtil.setEx(RedisConstants.USER_TOKEN + Constants.SYMBOL_COLON + token, JsonUtils.objectToJson(user), userTokenSurvivalTime, TimeUnit.HOURS);
            log.info("登录成功，返回token: ", token);
            return ResultUtil.result(SysConstants.SUCCESS, token);
        } else {
            return ResultUtil.result(SysConstants.ERROR, "账号或密码错误");
        }
    }

    @Operation(summary = "用户注册", description = "用户注册")
    @PostMapping("/register")
    public String register(@Validated({Insert.class}) @RequestBody UserVO userVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        // 判断是否开启登录方式
        Boolean isOpenLoginType = webConfigService.isOpenLoginType(RedisConstants.PASSWORD);
        if (!isOpenLoginType) {
            return ResultUtil.result(SysConstants.ERROR, "后台未开启注册功能!");
        }
        if (userVO.getUserName().length() < Constants.NUM_FIVE || userVO.getUserName().length() >= Constants.NUM_TWENTY || userVO.getPassWord().length() < Constants.NUM_FIVE || userVO.getPassWord().length() >= Constants.NUM_TWENTY) {
            return ResultUtil.result(SysConstants.ERROR, MessageConstants.PARAM_INCORRECT);
        }
        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(SQLConstants.USER_NAME, userVO.getUserName()).or().eq(SQLConstants.EMAIL, userVO.getEmail()));
        queryWrapper.eq(SysConstants.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConstants.LIMIT_ONE);
        User user = userService.getOne(queryWrapper);
        if (user != null) {
            return ResultUtil.result(SysConstants.ERROR, MessageConstants.USER_OR_EMAIL_EXIST);
        }
        user = new User();
        user.setUserName(userVO.getUserName());
        user.setNickName(userVO.getNickName());
        user.setPassWord(MD5Utils.string2MD5(userVO.getPassWord()));
        user.setEmail(userVO.getEmail());
        // 设置账号来源，蘑菇博客
        user.setSource(SysConstants.MOGU);
        user.setLastLoginIp(ip);
        user.setBrowser(map.get(SysConstants.BROWSER));
        user.setOs(map.get(SysConstants.OS));

        // 判断是否开启用户邮件激活状态
        SystemConfig systemConfig = systemConfigService.getConfig();
        String openEmailActivate = systemConfig.getOpenEmailActivate();
        String resultMessage = "注册成功";
        if (EOpenStatus.OPEN.equals(openEmailActivate)) {
            user.setStatus(EStatus.FREEZE);
        } else {
            // 未开启注册用户邮件激活，直接设置成激活状态
            user.setStatus(EStatus.ENABLE);
        }
        user.insert();

        // 判断是否需要发送邮件通知
        if (EOpenStatus.OPEN.equals(openEmailActivate)) {
            // 生成随机激活的token
            String token = StringUtils.getUUID();
            // 过滤密码
            user.setPassWord("");
            //将从数据库查询的数据缓存到redis中，用于用户邮箱激活，1小时后过期
            redisUtil.setEx(RedisConstants.ACTIVATE_USER + RedisConstants.SEGMENTATION + token, JsonUtils.objectToJson(user), 1, TimeUnit.HOURS);
            // 发送邮件，进行账号激活
            rabbitMqUtil.sendActivateEmail(user, token);
            resultMessage = "注册成功，请登录邮箱进行账号激活";
        }
        return ResultUtil.result(SysConstants.SUCCESS, resultMessage);
    }

    @Operation(summary = "激活用户账号", description = "激活用户账号")
    @GetMapping("/activeUser/{token}")
    public String bindUserEmail(@PathVariable("token") String token) {
        // 从redis中获取用户信息
        String userInfo = redisUtil.get(RedisConstants.ACTIVATE_USER + RedisConstants.SEGMENTATION + token);
        if (StringUtils.isEmpty(userInfo)) {
            return ResultUtil.result(SysConstants.ERROR, MessageConstants.INVALID_TOKEN);
        }
        User user = JsonUtils.jsonToPojo(userInfo, User.class);
        if (EStatus.FREEZE != user.getStatus()) {
            return ResultUtil.result(SysConstants.ERROR, "用户账号已经被激活");
        }
        user.setStatus(EStatus.ENABLE);
        user.updateById();

        // 更新成功后，需要把该用户名下其它未激活的用户删除【删除】
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConstants.USER_NAME, user.getUserName());
        queryWrapper.ne(SQLConstants.UID, user.getUid());
        queryWrapper.ne(SQLConstants.STATUS, EStatus.ENABLE);
        List<User> userList = userService.list(queryWrapper);
        if (userList.size() > 0) {
            List<String> uidList = new ArrayList<>();
            userList.forEach(item -> {
                uidList.add(item.getUid());
            });
            // 移除所有未激活的用户【该用户名下的】
            userService.removeByIds(uidList);
        }

        return ResultUtil.result(SysConstants.SUCCESS, MessageConstants.OPERATION_SUCCESS);
    }

    @Operation(summary = "退出登录", description = "退出登录")
    @PostMapping(value = "/logout")
    public String logout(@Parameter(name = "token", description = "token令牌", required = false) @RequestParam(name = "token", required = false) String token) {
        if (StringUtils.isEmpty(token)) {
            return ResultUtil.result(SysConstants.ERROR, MessageConstants.OPERATION_FAIL);
        }
        redisUtil.set(RedisConstants.USER_TOKEN + Constants.SYMBOL_COLON + token, "");
        return ResultUtil.result(SysConstants.SUCCESS, "退出成功");
    }

}
