package com.zhangxiaobai.cloud.fulltextsearch.controller;

import com.zhangxiaobai.cloud.fulltextsearch.bean.Result;
import com.zhangxiaobai.cloud.fulltextsearch.service.FulltextSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 查询
 * @author zhangcq
 */
@Controller
@RequestMapping("/search/fulltextsearch")
public class FulltextSearchController {

    @Resource
    private FulltextSearchService service;

    @RequestMapping("/search")
    @ResponseBody
    public Result search(String indexName, String queryString) throws Exception {
        return Result.SUCCESS(service.search(indexName, queryString));
    }
}
