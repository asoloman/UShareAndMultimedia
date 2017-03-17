package com.dbyc.ushareandmultimedia.mediarelease.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-06-30.
 */
public class AudioAndVideoBean implements Serializable {

    private String name;//文件标题
    private String url;//文件路径
    private long fielsize;//文件大小
    private Bitmap videoImg;//视频图片
    private int duration;//时长
    private int IsClick; //是否选中  1 = 选中 0 =未选中;

    public int getIsClick() {
        return IsClick;
    }

    public void setIsClick(int isClick) {
        IsClick = isClick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getFielsize() {
        return fielsize;
    }

    public void setFielsize(long fielsize) {
        this.fielsize = fielsize;
    }

    public Bitmap getVideoImg() {
        return videoImg;
    }

    public void setVideoImg(Bitmap videoImg) {
        this.videoImg = videoImg;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "AudioAndVideoBean{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", fielsize=" + fielsize +
                ", videoImg=" + videoImg +
                ", duration=" + duration +
                '}';
    }
}
