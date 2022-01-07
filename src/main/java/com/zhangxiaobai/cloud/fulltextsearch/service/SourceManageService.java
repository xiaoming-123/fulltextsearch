package com.zhangxiaobai.cloud.fulltextsearch.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 数据源管理
 * @author zhangcq
 */
public interface SourceManageService {

    /**
     * 增加数据源
     * @param id 文档id
     * @param sourceData 数据源数据
     * @throws Exception 数据库操作异常
     */
    void addSource(String id, Map<String, Object> sourceData) throws Exception;

    /**
     * 获取数据源
     * @return Object 数据源信息
     * @throws Exception 数据库操作异常
     */
    Object getSource() throws Exception;

    /**
     * 测试数据源连接
     * @param sourceData 数据源信息
     * @return 是否连接成功
     * @throws Exception 数据库异常
     */
    boolean testConnect(Map sourceData) throws Exception;

    /**
     * 获取数据源 表和字段
     * @param sourceData 数据源信息
     * @return Object 表和字段信息
     * @throws Exception 数据库异常
     */
    Object getTableAndField(Map sourceData) throws Exception;

    /**
     * 根据数据源id获取配置信息
     * @param databaseId 数据源id
     * @return Object 配置信息列表
     * @throws Exception 数据库异常
     */
    Object getConfigListByDatabaseId(String databaseId) throws Exception;

    /**
     * 保存配置信息
     * @param configData 配置信息
     * @throws Exception 数据库异常
     */
    void saveConfig(List<Map> configData) throws Exception;

    /**
     * 保存文件文档
     * @param file 文件
     * @throws Exception e
     */
    void uploadFile(MultipartFile file) throws Exception;
}
