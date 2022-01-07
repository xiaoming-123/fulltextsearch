package com.zhangxiaobai.cloud.fulltextsearch.service.impl;

import com.zhangxiaobai.cloud.fulltextsearch.repository.ESTemplate;
import com.zhangxiaobai.cloud.fulltextsearch.service.FulltextSearchService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 全文检索
 * @author zhangcq
 */
@Service
public class FulltextSearchServiceImpl implements FulltextSearchService {

    @Resource
    private ESTemplate template;

    @Override
    public Object search(String indexName, String queryString) throws Exception {
        return template.fullTextSearch(indexName, queryString);
    }
}
