package com.zhangxiaobai.cloud.fulltextsearch.controller;

import com.zhangxiaobai.cloud.fulltextsearch.bean.Result;
import com.zhangxiaobai.cloud.fulltextsearch.service.SourceManageService;
import com.zhangxiaobai.cloud.fulltextsearch.util.JsonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 数据源管理
 * @author zhangcq
 */
@Controller
@RequestMapping("/search/source/manage")
public class SourceManageController {

    @Resource
    private SourceManageService service;

    @RequestMapping("/addSource")
    @ResponseBody
    public Result addSource(String id, String sourceData) throws Exception {
        service.addSource(id, JsonUtil.toBean(Map.class, sourceData));
        return Result.SUCCESS;
    }

    @RequestMapping("/getSource")
    @ResponseBody
    public Result getSource() throws Exception {
        return Result.SUCCESS(service.getSource());
    }

    @RequestMapping("/testConnect")
    @ResponseBody
    public Result testConnect(String sourceData) throws Exception {
        if (service.testConnect(JsonUtil.toBean(Map.class, sourceData))) {
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }

    @RequestMapping("/getTableAndField")
    @ResponseBody
    public Result getTableAndField(String sourceData) throws Exception {
        return Result.SUCCESS(service.getTableAndField(JsonUtil.toBean(Map.class, sourceData)));
    }

    @RequestMapping("/getConfigListByDatabaseId")
    @ResponseBody
    public Result getConfigListByDatabaseId(String databaseId) throws Exception {
        return Result.SUCCESS(service.getConfigListByDatabaseId(databaseId));
    }

    @RequestMapping("/saveConfig")
    @ResponseBody
    public Result saveConfig(String configData) throws Exception {
        service.saveConfig(JsonUtil.toBeanList(Map.class, configData));
        return Result.SUCCESS;
    }

    @RequestMapping("/uploadFile")
    @ResponseBody
    public Result uploadFile(MultipartFile file) throws Exception {
        service.uploadFile(file);
        return Result.SUCCESS;
    }
}
