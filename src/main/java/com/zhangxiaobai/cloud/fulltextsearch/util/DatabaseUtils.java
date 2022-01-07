package com.zhangxiaobai.cloud.fulltextsearch.util;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.util.Map;

/**
 * 数据库工具类
 * @author zhangcq
 */
public class DatabaseUtils {

    private DatabaseUtils() {
    }

    /**
     * 获取数据源
     *
     * @param sourceData 数据源信息
     * @return HikariDataSource 数据源
     */
    public static HikariDataSource getDataSource(Map sourceData) {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(sourceData.get("driverClassName").toString());
        dataSource.setJdbcUrl(sourceData.get("url").toString());
        dataSource.setUsername(sourceData.get("username").toString());
        dataSource.setPassword(sourceData.get("password").toString());
        dataSource.setConnectionTimeout(3000L);
        return dataSource;
    }

    /**
     * 关闭数据源和数据库连接
     *
     * @param dataSource 数据源
     * @param connection 数据库连接
     * @throws Exception 数据库连接关闭异常
     */
    public static void closeDataSourceAndConnection(HikariDataSource dataSource, Connection connection) throws Exception {
        connection.close();
        dataSource.close();
    }

}
