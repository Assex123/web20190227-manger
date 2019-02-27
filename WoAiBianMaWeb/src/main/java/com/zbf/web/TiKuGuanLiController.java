package com.zbf.web;

import com.alibaba.fastjson.JSON;
import com.zbf.common.ResponseResult;
import com.zbf.core.CommonUtils;
import com.zbf.core.page.Page;
import com.zbf.core.utils.FileUploadDownUtils;
import com.zbf.core.utils.UID;
import com.zbf.enmu.MyRedisKey;
import com.zbf.entity.Shiti;
import com.zbf.entity.SolrPage;
import com.zbf.mapper.TiKuMapper;
import com.zbf.oauthLogin.User;
import com.zbf.service.TiKuService;
import com.zbf.zhongjian.test2.WebSocketServerTest;
import io.jsonwebtoken.Claims;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("tiku")
@RestController
public class TiKuGuanLiController {

    @Autowired
    private TiKuService tiKuService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SolrClient solrClient;

    @Autowired
    private TiKuMapper tiKuMapper;

    /**
     * 添加题库信息
     * @param request
     * @return
     */
    @RequestMapping("toaddTiKuInfo")
    public ResponseResult toaddTiKuInfo(HttpServletRequest request){
     ResponseResult responseResult =ResponseResult.getResponseResult();
     //获取数据
        Map<String, Object> parameterMap = CommonUtils.getParamsJsonMap(request);
        //存入数据
        try {
            parameterMap.put("id", UID.getUUIDOrder());
            tiKuService.addTiKuInfo(parameterMap,redisTemplate);
            responseResult.setSuccess("ok");
        }catch (Exception e){
            e.printStackTrace();
            responseResult.setError("error");
        }

        return  responseResult;
    }


    /**
     * 题库列表
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("getTikuList")
    public  ResponseResult getTikuList(HttpServletRequest httpServletRequest){
        Map<String, Object> paramsJsonMap = CommonUtils.getParamsJsonMap(httpServletRequest);
        Page<Map<String,Object>>  page=new Page<>();
        ResponseResult responseResult=ResponseResult.getResponseResult();
        Page.setPageInfo(page,paramsJsonMap);
        //设置查询参数
        page.setParams(paramsJsonMap);
        Page.setPageInfo(page,paramsJsonMap);
        //
        tiKuService.getTikuList(page);
        responseResult.setResult(page);
        return  responseResult;
    }


    /**
     * 更新题库信息
     * @param request
     * @return
     */
    @RequestMapping("updateTiKuInfo")
    public  ResponseResult  updateTiKuInfo(HttpServletRequest request){

        ResponseResult responseResult = ResponseResult.getResponseResult();

        Map<String, Object> paramsJsonMap = CommonUtils.getParamsJsonMap(request);

        tiKuService.updateTiKuInfo(paramsJsonMap);

        responseResult.setResult("ok");
        return responseResult;

    }

    /**
     * 从redis中获取题库列表
     * @param request
     * @return
     */
    @RequestMapping("getTikuListFromRedis")
    public  ResponseResult getTikuListFromRedis(HttpServletRequest request){
        List<Map<String,Object>> range = redisTemplate.opsForList().range(MyRedisKey.TIKU.getKey(), 0, -1);

        ResponseResult responseResult=ResponseResult.getResponseResult();

        responseResult.setResult(range);

        return responseResult;
    }

    /**
     * 根据ID获取试题信息
     * @param request
     * @return
     */
    @RequestMapping("getShitiById")
    public ResponseResult getShitiById(HttpServletRequest request){

        ResponseResult responseResult=ResponseResult.getResponseResult ();

        Map<String, Object> parameterMap = CommonUtils.getParameterMap ( request );

        Map<String, Object> shiTiById = tiKuService.getShiTiById ( parameterMap );

        responseResult.setResult ( shiTiById );

        return responseResult;

    }

    /**
     * 添加试题
     * @param request
     * @return
     */
    @RequestMapping("toAddShiTi")
    public  ResponseResult toAddShiTi(HttpServletRequest request, Shiti shiti) throws ParseException {
        ResponseResult responseResult =ResponseResult.getResponseResult();
        //获取请求数据
        Map<String, Object> parameterMap = CommonUtils.getParameterMap(request);
        String uuidOrder = UID.getUUIDOrder();
        parameterMap.put("id",uuidOrder);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format= sf.format(new Date());
        parameterMap.put("createtime",format);

        shiti.setIds(parameterMap.get("id").toString());
        shiti.setCreatetime(sf.parse(format));
        shiti.setCreateuserid(parameterMap.get("userId").toString());
        shiti.setDaan(parameterMap.get("checkList").toString());
        shiti.setLaiyuan(parameterMap.get("laiyuan").toString());
        shiti.setNanduid(parameterMap.get("nanduid").toString());
        shiti.setShitizhuangtai(parameterMap.get("shitizhuangtai").toString());
        shiti.setTigan(parameterMap.get("tigan").toString());
        shiti.setTikuid(parameterMap.get("tikuid").toString());
        shiti.setTimujiexi(parameterMap.get("timujiexi").toString());
        shiti.setTixingid(parameterMap.get("shitileixing").toString());
        shiti.setUserName(parameterMap.get("userName").toString());

        try {
            solrClient.addBean(shiti);
            solrClient.commit();
            tiKuService.addShiTi(parameterMap);
            responseResult.setSuccess("ok");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult.setError("error");
        }
        return responseResult;

    }

    //删除题库
    @RequestMapping("deleteTikuList")
    public  ResponseResult deleteTikuList(HttpServletRequest request){

        ResponseResult responseResult=ResponseResult.getResponseResult();

        Map<String, Object> paramsJsonMap = CommonUtils.getParamsJsonMap(request);

        try {
            tiKuService.deleteTikuList(paramsJsonMap);
            responseResult.setSuccess("ok");
        }catch (Exception e){
            e.printStackTrace();
            responseResult.setError("error");
        }

        return  responseResult;
    }


    /**
     * 试题管理
     * @param request
     * @return
     */
    @RequestMapping("getshitiguanli")
    public  ResponseResult   getshitiguanli(HttpServletRequest request) throws IOException, SolrServerException {

        ResponseResult responseResult=ResponseResult.getResponseResult();
        Map<String, Object> maps = CommonUtils.getParamsJsonMap(request);
        SolrQuery solrQuery=new SolrQuery();
        solrQuery.set("q","*:*");
        SolrPage<Shiti> solrPage=new SolrPage<>();
        //传入
        SolrPage.setPageInfo(solrPage,maps);
        QueryResponse query=solrClient.query(solrQuery);
        List<Shiti> beans=query.getBeans(Shiti.class);
        solrPage.setResultList(beans);
        responseResult.setResult(solrPage);
        return responseResult;

    }


    /**
     * Excel文件上传,导入数据
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("toImportExcelData")
    public ResponseResult toImportExcelData(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        ResponseResult responseResult=ResponseResult.getResponseResult();
        //得到表格的输入流
        InputStream inputStream=file.getInputStream();
        //工作簿
        XSSFWorkbook xssfWorkbook=new XSSFWorkbook(inputStream);
        XSSFSheet sheetAt = xssfWorkbook.getSheetAt(0);
        int physicalNumberOfRows = sheetAt.getPhysicalNumberOfRows();//获取行数

        XSSFRow row1=sheetAt.getRow(0);
        XSSFCell cell = row1.getCell(0);
        cell.getStringCellValue();//获取字符数据
        List<Map<String,Object>> listdata=new ArrayList<>();
        for (int i=1;i<physicalNumberOfRows;i++){
            XSSFRow row = sheetAt.getRow(i);
            row.getPhysicalNumberOfCells();
            Map<String,Object> maprow=new HashMap<String, Object>();
            maprow.put("tigan",row.getCell(0).getStringCellValue());
            maprow.put("xuanxiangbianhao",row.getCell(1).getStringCellValue());
            List<String> xuanxiangmiaoshu=new ArrayList<>();
            xuanxiangmiaoshu.add(row.getCell(2).getStringCellValue());
            xuanxiangmiaoshu.add(row.getCell(3).getStringCellValue());
            xuanxiangmiaoshu.add(row.getCell(4).getStringCellValue());
            xuanxiangmiaoshu.add(row.getCell(5).getStringCellValue());

            maprow.put("xuanxiangmiaoshu", JSON.toJSONString(xuanxiangmiaoshu));
            //正确答案
            maprow.put("daan", row.getCell(6).getStringCellValue());
            if (row.getCell(7)!=null){
                maprow.put("timujiexi", row.getCell(7).getStringCellValue());
            }

            listdata.add(maprow);

        }

        System.out.println(JSON.toJSONString(listdata)+"666666");

        return  responseResult;

    }


    /**
     * 导出
     * @param request
     * @param response
     * @throws FileNotFoundException
     */
    @RequestMapping("getExceltemplate")
    public  void  getExceltemplate(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {

        File excelTemplate = FileUploadDownUtils.getExcelTemplate("exceltemplate/timu.xlsx");
        FileUploadDownUtils.responseFileBuilder ( response,excelTemplate,"数据模板【题目】.xlsx" );


    }

    /**
     * Excel数据的导出
     * @param response
     * @param request
     */
    @RequestMapping("exportExcelData")
    public  void  exportExcelData(HttpServletResponse response,HttpServletRequest request) throws IOException {
        //获取数据

        Page<Map<String,Object>> page=new Page<>();

        Map<String, Object> parameterMap = CommonUtils.getParameterMap(request);
        parameterMap.put("tikuid","1000001080314666");
        page.setPageSize(30);
        page.setParams(parameterMap);
        List<Map<String, Object>> resultList = tiKuMapper.getShitiDataListByTiKu(page);

        //poi的操作
        XSSFWorkbook xssfWorkbook=new XSSFWorkbook();

        XSSFSheet sheet = xssfWorkbook.createSheet("秀儿");

        XSSFRow row1 = sheet.createRow ( 0 );
        row1.createCell ( 0 ).setCellValue ( "ID" );
        row1.createCell ( 1 ).setCellValue ( "答案" );
        row1.createCell ( 2 ).setCellValue ( "答案解析" );
        row1.createCell ( 3 ).setCellValue ( "题干描述" );
        row1.createCell ( 4 ).setCellValue ( "试题类型" );

        for (int i=1;i<resultList.size();i++){
            Map<String, Object> map = resultList.get(i);
            XSSFRow row = sheet.createRow ( i+1 );
            List<String> cillect=map.keySet().stream().collect(Collectors.toList());

            for (int j=0;j<cillect.size();j++){
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(map.get(cillect.get(j))!=null?map.get(cillect.get(j)).toString():"");
            }
        }

        //输出工作簿
        String filename=new String("【实例】信息表.xlsx".getBytes (),"ISO8859-1");
        response.setContentType ( "application/octet-stream;charset=ISO8859-1" );
        response.setHeader("Content-Disposition", "attachment;filename="+filename);

        xssfWorkbook.write ( response.getOutputStream () );



    }



    @RequestMapping("sendTest")
        public  void  sendTest(){

        Session session = WebSocketServerTest.mapsession.get ( "1" );
        WebSocketServerTest.sendMessageFromServer ( session,"hi，i am server","1");
    }













}
