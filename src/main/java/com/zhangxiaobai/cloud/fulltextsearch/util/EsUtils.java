package com.zhangxiaobai.cloud.fulltextsearch.util;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.slf4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 数据库工具类
 *
 * @author zhangcq
 */
public class EsUtils {

    private EsUtils() {
    }

    /**
     * 匹配命名参数的正则表达式
     */
    private final static String REGEX_NAMEDSQL = "\\$\\{\\s*([.\\w]+)\\s*}";

    /**
     * 查询和结果处理
     *
     * @param client  查询客户端
     * @param request 请求
     * @return list
     * @throws IOException io
     */
    public static List searchAndDealResult(RestHighLevelClient client, SearchRequest request) throws IOException {
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取命中的文档
        SearchHit[] hits = response.getHits().getHits();
        return Arrays.stream(hits).map(EsUtils::analysisResult).collect(Collectors.toList());
    }

    /**
     * 获取标题里的参数
     *
     * @param title 标题
     * @return sqlString sql片段、keys 字段列表
     */
    public static Map<String, Object> analysisTitle(Object title) {
        Map<String, Object> result = new HashMap<>(4);
        result.put("sqlString", "");
        result.put("keys", new ArrayList<>());
        if (title == null) {
            return result;
        }
        String titleStr = title.toString();
        Pattern pattern = Pattern.compile(REGEX_NAMEDSQL);
        Matcher matcher = pattern.matcher(titleStr);
        StringBuilder builder = new StringBuilder();
        List<String> keys = new ArrayList<>();
        while (matcher.find()) {
            builder.append(",").append(matcher.group(1).toLowerCase());
            keys.add(matcher.group(1));
        }
        result.put("sqlString", builder.toString());
        result.put("keys", keys);
        return result;
    }

    /**
     * 组合真实标题
     *
     * @param title     标题
     * @param keys      字段列表
     * @param resultSet 结果集
     * @return 真实标题
     * @throws SQLException sql
     */
    public static String groupTitle(Object title, List keys, ResultSet resultSet) throws SQLException {
        if (title == null) {
            return "";
        }
        String titleStr = title.toString();
        for (Object o : keys) {
            if (o == null) {
                continue;
            }
            titleStr = titleStr.replaceAll("\\$\\{" + o + "}", resultSet.getString(o.toString().toLowerCase()));
        }
        return titleStr;
    }

    /**
     * 解析查询结果
     *
     * @param searchHit 命中结果
     * @return 解析后的集合
     */
    private static Map<String, Object> analysisResult(SearchHit searchHit) {
        // 获取源集合
        Map<String, Object> sourceMap = searchHit.getSourceAsMap();
        sourceMap.put("id", searchHit.getId());
        // attachment不为空表示结果是文件文档
        if (sourceMap.get("attachment") != null) {
            // 文件名->标题
            sourceMap.put("title", sourceMap.get("name"));
            sourceMap.put("content", ((Map) sourceMap.get("attachment")).get("content"));
            sourceMap.put("fileType", sourceMap.get("type"));
            // 文件类型值为2
            sourceMap.put("type", 2);
        }
        return analysisHighlightField(searchHit, sourceMap, "content", "title", "attachment.content");
    }

    /**
     * 解析高亮字段
     *
     * @param searchHit 命中结果
     * @param sourceMap 源集合
     * @param fields    需要解析的字段
     * @return 解析后集合
     */
    private static Map<String, Object> analysisHighlightField(SearchHit searchHit, Map<String, Object> sourceMap, String... fields) {
        // 获取高亮字段集合
        Map<String, HighlightField> highlightFieldMap = searchHit.getHighlightFields();
        if (!highlightFieldMap.isEmpty()) {
            for (String field : fields) {
                // 获取高亮字段
                HighlightField highlightField = highlightFieldMap.get(field);
                if (highlightField != null) {
                    // 文件类型attachment.content置换为content
                    field = "attachment.content".equals(field) ? "content" : field;
                    // 原始内容
                    String sourceText = sourceMap.get(field).toString();
                    String highResult = Arrays.stream(highlightField.getFragments()).map((text -> {
                        // 原始高亮内容
                        String textString = text.string();
                        // 去掉高亮内容中的标签
                        String replaceText = textString.replaceAll("<span style='color: red'>", "").replaceAll("</span>", "");
                        // 替换原始内容为高亮
                        return sourceText.replace(replaceText, textString);
                    })).collect(Collectors.joining("\n"));
                    // 替换原始值
                    sourceMap.put(field, highResult);
                }
            }
        }
        return sourceMap;
    }

    /**
     * 索引操作
     *
     * @param logger    记录器
     * @param client    客户端
     * @param request   请求
     * @param operation 操作
     * @param indexName 索引
     * @return 结果
     * @throws Exception 异常
     */
    public static boolean indexResponse(Logger logger, RestHighLevelClient client, IndexRequest request, String operation, String indexName) throws Exception {
        boolean result = true;
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        if (response.getResult() == DocWriteResponse.Result.CREATED) {
            logger.info("{} {} CREATE SUCCESS", operation, indexName);
        } else if (response.getResult() == DocWriteResponse.Result.UPDATED) {
            logger.info("{} {} UPDATE SUCCESS", operation, indexName);
        } else {
            logger.info("{} {} OPTION FIELD", operation, indexName);
            result = false;
        }
        return result;
    }
}
