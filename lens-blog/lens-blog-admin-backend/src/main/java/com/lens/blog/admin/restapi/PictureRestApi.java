package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.xo.service.PictureService;
import com.lens.blog.vo.PictureVO;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.GetList;
import com.lens.common.base.validator.group.Insert;
import com.lens.common.base.validator.group.Update;
import com.lens.common.core.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "图片相关接口", tags = {"图片相关接口"})
@Slf4j
public class PictureRestApi {

    @Autowired
    private PictureService pictureService;

    @AuthorityVerify
    @ApiOperation(value = "获取图片列表", notes = "获取图片列表", response = String.class)
    @PostMapping(value = "/getList")
    public String getList(@Validated({GetList.class}) @RequestBody PictureVO pictureVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取图片列表:", pictureVO);
        return ResultUtil.successWithData(pictureService.getPageList(pictureVO));
    }

    @AuthorityVerify
    @OperationLogger(value = "增加图片")
    @ApiOperation(value = "增加图片", notes = "增加图片", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody List<PictureVO> pictureVOList, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("添加图片:", pictureVOList);
        return pictureService.addPicture(pictureVOList);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑图片")
    @ApiOperation(value = "编辑图片", notes = "编辑图片", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody PictureVO pictureVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑图片:{}", pictureVO);
        return pictureService.editPicture(pictureVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除图片")
    @ApiOperation(value = "删除图片", notes = "删除图片", response = String.class)
    @PostMapping("/delete")
    public String delete(@RequestBody PictureVO pictureVO) {
        log.info("删除图片:{}", pictureVO);
        return pictureService.deleteBatchPicture(pictureVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "通过图片Uid将图片设为封面")
    @ApiOperation(value = "通过图片Uid将图片设为封面", notes = "通过图片Uid将图片设为封面", response = String.class)
    @PostMapping("/setCover")
    public String setCover(@Validated({Update.class}) @RequestBody PictureVO pictureVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("设置图片分类封面:{}", pictureVO);
        return pictureService.setPictureCover(pictureVO);
    }
}

