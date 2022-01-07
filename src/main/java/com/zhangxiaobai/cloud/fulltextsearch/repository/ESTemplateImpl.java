package com.zhangxiaobai.cloud.fulltextsearch.repository;

import com.zhangxiaobai.cloud.fulltextsearch.util.EsUtils;
import com.zhangxiaobai.cloud.fulltextsearch.util.JsonUtil;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ESTemplateImpl implements ESTemplate {

    private Logger logger = LoggerFactory.getLogger(ESTemplateImpl.class);

    @Resource
    private RestHighLevelClient client;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public boolean addOrUpdateIndex(String indexName) throws Exception {
        // 创建index请求
        IndexRequest indexRequest = new IndexRequest(indexName);
        return EsUtils.indexResponse(logger, client, indexRequest, "INDEX", indexName);
    }

    @Override
    public boolean deleteIndex(String indexName) throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest(indexName);
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        if (deleteResponse.getResult() == DocWriteResponse.Result.DELETED) {
            logger.info("INDEX {} DELETE SUCCESS", indexName);
        } else {
            logger.info("INDEX {} DELETE FIELD", indexName);
            return false;
        }
        return true;
    }

    @Override
    public Object searchAllIndex() throws Exception {
        Map<String, Object> params = new HashMap<>(0);
        // 发送请求
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, initHeader());
        ResponseEntity<String> exchange = restTemplate.exchange("http://localhost:9200/_cat/indices", HttpMethod.GET, requestEntity, String.class);
        List<Map> list = JsonUtil.toBeanList(Map.class, exchange.getBody());
        return list.stream().filter(item -> !item.get("index").toString().startsWith("."));
    }

    @Override
    public List fullTextSearch(String index, String queryString) throws IOException {
        SearchRequest request = new SearchRequest();
        if (StringUtils.hasLength(index)) {
            request.indices(index);
        } else {
            // 无指定索引则查询数据索引data、文件索引docwrite
            request.indices("data", "docwrite");
        }
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(1);
        sourceBuilder.size(500);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        // 设置高亮字段
        highlightBuilder.field("content");
        highlightBuilder.field("title");
        highlightBuilder.field("attachment.content");
        // 设置高亮标签
        highlightBuilder.preTags("<span style='color: red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        if (StringUtils.hasLength(queryString)) {
            // 全文检索，使用ik_smart中文分词
            QueryBuilder builder = QueryBuilders.queryStringQuery(QueryParser.escape(queryString)).analyzer("ik_smart");
            sourceBuilder.query(builder);
            request.source(sourceBuilder);
        }
        return EsUtils.searchAndDealResult(client, request);
    }

    @Override
    public List conditionSearch(String index, List<Map<String, Object>> queryList) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        queryList.forEach((value) -> {
            if (value.get("searchType") == null) {
                return;
            }
            String searchType = value.get("searchType").toString();

            switch (searchType) {
                case "match":
                    Map queryMap = (Map) value.get("query");
                    queryMap.forEach((queryKey, queryValue) -> {
                        QueryBuilder builder = QueryBuilders.matchQuery(queryKey.toString(), queryValue);
                        searchSourceBuilder.query(builder);
                    });
                    break;
                default:
                    break;
            }

        });
        SearchRequest request = new SearchRequest(index);
        request.source(searchSourceBuilder);
        return EsUtils.searchAndDealResult(client, request);
    }

    @Override
    public boolean addDoc(String indexName, String id, Map<String, Object> sourceData) throws Exception {
        IndexRequest request = new IndexRequest(indexName);
        if (StringUtils.hasLength(id)) {
            request.id(id);
        }
        request.source(sourceData);
        return EsUtils.indexResponse(logger, client, request, "DOC", indexName);
    }

    @Override
    public boolean addFileDoc(String indexName, Map<String, Object> data) throws Exception {
        IndexRequest request = new IndexRequest(indexName);
        request.source(JsonUtil.toJson(data), XContentType.JSON);
        request.setPipeline("attachment");
        return EsUtils.indexResponse(logger, client, request, "FILE DOC", indexName);
    }

    private HttpHeaders initHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return headers;
    }
}
