package com.zbf.web.shijuan;


import com.alibaba.fastjson.JSONObject;
import com.zbf.common.ResponseResult;
import com.zbf.core.CommonUtils;
import com.zbf.core.page.Page;
import com.zbf.entity.User;
import com.zbf.service.ShiJuanService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("shijuan")
public class ShiJuanController {
    @Resource
    private ShiJuanService shiJuanService;

    @Resource
    private RedisTemplate redisTemplate;



    @RequestMapping("getFenLei")
    public ResponseResult getFenLei(){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        List<Map<String,Object>> list= shiJuanService.getFenLei();
        responseResult.setResult(list);
        return  responseResult;
    }

    @RequestMapping("getUserList")
    public ResponseResult getUserList(HttpServletRequest request){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        Map<String, Object> parameterMap = CommonUtils.getParamsJsonMap(request);
        Page<Map<String, Object>> page = new Page<>();
        Page.setPageInfo(page,parameterMap);
        shiJuanService.getUserList(page);
        responseResult.setResult(page);
        return  responseResult;
    }


    @RequestMapping("createSj")
    public  ResponseResult createSj(HttpServletRequest request) throws ParseException {

        ResponseResult responseResult=ResponseResult.getResponseResult();
        Map<String, Object> paramsJsonMap = CommonUtils.getParamsJsonMap(request);
        List<User> user = redisTemplate.opsForHash().values(paramsJsonMap.get("userid").toString());


        String date6 = paramsJsonMap.get("date6").toString();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse=sf.parse(date6);


        shiJuanService.createSj(paramsJsonMap,user);

        return responseResult;

    }


}
