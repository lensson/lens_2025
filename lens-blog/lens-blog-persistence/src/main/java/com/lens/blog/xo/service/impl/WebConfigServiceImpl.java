package com.lens.blog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lens.blog.xo.constant.MessageConstants;
import com.lens.blog.xo.constant.RedisConstants;
import com.lens.blog.xo.constant.SQLConstants;
import com.lens.blog.xo.constant.SysConstants;
import com.lens.blog.mapper.WebConfigMapper;
import com.lens.blog.xo.service.WebConfigService;
import com.lens.blog.xo.utils.WebUtil;
import com.lens.blog.vo.WebConfigVO;
import com.lens.common.base.constant.Constants;
import com.lens.common.base.constant.ErrorCode;
import com.lens.common.base.enums.EAccountType;
import com.lens.common.base.enums.ELoginType;
import com.lens.common.base.exception.exceptionType.QueryException;
import com.lens.common.base.utils.JsonUtils;
import com.lens.common.core.utils.ResultUtil;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.db.entity.WebConfig;
import com.lens.common.db.mybatis.serviceImpl.SuperServiceImpl;
import com.lens.common.redis.utils.RedisUtil;
import com.lens.common.web.feign.PictureFeignClient;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 网站配置关系表 服务实现类
 *
 * @author 陌溪
 * @date 2018年11月11日15:10:41
 */
@Service
public class WebConfigServiceImpl extends SuperServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService {

    @Autowired
    private WebUtil webUtil;

    @Autowired
    private WebConfigService webConfigService;

    @Resource
    private PictureFeignClient pictureFeignClient;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public WebConfig getWebConfig() {

        QueryWrapper<WebConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConstants.CREATE_TIME);
        WebConfig webConfig = webConfigService.getOne(queryWrapper);

        //获取图片
        if (webConfig != null && StringUtils.isNotEmpty(webConfig.getLogo())) {
            String pictureList = this.pictureFeignClient.getPicture(webConfig.getLogo(), SysConstants.FILE_SEGMENTATION);
            webConfig.setPhotoList(webUtil.getPicture(pictureList));
        }

        //获取支付宝收款二维码
        if (webConfig != null && StringUtils.isNotEmpty(webConfig.getAliPay())) {
            String pictureList = this.pictureFeignClient.getPicture(webConfig.getAliPay(), SysConstants.FILE_SEGMENTATION);
            if (webUtil.getPicture(pictureList).size() > 0) {
                webConfig.setAliPayPhoto(webUtil.getPicture(pictureList).get(0));
            }

        }
        //获取微信收款二维码
        if (webConfig != null && StringUtils.isNotEmpty(webConfig.getWeixinPay())) {
            String pictureList = this.pictureFeignClient.getPicture(webConfig.getWeixinPay(), SysConstants.FILE_SEGMENTATION);
            if (webUtil.getPicture(pictureList).size() > 0) {
                webConfig.setWeixinPayPhoto(webUtil.getPicture(pictureList).get(0));
            }
        }
        return webConfig;
    }

    @Override
    public String getWebSiteName() {
        QueryWrapper<WebConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.last(SysConstants.LIMIT_ONE);
        WebConfig webConfig = webConfigService.getOne(queryWrapper);
        if (StringUtils.isNotEmpty(webConfig.getName())) {
            return webConfig.getName();
        }
        return "";
    }

    @Override
    public WebConfig getWebConfigByShowList() {
        //从Redis中获取IP来源
        String webConfigResult = redisUtil.get(RedisConstants.WEB_CONFIG);
        if (StringUtils.isNotEmpty(webConfigResult)) {
            WebConfig webConfig = JsonUtils.jsonToPojo(webConfigResult, WebConfig.class);
            return webConfig;
        }

        QueryWrapper<WebConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConstants.CREATE_TIME);
        WebConfig webConfig = webConfigService.getOne(queryWrapper);
        if (webConfig == null) {
            throw new QueryException(ErrorCode.SYSTEM_CONFIG_IS_NOT_EXIST, MessageConstants.SYSTEM_CONFIG_IS_NOT_EXIST);
        }
        StringBuilder stringBuilder = new StringBuilder();
        String pictureResult = "";

        // 获取LOGO
        if (StringUtils.isNotEmpty(webConfig.getLogo())) {
            stringBuilder.append(webConfig.getLogo() + Constants.SYMBOL_COMMA);
        }
        if (StringUtils.isNotEmpty(webConfig.getAliPay())) {
            stringBuilder.append(webConfig.getAliPay() + Constants.SYMBOL_COMMA);
        }
        if (StringUtils.isNotEmpty(webConfig.getWeixinPay())) {
            stringBuilder.append(webConfig.getWeixinPay() + Constants.SYMBOL_COMMA);
        }

        if (stringBuilder != null) {
            pictureResult = this.pictureFeignClient.getPicture(stringBuilder.toString(), Constants.SYMBOL_COMMA);
        }
        List<Map<String, Object>> pictureList = webUtil.getPictureMap(pictureResult);
        Map<String, String> pictureMap = new HashMap<>();
        pictureList.forEach(item -> {
            pictureMap.put(item.get(SQLConstants.UID).toString(), item.get(SQLConstants.URL).toString());
        });

        // 获取LOGO
        if (StringUtils.isNotEmpty(webConfig.getLogo()) && pictureMap.get(webConfig.getLogo()) != null) {
            webConfig.setLogoPhoto(pictureMap.get(webConfig.getLogo()));
        }
        // 获取阿里支付码
        if (StringUtils.isNotEmpty(webConfig.getAliPay()) && pictureMap.get(webConfig.getAliPay()) != null) {
            webConfig.setAliPayPhoto(pictureMap.get(webConfig.getAliPay()));
        }
        // 获取微信支付码
        if (StringUtils.isNotEmpty(webConfig.getWeixinPay()) && pictureMap.get(webConfig.getWeixinPay()) != null) {
            webConfig.setWeixinPayPhoto(pictureMap.get(webConfig.getWeixinPay()));
        }

        // 过滤一些不需要显示的用户账号信息
        String showListJson = webConfig.getShowList();

        // 获取联系方式
        String email = webConfig.getEmail();
        String qqNumber = webConfig.getQqNumber();
        String qqGroup = webConfig.getQqGroup();
        String github = webConfig.getGithub();
        String gitee = webConfig.getGitee();
        String weChat = webConfig.getWeChat();

        // 将联系方式全部置空
        webConfig.setEmail("");
        webConfig.setQqNumber("");
        webConfig.setQqGroup("");
        webConfig.setGithub("");
        webConfig.setGitee("");
        webConfig.setWeChat("");

        // 判断哪些联系方式需要显示出来
        List<String> showList = JsonUtils.jsonToList(showListJson, String.class);
        for (String item : showList) {
            if (EAccountType.EMail.getCode().equals(item)) {
                webConfig.setEmail(email);
            }
            if (EAccountType.QQNumber.getCode().equals(item)) {
                webConfig.setQqNumber(qqNumber);
            }
            if (EAccountType.QQGroup.getCode().equals(item)) {
                webConfig.setQqGroup(qqGroup);
            }
            if (EAccountType.Github.getCode().equals(item)) {
                webConfig.setGithub(github);
            }
            if (EAccountType.Gitee.getCode().equals(item)) {
                webConfig.setGitee(gitee);
            }
            if (EAccountType.WeChat.getCode().equals(item)) {
                webConfig.setWeChat(weChat);
            }
        }
        // 将WebConfig存到Redis中 [过期时间24小时]
        redisUtil.setEx(RedisConstants.WEB_CONFIG, JsonUtils.objectToJson(webConfig), 24, TimeUnit.HOURS);
        return webConfig;
    }

    @Override
    public String editWebConfig(WebConfigVO webConfigVO) {
        if (StringUtils.isEmpty(webConfigVO.getUid())) {
            WebConfig webConfig = new WebConfig();
            // 设置网站配置【使用Spring工具类提供的深拷贝】
            BeanUtils.copyProperties(webConfigVO, webConfig, SysConstants.STATUS);
            webConfigService.save(webConfig);

        } else {
            WebConfig webConfig = webConfigService.getById(webConfigVO.getUid());
            // 更新网站配置【使用Spring工具类提供的深拷贝】
            BeanUtils.copyProperties(webConfigVO, webConfig, SysConstants.STATUS, SysConstants.UID);
            webConfig.setUpdateTime(new Date());
            webConfigService.updateById(webConfig);
        }

        // 修改配置后，清空Redis中的 WEB_CONFIG
        redisUtil.delete(RedisConstants.WEB_CONFIG);
        // 同时清空Redis中的登录方式
        Set<String> keySet = redisUtil.keys(RedisConstants.LOGIN_TYPE + Constants.SYMBOL_STAR);
        redisUtil.delete(keySet);

        return ResultUtil.successWithMessage(MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public Boolean isOpenLoginType(String loginType) {
        String loginTypeJson = redisUtil.get(RedisConstants.LOGIN_TYPE + Constants.SYMBOL_COLON + loginType);
        // 判断redis中是否包含该登录记录
        if(StringUtils.isNotEmpty(loginTypeJson)) {
            // 如果Redis中有内容，表示开启该登录方式
            return true;
        }else if(loginTypeJson!= null && loginTypeJson.length() == 0) {
            // 如果内容为空串，表示没有开启该登录方式
            return false;
        }

        QueryWrapper<WebConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConstants.CREATE_TIME);
        WebConfig webConfig = webConfigService.getOne(queryWrapper);
        if (webConfig == null) {
            throw new QueryException(ErrorCode.SYSTEM_CONFIG_IS_NOT_EXIST, MessageConstants.SYSTEM_CONFIG_IS_NOT_EXIST);
        }
        // 过滤一些不需要显示的用户账号信息
        String loginTypeListJson = webConfig.getLoginTypeList();
        // 判断哪些联系方式需要显示出来
        List<String> loginTypeList = JsonUtils.jsonToList(loginTypeListJson, String.class);
        for (String item : loginTypeList) {
            if (ELoginType.PASSWORD.getCode().equals(item)) {
                redisUtil.set(RedisConstants.LOGIN_TYPE + Constants.SYMBOL_COLON + RedisConstants.PASSWORD, ELoginType.PASSWORD.getName());
            }
            if (ELoginType.GITEE.getCode().equals(item)) {
                redisUtil.set(RedisConstants.LOGIN_TYPE + Constants.SYMBOL_COLON + RedisConstants.GITEE, ELoginType.GITEE.getName());
            }
            if (ELoginType.GITHUB.getCode().equals(item)) {
                redisUtil.set(RedisConstants.LOGIN_TYPE + Constants.SYMBOL_COLON + RedisConstants.GITHUB, ELoginType.GITHUB.getName());
            }
            if (ELoginType.QQ.getCode().equals(item)) {
                redisUtil.set(RedisConstants.LOGIN_TYPE + Constants.SYMBOL_COLON + RedisConstants.QQ, ELoginType.QQ.getName());
            }
            if (ELoginType.WECHAT.getCode().equals(item)) {
                redisUtil.set(RedisConstants.LOGIN_TYPE + Constants.SYMBOL_COLON + RedisConstants.WECHAT, ELoginType.WECHAT.getName());
            }
        }
        // 再次判断该登录方式是否开启
        loginTypeJson = redisUtil.get(RedisConstants.LOGIN_TYPE + Constants.SYMBOL_COLON + loginType);
        if(StringUtils.isNotEmpty(loginTypeJson)) {
            return true;
        } else {
            // 设置一个为空的字符串【防止缓存穿透】
            redisUtil.set(RedisConstants.LOGIN_TYPE + Constants.SYMBOL_COLON + loginType, "");
            return false;
        }
    }
}
