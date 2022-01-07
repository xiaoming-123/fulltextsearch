package com.zhangxiaobai.cloud.fulltextsearch.service.impl;

import com.zhangxiaobai.cloud.fulltextsearch.repository.ESTemplate;
import com.zhangxiaobai.cloud.fulltextsearch.service.SourceManageService;
import com.zhangxiaobai.cloud.fulltextsearch.util.DatabaseUtils;
import com.zhangxiaobai.cloud.fulltextsearch.util.FileUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

@Service
public class SourceManageServiceImpl implements SourceManageService {

    public static final String SOURCE_INDEX_NAME = "data_source";

    public static final String CONFIG_INDEX_NAME = "data_config";

    public static final String FILE_INDEX_NAME = "docwrite";

    @Resource
    private ESTemplate template;

    @Override
    public void addSource(String id, Map<String, Object> sourceData) throws Exception {
        template.addDoc(SOURCE_INDEX_NAME, id, sourceData);
    }

    @Override
    public Object getSource() throws Exception {
        return template.fullTextSearch(SOURCE_INDEX_NAME, null);
    }

    @Override
    public boolean testConnect(Map sourceData) {
        if (sourceData.get("type") == null) {
            return false;
        }
        try {
            int type = (int) sourceData.get("type");
            // mysql
            if (type == 1) {
                HikariDataSource dataSource = DatabaseUtils.getDataSource(sourceData);
                Connection connection = dataSource.getConnection();
                DatabaseUtils.closeDataSourceAndConnection(dataSource, connection);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Object getTableAndField(Map sourceData) throws Exception {
        if (sourceData.get("type") == null) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        int type = (int) sourceData.get("type");
        // mysql
        if (type == 1) {
            HikariDataSource dataSource = DatabaseUtils.getDataSource(sourceData);
            Connection connection = dataSource.getConnection();
            String sql = "SELECT T.TABLE_NAME, T.TABLE_COMMENT, C.COLUMN_NAME, C.COLUMN_COMMENT,COLUMN_KEY FROM information_schema.`TABLES` T LEFT JOIN " +
                    "    information_schema.`COLUMNS` C ON T.TABLE_NAME = C.TABLE_NAME AND T.TABLE_SCHEMA = C.TABLE_SCHEMA" +
                    "    WHERE T.TABLE_SCHEMA = 'jsaec' ORDER BY C.TABLE_NAME , C.ORDINAL_POSITION";
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            LinkedHashMap<String, List<Map<String, Object>>> tableNames = new LinkedHashMap<>();
            LinkedList<Map<String, Object>> columnNames;
            String primaryKey = "";
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                String columnName = resultSet.getString("COLUMN_NAME");
                String columnComment = resultSet.getString("COLUMN_COMMENT");
                String columnKey = resultSet.getString("COLUMN_KEY");

                if ("PRI".equals(columnKey)) {
                    primaryKey = columnName;
                }

                Map<String, Object> column = new HashMap<>(4);
                column.put("id", tableName + "_" + columnName);
                column.put("name", columnName);
                column.put("parentId", tableName);
                column.put("primaryKey", primaryKey);
                if (!tableNames.containsKey(tableName)) {
                    columnNames = new LinkedList<>();
                    columnNames.add(column);
                    tableNames.put(tableName, columnNames);
                } else {
                    tableNames.get(tableName).add(column);
                }
            }
            DatabaseUtils.closeDataSourceAndConnection(dataSource, connection);

            tableNames.forEach((key, value) -> {
                Map<String, Object> table = new HashMap<>(4);
                table.put("id", key);
                table.put("parentId", null);
                table.put("name", key);
                result.add(table);
                result.addAll(value);
            });
        }
        return result;
    }

    @Override
    public Object getConfigListByDatabaseId(String databaseId) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> queryConfig = new HashMap<>(4);
        queryConfig.put("searchType", "match");
        Map<String, Object> query = new HashMap<>(4);
        query.put("databaseId", databaseId);
        queryConfig.put("query", query);
        list.add(queryConfig);
        return template.conditionSearch(CONFIG_INDEX_NAME, list);
    }

    @Override
    public void saveConfig(List<Map> configData) throws Exception {
        configData.forEach((item) -> {
            try {
                template.addDoc(CONFIG_INDEX_NAME, item.get("id") == null ? null : item.get("id").toString(), item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void uploadFile(MultipartFile file) throws Exception {
        if (file.isEmpty() || file.getOriginalFilename() == null)  {
            return;
        }
        String url = FileUtils.saveFile(file);
        if (url != null) {
            String filename = file.getOriginalFilename();
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", UUID.randomUUID().toString());
            map.put("name", filename);
            map.put("time", System.currentTimeMillis());
            map.put("type", filename.substring(filename.indexOf(".") + 1));
            map.put("url", url);
            // 文件内容需要使用base64编码
            map.put("content", FileUtils.getBase64String(file));
            template.addFileDoc(FILE_INDEX_NAME, map);
        }
    }


}
