package com.lens.common.base.enums;


import com.lens.common.base.constant.BaseSysConstants;
import com.lens.common.base.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 行为枚举类
 *
 * @author 陌溪
 * @date 2020/9/14 10:40
 */
@Slf4j
public enum EBehavior {

    /**
     * 用户行为
     */
    BLOG_TAG("点击标签", "blog_tag"),
    BLOG_SORT("点击博客分类", "blog_sort"),
    BLOG_CONTNET("点击博客", "blog_content"),
    BLOG_PRAISE("点赞", "blog_praise"),
    FRIENDSHIP_LINK("点击友情链接", "friendship_link"),
    BLOG_SEARCH("点击搜索", "blog_search"),
    STUDY_VIDEO("点击学习视频", "study_video"),
    VISIT_PAGE("访问页面", "visit_page"),
    VISIT_CLASSIFY("点击博客分类", "visit_classify"),
    VISIT_SORT("点击归档", "visit_sort"),
    BLOG_AUTHOR("点击作者", "blog_author"),
    PUBLISH_COMMENT("发表评论", "publish_comment"),
    DELETE_COMMENT("删除评论", "delete_comment"),
    REPORT_COMMENT("举报评论", "report_comment"),
    VISIT_TAG("点击博客标签页面", "visit_tag");

    private String content;
    private String behavior;

    EBehavior(String content, String behavior) {
        this.content = content;
        this.behavior = behavior;
    }

    /**
     * 根据value返回枚举类型，主要在switch中使用
     *
     * @param value
     * @return
     */
    public static EBehavior getByValue(String value) {
        for (EBehavior behavior : values()) {
            if (behavior.getBehavior() == value) {
                return behavior;
            }
        }
        return null;
    }

    public static Map<String, String> getModuleAndOtherData(EBehavior behavior,
                                                            Map<String, Object> nameAndArgsMap,
                                                            String bussinessName) {
        String otherData = "";
        String moduleUid = "";
        switch (behavior) {
            case BLOG_AUTHOR: {
                // 判断是否是点击作者
                if (nameAndArgsMap.get(BaseSysConstants.AUTHOR) != null) {
                    otherData = nameAndArgsMap.get(BaseSysConstants.AUTHOR).toString();
                }
            }
            break;
            case BLOG_SORT: {
                // 判断是否点击博客分类
                if (nameAndArgsMap.get(BaseSysConstants.BLOG_SORT_UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConstants.BLOG_SORT_UID).toString();
                }
            }
            break;
            case BLOG_TAG: case VISIT_TAG:{
                // 判断是否点击博客标签
                if (nameAndArgsMap.get(BaseSysConstants.TAG_UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConstants.TAG_UID).toString();
                }
            }
            break;
            case BLOG_SEARCH: {
                // 判断是否进行搜索
                if (nameAndArgsMap.get(BaseSysConstants.KEYWORDS) != null) {
                    otherData = nameAndArgsMap.get(BaseSysConstants.KEYWORDS).toString();
                }
            }
            break;
            case VISIT_CLASSIFY: {
                // 判断是否点击分类
                if (nameAndArgsMap.get(BaseSysConstants.BLOG_SORT_UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConstants.BLOG_SORT_UID).toString();
                }
            }
            break;
            case VISIT_SORT: {
                // 判断是否点击归档
                if (nameAndArgsMap.get(BaseSysConstants.MONTH_DATE) != null) {
                    otherData = nameAndArgsMap.get(BaseSysConstants.MONTH_DATE).toString();
                }
            }
            break;
            case BLOG_CONTNET: {
                // 判断是否博客详情
                if (nameAndArgsMap.get(BaseSysConstants.UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConstants.UID).toString();
                } else if(nameAndArgsMap.get(BaseSysConstants.OID) != null) {
                    // 当收到的是oid的时候，存储到otherData处
                    otherData = nameAndArgsMap.get(BaseSysConstants.OID).toString();
                }
            }
            break;
            case BLOG_PRAISE: {
                // 判断是否给博客点赞
                if (nameAndArgsMap.get(BaseSysConstants.UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConstants.UID).toString();
                }
            }
            break;
            case FRIENDSHIP_LINK: {
                // 判断是否点击友链
                if (nameAndArgsMap.get(BaseSysConstants.UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConstants.UID).toString();
                }
                otherData = bussinessName;
            }
            break;
            case VISIT_PAGE: {
                // 访问页面
                if (nameAndArgsMap.get(BaseSysConstants.PAGE_NAME) != null) {
                    otherData = nameAndArgsMap.get(BaseSysConstants.PAGE_NAME).toString();
                } else {
                    otherData = bussinessName;
                }
            }
            break;
            case PUBLISH_COMMENT: {
                // 发表评论
                Object object = nameAndArgsMap.get(BaseSysConstants.COMMENT_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConstants.CONTENT) != null) {
                    otherData = map.get(BaseSysConstants.CONTENT).toString();
                }
            }
            break;
            case REPORT_COMMENT: {
                // 举报评论
                Object object = nameAndArgsMap.get(BaseSysConstants.COMMENT_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConstants.CONTENT) != null) {
                    otherData = map.get(BaseSysConstants.CONTENT).toString();
                }
            }
            break;
            case DELETE_COMMENT: {
                // 删除评论
                Object object = nameAndArgsMap.get(BaseSysConstants.COMMENT_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConstants.CONTENT) != null) {
                    otherData = map.get(BaseSysConstants.CONTENT).toString();
                }
            }
            break;
            default: {
                log.info("其它行为");
            }
        }
        Map<String, String> result = new HashMap<>();
        result.put(BaseSysConstants.MODULE_UID, moduleUid);
        result.put(BaseSysConstants.OTHER_DATA, otherData);
        return result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }


}