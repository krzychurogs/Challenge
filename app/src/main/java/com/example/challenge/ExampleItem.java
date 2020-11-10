package com.example.challenge;

public class ExampleItem {
    private int mImageResource;
    private int mImageResource1;
    private int mImageResource2;
    private String mText1;
    private String mText2;
    private String mText3;
    private String mText4;
    private String mText5;

    public ExampleItem(int imageResource,int imageResource1,int imageResource2, String text1,String text3,String text4,String text5) {
        mImageResource = imageResource;
        mText1 = text1;
        mText3 = text3;
        mText4=  text4;
        mText5=  text5;
        mImageResource1=imageResource1;
        mImageResource2=imageResource2;
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

    public String getmText3() {
        return mText3;
    }

    public void setmText3(String mText3) {
        this.mText3 = mText3;
    }

    public String getmText4() {
        return mText4;
    }

    public void setmText4(String mText4) {
        this.mText4 = mText4;
    }

    public String getmText5() {
        return mText5;
    }

    public void setmText5(String mText5) {
        this.mText5 = mText5;
    }

    public int getmImageResource1() {
        return mImageResource1;
    }

    public void setmImageResource1(int mImageResource1) {
        this.mImageResource1 = mImageResource1;
    }

    public int getmImageResource2() {
        return mImageResource2;
    }

    public void setmImageResource2(int mImageResource2) {
        this.mImageResource2 = mImageResource2;
    }
}