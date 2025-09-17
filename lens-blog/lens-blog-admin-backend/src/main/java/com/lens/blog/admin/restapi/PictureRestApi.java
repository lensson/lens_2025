package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.vo.PictureVO;
import com.lens.blog.xo.service.PictureService;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.GetList;
import com.lens.common.base.validator.group.Insert;
import com.lens.common.base.validator.group.Update;
import com.lens.common.core.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 图片表 RestApi
 *
 * @author 陌溪
 * @date 2018年9月17日16:21:43
 */
@RestController
@RequestMapping("/picture")
@Tag(name ="图片相关接口", description = "图片相关接口")
@Slf4j
public class PictureRestApi {

    @Autowired
    private PictureService pictureService;

    @AuthorityVerify
    @Operation(summary = "获取图片列表", description ="获取图片列表")
    @PostMapping(value = "/getList")
    public String getList(@Validated({GetList.class}) @RequestBody PictureVO pictureVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取图片列表:", pictureVO);
        return ResultUtil.successWithData(pictureService.getPageList(pictureVO));
    }

    @AuthorityVerify
    @OperationLogger(value = "增加图片")
    @Operation(summary = "增加图片", description ="增加图片")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody List<PictureVO> pictureVOList, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("添加图片:", pictureVOList);
        return pictureService.addPicture(pictureVOList);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑图片")
    @Operation(summary = "编辑图片", description ="编辑图片")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody PictureVO pictureVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑图片:{}", pictureVO);
        return pictureService.editPicture(pictureVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除图片")
    @Operation(summary = "删除图片", description ="删除图片")
    @PostMapping("/delete")
    public String delete(@RequestBody PictureVO pictureVO) {
        log.info("删除图片:{}", pictureVO);
        return pictureService.deleteBatchPicture(pictureVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "通过图片Uid将图片设为封面")
    @Operation(summary = "通过图片Uid将图片设为封面", description ="通过图片Uid将图片设为封面")
    @PostMapping("/setCover")
    public String setCover(@Validated({Update.class}) @RequestBody PictureVO pictureVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("设置图片分类封面:{}", pictureVO);
        return pictureService.setPictureCover(pictureVO);
    }
}

