package com.example.challenge;

import java.util.List;

public class ExampleItem {
    private int mImageResource;
    private String mText1;
    private String mText2;

    public ExampleItem(int mImageResource, String mText1, String mText2, List<ExampleTrain> subItemList, boolean expanded) {
        this.mImageResource = mImageResource;
        this.mText1 = mText1;
        this.mText2 = mText2;
        this.subItemList = subItemList;
        this.expanded = expanded;
    }

    public List<ExampleTrain> getSubItemList() {
        return subItemList;
    }

    public void setSubItemList(List<ExampleTrain> subItemList) {
        this.subItemList = subItemList;
    }

    private List<ExampleTrain> subItemList;
    private boolean expanded;
    public ExampleItem(int imageResource, String text1, String text2) {
        mImageResource = imageResource;
        mText1 = text1;
        mText2 = text2;
    }
    public int getImageResource() {
        return mImageResource;
    }
    public String getText1() {
        return mText1;
    }
    public String getText2() {
        return mText2;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public ExampleItem(int mImageResource, String mText1, String mText2, boolean expanded) {
        this.mImageResource = mImageResource;
        this.mText1 = mText1;
        this.mText2 = mText2;
        this.expanded = false;
    }
}