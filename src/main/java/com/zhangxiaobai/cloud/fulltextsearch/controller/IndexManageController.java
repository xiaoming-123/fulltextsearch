package com.zhangxiaobai.cloud.fulltextsearch.controller;

import com.zhangxiaobai.cloud.fulltextsearch.bean.Result;
import com.zhangxiaobai.cloud.fulltextsearch.service.IndexManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 索引管理
 * @author zhangcq
 */
@Controller
@RequestMapping("/search/index/manage")
public class IndexManageController {

    @Resource
    private IndexManageService sourceManagerService;

    @RequestMapping("/addIndex")
    @ResponseBody
    public Result addIndex(@RequestBody Map<String, Object> source) throws Exception {
        sourceManagerService.addIndex(source);
        return Result.SUCCESS;
    }

    @RequestMapping("/deleteIndex")
    @ResponseBody
    public Result deleteIndex(String indexName) throws Exception {
        sourceManagerService.deleteIndex(indexName);
        return Result.SUCCESS;
    }


    @RequestMapping("/getIndex")
    @ResponseBody
    public Result getIndex() throws Exception {
        Object o = sourceManagerService.getIndex();
        return Result.SUCCESS(o);
    }

}
