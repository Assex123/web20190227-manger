package com.zbf.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private String id;
    private String userName;
    private  String dengluyonghuId;
    private  String shijuanId;

    private  String linshiId;

}
