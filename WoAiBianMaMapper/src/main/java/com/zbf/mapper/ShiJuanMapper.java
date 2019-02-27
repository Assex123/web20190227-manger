package com.zbf.mapper;


import com.zbf.core.page.Page;
import com.zbf.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ShiJuanMapper {
    List<Map<String, Object>> getFenLei();

    List<Map<String, Object>> getUserList(Page<Map<String, Object>> page);

    void createSj(Map<String, Object> paramsJsonMap);

    void insertsjUser(User u);
}
