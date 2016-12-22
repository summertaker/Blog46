package com.summertaker.blog46.data;

import java.io.Serializable;

public class Blog implements Serializable {

    private String title;
    private String url;
    private String pageParam;
    private int maxPage;

    public Blog(String title, String url, String pageParam, int maxPage) {
        this.title = title;
        this.url = url;
        this.pageParam = pageParam;
        this.maxPage = maxPage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPageParam() {
        return pageParam;
    }

    public void setPageParam(String pageParam) {
        this.pageParam = pageParam;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }
}
