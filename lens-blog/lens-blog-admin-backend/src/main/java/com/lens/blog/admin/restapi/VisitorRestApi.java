package com.lens.blog.admin.restapi;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 游客表 RestApi
 *
 * @author 陌溪
 * @date 2018-09-08
 */
@RestController
@RequestMapping("/visitor")
@Tag(name ="游客相关接口", description = "游客相关接口")
@Slf4j
public class VisitorRestApi {

}

