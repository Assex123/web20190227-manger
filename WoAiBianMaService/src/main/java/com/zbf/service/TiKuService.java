package com.zbf.service;

import com.alibaba.fastjson.JSON;
import com.zbf.common.ResponseResult;
import com.zbf.core.page.Page;
import com.zbf.enmu.MyRedisKey;
import com.zbf.mapper.TiKuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class TiKuService {

    @Autowired
    private TiKuMapper tiKuMapper;

    @Transactional
    public  int addTiKuInfo(Map<String,Object> map,RedisTemplate  redisTemplate){
        redisTemplate.opsForList().rightPush(MyRedisKey.TIKU.getKey(),map);
        return tiKuMapper.addTiKuInfo(map);
    }

    public  void getTikuList(Page<Map<String,Object>> page){
        List<Map<String,Object>> list=tiKuMapper.getTikuList(page);
        list.forEach((item)->{
            if (item.get("tikuzhuangtai").toString().equals("1")){
                item.put("tikuzhuangtai","开放");
            }else {
                item.put("tikuzhuangtai","关闭");
            }
        });
        page.setResultList(list);
    }

    /**
     * 更新题库信息
     * @param map
     */
    @Transactional
    public  void  updateTiKuInfo(Map<String,Object> map){
        tiKuMapper.updateTiKuInfo(map);

    }

   /* public void toAddShiTi(Map<String, Object> parameterMap) {
        ResponseResult responseResult=ResponseResult.getResponseResult();
        tiKuMapper.toAddShiTi(parameterMap);
    }*/

    public void deleteTikuList(Map<String, Object> paramsJsonMap) {
        tiKuMapper.deleteTikuList(paramsJsonMap);
    }

    public  void getshitiguanli(Page<Map<String,Object>> page){
     List<Map<String,Object>> list=tiKuMapper.getshitiguanli(page);
     page.setResultList(list);
    }

    public void addShiTi(Map<String, Object> parameterMap) {
        ResponseResult responseResult = ResponseResult.getResponseResult();
        tiKuMapper.addShiTi(parameterMap);
    }

    public Map<String,Object> getShiTiById(Map<String,Object> map){
        Map<String, Object> params = tiKuMapper.getShiTiById ( map );
        if(params.get ( "tixingid" )!=null&&params.get ( "tixingid" ).toString ().equals ( "1" )){//单选题
            //试题的选项编号
            params.put ( "xuanxiangbianhao",JSON.toJSONString ( JSON.parse (params.get ( "xuanxiangbianhao" ).toString () )));
            //试题的选项描述
            params.put ( "xuanxiangmiaoshu",JSON.toJSONString ( JSON.parse (params.get ( "xuanxiangmiaoshu" ).toString () )));

        }else if(params.get ( "tixingid" )!=null&&params.get ( "tixingid" ).toString ().equals ( "2" )){//多选题
            //试题答案
            params.put ( "daan",JSON.toJSONString ( JSON.parse (params.get ( "daan" ).toString () )) );
            //试题的选项编号
            params.put ( "xuanxiangbianhao",JSON.toJSONString ( JSON.parse (params.get ( "xuanxiangbianhao" ).toString () )));
            //试题的选项描述
            params.put ( "xuanxiangmiaoshu", JSON.toJSONString ( JSON.parse (params.get ( "xuanxiangmiaoshu" ).toString () )));

        }else if(params.get ( "tixingid" )!=null&&params.get ( "tixingid" ).toString ().equals ( "3" )){//判断题
            //试题的判断描述
            Object miaoshu=params.get ( "xuanxiangmiaoshu" )!=null?params.get ( "xuanxiangmiaoshu" ):"";
            params.put ( "xuanxiangmiaoshu",JSON.toJSONString ( JSON.parse (miaoshu.toString () )) );

        }

        return params;
    }

}
