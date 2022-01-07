package com.zhangxiaobai.cloud.fulltextsearch.schedule;

import com.zhangxiaobai.cloud.fulltextsearch.repository.ESTemplate;
import com.zhangxiaobai.cloud.fulltextsearch.util.DatabaseUtils;
import com.zhangxiaobai.cloud.fulltextsearch.util.EsUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zhangxiaobai.cloud.fulltextsearch.service.impl.SourceManageServiceImpl.CONFIG_INDEX_NAME;
import static com.zhangxiaobai.cloud.fulltextsearch.service.impl.SourceManageServiceImpl.SOURCE_INDEX_NAME;


@Component
public class DataImportSchedule {

    private final static int PAGE_SIZE = 100;

    private final static String DATA_INDEX = "data";

    @Resource
    private ESTemplate esTemplate;

    @PostConstruct
    public void dataImport() throws Exception {
        // 查询全部配置项
        List list = esTemplate.conditionSearch(CONFIG_INDEX_NAME, new ArrayList<>());
        int page = 1;
        for (Object o : list) {
            Map map = (Map) o;
            String databaseId = map.get("databaseId").toString();
            // 查询数据库配置
            List<Map<String, Object>> params = new ArrayList<>();
            Map<String, Object> queryConfig = new HashMap<>(4);
            queryConfig.put("searchType", "match");
            Map<String, Object> query = new HashMap<>(4);
            // id为数据库id
            query.put("id", databaseId);
            queryConfig.put("query", query);
            params.add(queryConfig);
            Map sourceData = (Map) esTemplate.conditionSearch(SOURCE_INDEX_NAME, params).get(0);

            if (sourceData.get("type") == null) {
                continue;
            }
            int type = (int) sourceData.get("type");
            // 1 mysql
            if (type == 1) {
                // 获取数据库连接
                HikariDataSource dataSource = DatabaseUtils.getDataSource(sourceData);
                Connection connection = dataSource.getConnection();
                // 分页查询
                int start = (page - 1) * PAGE_SIZE;
                int end = page * PAGE_SIZE;
                Map<String, Object> titles = EsUtils.analysisTitle(map.get("title"));
                // 获取主键，内容字段和标题字段
                String sql = "select " + map.get("primaryKey") +" id," + map.get("fieldName") + " content " + titles.get("sqlString") + " from " + map.get("tableName") + " limit " + start + "," + end;

                ResultSet resultSet = connection.createStatement().executeQuery(sql);
                // 索引查询结果
                while (resultSet.next()) {
                    if (StringUtils.hasLength(resultSet.getString("content"))) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", resultSet.getString("id"));
                        data.put("type", map.get("type"));
                        data.put("schema", map.get("schema"));
                        data.put("tableName", map.get("tableName"));
                        data.put("fieldName", map.get("fieldName"));
                        data.put("time", System.currentTimeMillis());
                        data.put("title", EsUtils.groupTitle(map.get("title"), (List) titles.get("keys"), resultSet));
                        data.put("content", resultSet.getString("content"));
                        esTemplate.addDoc(DATA_INDEX, resultSet.getString("id"), data);
                    }
                }
                DatabaseUtils.closeDataSourceAndConnection(dataSource, connection);
            }
        }
    }

}
