package com.example.challenge;

public class ChoiceRoadItem {
    private String mImageResource;
    private int mImageResourceDist;
    private int mImageResourceAvg;
    private int mImageResourceStats;
    private String mText1;
    private String mText2;
    private String mText3;

    public ChoiceRoadItem(String imageResource,int imageResourceDist,int imageResourceAvg,String text1,String text2,String text3) {
        mImageResource = imageResource;
        mText1 = text1;
        mText2 = text2;
        mText3 = text3;
        mImageResourceDist=imageResourceDist;
        mImageResourceAvg=imageResourceAvg;
    }

    public ChoiceRoadItem() {

    }

    public void setmImageResource(String mImageResource) {
        this.mImageResource = mImageResource;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getImageResource() {
        return mImageResource;
    }

    public String getText1() {
        return mText1;
    }

    public int getmImageResourceDist() {
        return mImageResourceDist;
    }

    public void setmImageResourceDist(int mImageResourceDist) {
        this.mImageResourceDist = mImageResourceDist;
    }

    public String getmImageResource() {
        return mImageResource;
    }

    public int getmImageResourceAvg() {
        return mImageResourceAvg;
    }

    public void setmImageResourceAvg(int mImageResourceAvg) {
        this.mImageResourceAvg = mImageResourceAvg;
    }

    public String getmText1() {
        return mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }

    public String getmText3() {
        return mText3;
    }

    public void setmText3(String mText3) {
        this.mText3 = mText3;
    }

    public int getmImageResourceStats() {
        return mImageResourceStats;
    }

    public void setmImageResourceStats(int mImageResourceStats) {
        this.mImageResourceStats = mImageResourceStats;
    }
}