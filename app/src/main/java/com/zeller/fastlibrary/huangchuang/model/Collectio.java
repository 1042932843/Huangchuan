package com.zeller.fastlibrary.huangchuang.model;

/**
 * Created by Administrator on 2017/6/15 0015.
 */
public class Collectio {
    private String title;
    private String newsId;
    private String collectionId;
    private String type;
    private String createDate;
    private String newsImgUrl;
    public  static  boolean isCheck;  //该属性主要标志CheckBox是否选中



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getNewsImgUrl() {
        return newsImgUrl;
    }

    public void setNewsImgUrl(String newsImgUrl) {
        this.newsImgUrl = newsImgUrl;
    }
}
