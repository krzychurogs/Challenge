package com.example.challenge;

public class ChoiceRoadItem {
    private String mImageResource;
    private String mText1;

    public ChoiceRoadItem(String imageResource, String text1) {
        mImageResource = imageResource;
        mText1 = text1;

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
}