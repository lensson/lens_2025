package com.lens.blog.web.restapi;


import com.lens.blog.web.constant.SysConstants;
import com.lens.blog.xo.service.SysDictDataService;
import com.lens.common.core.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据查询 RestApi
 *
 * @author 陌溪
 * @since 2020年3月11日10:37:13
 */
@RestController
@RequestMapping("/sysDictData")
@Tag(name = "数据字典相关接口", description = "数据字典相关接口")
@Slf4j
public class SysDictDataRestApi {

    @Autowired
    SysDictDataService sysDictDataService;

    @Operation(summary = "根据字典类型获取字典数据", description = "根据字典类型获取字典数据")
    @PostMapping("/getListByDictType")
    public String getListByDictType(@RequestParam("dictType") String dictType) {

        log.info("根据字典类型获取字典数据");
        return ResultUtil.result(SysConstants.SUCCESS, sysDictDataService.getListByDictType(dictType));
    }

    @Operation(summary = "根据字典类型数组获取字典数据", description = "根据字典类型数组获取字典数据")
    @PostMapping("/getListByDictTypeList")
    public String getListByDictTypeList(@RequestBody List<String> dictTypeList) {
        log.info("根据字典类型数组获取字典数据");
        return ResultUtil.result(SysConstants.SUCCESS, sysDictDataService.getListByDictTypeList(dictTypeList));
    }
}

