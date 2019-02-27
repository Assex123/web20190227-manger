package com.zbf.entity;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;


@Data
public class Shiti {

    @Field
    private  String ids;
    @Field
    private  String createuserid;
    @Field
    private  String tixingid;
    @Field
    private  String  tikuid;
    @Field
    private  String shitizhuangtai;
    @Field
    private  String nanduid;
    @Field
    private  String laiyuan;
    @Field
    private  String tigan;
    @Field
    private  String daan;
    @Field
    private Date createtime;
    @Field
    private  String timujiexi;

    @Field
    private  String userName;
}
