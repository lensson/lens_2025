package com.lens.common.web.fallback;



import com.lens.common.core.utils.ResultUtil;
import com.lens.common.web.feign.SearchFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 搜索服务降级兜底方法【当服务不可用时会触发】
 *
 * @author: Lens
 * @create: 2020年10月6日09:03:19
 */
@Component
@Slf4j
public class SearchFeignFallback implements SearchFeignClient {

    @Override
    public String deleteElasticSearchByUid(String uid) {
        log.error("搜索服务出现异常, 服务降级返回, 删除ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 删除ElasticSearch索引失败");
    }

    @Override
    public String deleteElasticSearchByUids(String uids) {
        log.error("搜索服务出现异常, 服务降级返回, 批量删除ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 批量删除ElasticSearch索引失败");
    }

    @Override
    public String initElasticSearchIndex() {
        log.error("搜索服务出现异常, 服务降级返回, 初始化ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 初始化ElasticSearch索引失败");
    }

    @Override
    public String addElasticSearchIndexByUid(String uid) {
        log.error("搜索服务出现异常, 服务降级返回, 添加ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 添加ElasticSearch索引失败");
    }

    @Override
    public String deleteSolrIndexByUid(String uid) {
        log.error("搜索服务出现异常, 服务降级返回, 删除Solr索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 删除Solr索引失败");
    }

    @Override
    public String deleteSolrIndexByUids(String uids) {
        log.error("搜索服务出现异常, 服务降级返回, 删除Solr索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 批量删除Solr索引失败");
    }

    @Override
    public String initSolrIndex() {
        log.error("搜索服务出现异常, 服务降级返回, 初始化Solr索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 初始化Solr索引失败");
    }

    @Override
    public String addSolrIndexByUid(String uid) {
        log.error("搜索服务出现异常, 服务降级返回, 添加Solr索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 添加Solr索引失败");
    }

    @Override
    public String updateSolrIndexByUid(String uid) {
        log.error("搜索服务出现异常, 服务降级返回, 更新Solr索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 更新Solr索引失败");
    }
}
