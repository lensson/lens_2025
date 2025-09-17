package com.lens.blog.admin.restapi;


import com.lens.blog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.lens.blog.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.lens.blog.admin.annotion.OperationLogger.OperationLogger;
import com.lens.blog.admin.constant.SysConstants;
import com.lens.blog.entity.PictureSort;
import com.lens.blog.vo.PictureSortVO;
import com.lens.blog.xo.service.PictureSortService;
import com.lens.common.base.exception.ThrowableUtils;
import com.lens.common.base.validator.group.Delete;
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

/**
 * 图片分类表 RestApi
 *
 * @author 陌溪
 * @date 2018年9月17日16:37:13
 */
@Tag(name ="图片分类相关接口", description = "图片分类相关接口")
@RestController
@RequestMapping("/pictureSort")
@Slf4j
public class PictureSortRestApi {

    @Autowired
    private PictureSortService pictureSortService;

    @AuthorityVerify
    @Operation(summary = "获取图片分类列表", description ="获取图片分类列表")
    @PostMapping(value = "/getList")
    public String getList(@Validated({GetList.class}) @RequestBody PictureSortVO pictureSortVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取图片分类列表: {}", pictureSortVO);
        return ResultUtil.result(SysConstants.SUCCESS, pictureSortService.getPageList(pictureSortVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加图片分类")
    @Operation(summary = "增加图片分类", description ="增加图片分类")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody PictureSortVO pictureSortVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加图片分类: {}", pictureSortVO);
        return pictureSortService.addPictureSort(pictureSortVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑图片分类")
    @Operation(summary = "编辑图片分类", description ="编辑图片分类")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody PictureSortVO pictureSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑图片分类: {}", pictureSortVO);
        return pictureSortService.editPictureSort(pictureSortVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除图片分类")
    @Operation(summary = "删除图片分类", description ="删除图片分类")
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class}) @RequestBody PictureSortVO pictureSortVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("删除图片分类: {}", pictureSortVO);
        return pictureSortService.deletePictureSort(pictureSortVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "置顶分类")
    @Operation(summary = "置顶分类", description ="置顶分类")
    @PostMapping("/stick")
    public String stick(@Validated({Delete.class}) @RequestBody PictureSortVO pictureSortVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("置顶图片分类: {}", pictureSortVO);
        return pictureSortService.stickPictureSort(pictureSortVO);
    }

    @OperationLogger(value = "通过Uid获取分类")
    @Operation(summary = "通过Uid获取分类", description ="通过Uid获取分类")
    @PostMapping("/getPictureSortByUid")
    public String getPictureSortByUid(@Validated({Delete.class}) @RequestBody PictureSortVO pictureSortVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        PictureSort pictureSort = pictureSortService.getById(pictureSortVO.getUid());
        log.info("通过Uid获取分类: {}", pictureSort);
        return ResultUtil.successWithData(pictureSort);
    }
}

