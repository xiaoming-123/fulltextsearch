package com.zhangxiaobai.cloud.fulltextsearch.service;

/**
 * 全文检索
 * @author zhangcq
 */
public interface FulltextSearchService {

    /**
     * 全文检索查询
     * @param indexName 索引
     * @param queryString 查询字段
     * @return 结果
     * @throws Exception e
     */
    Object search(String indexName, String queryString) throws Exception;
}
