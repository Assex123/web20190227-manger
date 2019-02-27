package com.zbf.service;

import com.zbf.core.page.Page;
import com.zbf.core.utils.UID;
import com.zbf.entity.User;
import com.zbf.mapper.ShiJuanMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ShiJuanService {

    @Resource
    private ShiJuanMapper shiJuanMapper;

    @Resource
    private RedisTemplate redisTemplate;


    public List<Map<String, Object>> getFenLei() {
     List<Map<String,Object>>  list=shiJuanMapper.getFenLei();
     return  list;
    }


    public void getUserList(Page<Map<String, Object>> page) {
        List<Map<String,Object>> list=shiJuanMapper.getUserList(page);
        list.forEach((item)->{
            if(item.get("sex").toString().equals("1")){
                item.put("sex","男");
            }else{
                item.put("sex","女");
            }
            item.entrySet().forEach((kk)->{
                if(kk.getKey().equals("createTime")){
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
                    String format = sf.format(kk.getValue());
                    kk.setValue(format);
                }
            });
        });
        page.setResultList(list);
    }

    public void createSj(Map<String, Object> paramsJsonMap, List<User> user) {
        String id = UID.getUUIDOrder();
        paramsJsonMap.put("id",id);
        shiJuanMapper.createSj(paramsJsonMap);

        for(User u:user){
            u.setShijuanId(paramsJsonMap.get("id").toString());
            u.setLinshiId(UID.getUUIDOrder());
            shiJuanMapper.insertsjUser(u);
        }
        //将redis中的数据移除
        Set keys = redisTemplate.opsForHash().keys(paramsJsonMap.get("userid").toString());
        for (Object key : keys) {
            redisTemplate.opsForHash().delete(paramsJsonMap.get("userid").toString(),key);
        }
    }
}
