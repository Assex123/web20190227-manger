package com.zbf.mapper;

import com.zbf.core.page.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TiKuMapper {
    public  int addTiKuInfo(Map<String,Object> map);

    List<Map<String, Object>> getTikuList(Page<Map<String, Object>> page);

    public  int  updateTiKuInfo(Map<String,Object> map);

 /*  public   void toAddShiTi(Map<String, Object> parameterMap);*/

     public int deleteTikuList(Map<String, Object> paramsJsonMap);

    List<Map<String, Object>> getshitiguanli(Page<Map<String, Object>> page);

    void addShiTi(Map<String, Object> parameterMap);

    public List<Map<String,Object>> getShitiDataListByTiKu(Page<Map<String,Object>> page);

    Map<String, Object> getShiTiById(Map<String, Object> map);
}
