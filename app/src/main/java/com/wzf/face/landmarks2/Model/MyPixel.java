package com.wzf.face.landmarks2.Model;

/**
 * Created by qq on 2017/6/19.
 * 用来描述一个像素点RGB是三个通道的颜色的
 */

public class MyPixel {
    private int R;
    private int G;
    private int B;

    public MyPixel(){

    }

    public MyPixel(int r,int g,int b){
        this.R=r;
        this.G=g;
        this.B=b;
    }

    public MyPixel(int rgb){
        this.R=(rgb & 0x00FF0000) >> 4*4;
        this.G=(rgb & 0x0000FF00) >> 2*4;
        this.B=rgb & 0x000000FF;
    }

    public int getR(){
        return this.R;
    }

    public int getG(){
        return this.G;
    }

    public int getB(){
        return this.B;
    }

    @Override
    public String toString() {
        return "R: "+this.R+" G: "+this.G+" B: "+this.B;
    }
}
