package com.njust.controller;


import com.njust.base.BaseInfoProperties;
import com.njust.bo.VlogBO;
import com.njust.grace.result.GraceJSONResult;
import com.njust.service.VlogService;
import com.njust.utils.PagedGridResult;
import com.njust.vo.IndexVlogVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Api(tags = "短视频相关接口")
@RequestMapping("vlog")
public class VlogController extends BaseInfoProperties {

    @Autowired
    private VlogService vlogService;

    @PostMapping("publish")
    public GraceJSONResult publish(@Valid @RequestBody VlogBO vlogBO) {
        vlogService.createVlog(vlogBO);
        return GraceJSONResult.ok();
    }

    @GetMapping("indexList")
    public GraceJSONResult indexList(@RequestParam(defaultValue = "") String search,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize) {   //要写默认值,否则knife4j测试强制要求写这个字段
        if(page==null)
            page=COMMON_START_PAGE;
        if(pageSize==null)
            pageSize=COMMON_PAGE_SIZE;
        PagedGridResult gridResult = vlogService.getIndexVlogList(search, page, pageSize);
        return GraceJSONResult.ok(gridResult);
    }

    @GetMapping("detail")
    public GraceJSONResult detail(@RequestParam(defaultValue = "") String userId,
                                  @RequestParam String vlogId){
        IndexVlogVO detail = vlogService.getVlogDetailById(vlogId);
        return GraceJSONResult.ok(detail);
    }

}
