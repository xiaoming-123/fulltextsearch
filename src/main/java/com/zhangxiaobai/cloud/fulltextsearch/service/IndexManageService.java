package com.zhangxiaobai.cloud.fulltextsearch.service;

import java.io.IOException;
import java.util.Map;

/**
 * 索引管理
 * @author zhangcq
 */
public interface IndexManageService {
    /**
     * 增加索引
     * @param source 配置
     */
    void addIndex(Map<String, Object> source);

    /**
     * 获取索引
     * @return 结果
     * @throws Exception 异常
     */
    Object getIndex() throws Exception;

    /**
     * 删除索引
     * @param indexName 索引名称
     * @throws Exception 异常
     */
    void deleteIndex(String indexName) throws Exception;
}
