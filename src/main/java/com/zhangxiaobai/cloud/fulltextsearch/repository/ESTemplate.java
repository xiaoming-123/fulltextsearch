package com.zhangxiaobai.cloud.fulltextsearch.repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ESTemplate {

    /**
     * 增加更新索引
     *
     * @param indexName 索引名称
     * @return boolean 结果
     * @throws Exception Exception
     */
    boolean addOrUpdateIndex(String indexName) throws Exception;

    /**
     * 删除索引
     *
     * @param indexName 索引名称
     * @return boolean 结果
     * @throws Exception Exception
     */
    boolean deleteIndex(String indexName) throws Exception;

    /**
     * 查询全部索引
     *
     * @return boolean 结果
     * @throws Exception Exception
     */
    Object searchAllIndex() throws Exception;

    /**
     * 全文检索
     *
     * @param index       索引
     * @param queryString 检索值
     * @return 返回结果集
     * @throws IOException io异常
     */
    List fullTextSearch(String index, String queryString) throws IOException;

    /**
     * 全文检索
     *
     * @param index     索引
     * @param queryList 检索类型、字段和值
     * @return 返回结果集
     * @throws IOException io异常
     */
    List conditionSearch(String index, List<Map<String, Object>> queryList) throws IOException;

    /**
     * 新增和更新文档, id存在时为更新
     *
     * @param indexName  索引
     * @param id         文档id
     * @param sourceData 文档数据
     * @return 执行结果
     * @throws Exception 异常
     */
    boolean addDoc(String indexName, String id, Map<String, Object> sourceData) throws Exception;

    /**
     * 增加文件文档
     *
     * @param indexName 索引
     * @param data      数据
     * @return 结果
     * @throws Exception 异常
     */
    boolean addFileDoc(String indexName, Map<String, Object> data) throws Exception;
}
