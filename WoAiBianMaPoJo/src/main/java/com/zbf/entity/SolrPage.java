package com.zbf.entity;


import java.util.List;
import java.util.Map;

public class SolrPage<T> {

    private  int pageNo=1;

    private  int pageSize=10;

    private  long totalPage;

    private  long  totalCount;

    private List<T> resultList;


    public static void setPageInfo(SolrPage page, Map<String,Object> mapp){
        if(mapp.get ( "pageNo" )!=null){
            page.setPageNo ( Integer.valueOf ( mapp.get ( "pageNo" ).toString () ) );
        }
        if(mapp.get ( "pageSize" )!=null){
            page.setPageSize ( Integer.valueOf ( mapp.get ( "pageSize" ).toString () ) );
        }
    }
    public long getTotalPage() {

        if(totalCount%pageSize==0){
            this.totalPage=totalCount/pageSize;
        }else{
            this.totalPage=totalCount/pageSize+1;
        }
        return totalPage;
    }


    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getResultList() {
        return resultList;
    }

    public void setResultList(List<T> resultList) {
        this.resultList = resultList;
    }
}
