package com.bjpowernode.p2p.model;

import java.io.Serializable;

/**
 * 分页模型
 */
public class PageModel implements Serializable {

    //首页
    private Integer firstPage=1;
    //当前页
    private  Long cunPage;

    //每页显示多少个
    private Integer pageContent=9;

    //总记录数
    private  Long totalCount;
    //尾页
    private  Long lastPage;

    // List contents：存放真实数据


    public PageModel() {
    }

    public PageModel(Integer firstPage, Integer pageContent) {
        this.firstPage = firstPage;
        this.pageContent = pageContent;
    }

    public Integer getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(Integer firstPage) {
        this.firstPage = firstPage;
    }

    public Long getCunPage() {
        return cunPage;
    }

    public void setCunPage(Long cunPage) {
        this.cunPage = cunPage;
    }

    public Integer getPageContent() {
        return pageContent;
    }

    public void setPageContent(Integer pageContent) {
        this.pageContent = pageContent;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getLastPage() {
        return lastPage=totalCount%pageContent>0?totalCount/pageContent+1:totalCount/pageContent;
    }


}
