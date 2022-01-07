package com.zhangxiaobai.cloud.fulltextsearch.service.impl;

import com.zhangxiaobai.cloud.fulltextsearch.repository.ESTemplate;
import com.zhangxiaobai.cloud.fulltextsearch.service.IndexManageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 索引管理
 * @author zhangcq
 */
@Service
public class IndexManageServiceImpl implements IndexManageService {

    @Resource
    private ESTemplate template;


    @Override
    public void addIndex(Map<String, Object> source) {

    }

    @Override
    public Object getIndex() throws Exception {
        return template.searchAllIndex();
    }

    @Override
    public void deleteIndex(String indexName) throws Exception {

    }

}
