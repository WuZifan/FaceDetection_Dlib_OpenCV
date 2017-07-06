package com.wzf.face.landmarks2.Model;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by qq on 2017/6/16.
 * 用来保存人脸五官边缘在原图中的信息。
 */

public class LandMarks {
    private ArrayList<Integer> x;
    private ArrayList<Integer> y;

    public LandMarks(){

    }

    public LandMarks(List x, List y){
        this.x=new ArrayList<Integer>(x);
        this.y=new ArrayList<Integer>(y);
    }

    public int getMaxX(){
        return Collections.max(this.x);
    }

    public int getMaxY(){
        return Collections.max(this.y);
    }

    public int getMinX(){
        return Collections.min(this.x);
    }

    public int getMinY(){
        return Collections.min(this.y);
    }

    public Point getPoint(int cor){
        if( cor>=0 && cor<this.x.size() ){
            return new Point(this.x.get(cor),this.y.get(cor));
        }else{
            return null;
        }
    }

    /**
     * 拿到脸一圈的坐标点
     * @return
     */
    public List<Point> getFaceEdge(){
        List<Point> points=new ArrayList<Point>();
        for (int i=0;i<=16;i++){
            Point point=new Point(this.x.get(i),this.y.get(i));
            points.add(point);
        }
        return points;
    }

    /**
     * 拿到图片中左边的眉毛
     * @return
     */
    public List<Point> getLeftEyeBrow(){
        List<Point> points=new ArrayList<Point>();
        for (int i=17;i<=21;i++){
            Point point=new Point(this.x.get(i),this.y.get(i));
            points.add(point);
        }
        return points;
    }

    /**
     * 拿到图片中右边的眉毛
     * @return
     */
    public List<Point> getRightEyeBrow(){
        List<Point> points=new ArrayList<Point>();
        for (int i=22;i<=26;i++){
            Point point=new Point(this.x.get(i),this.y.get(i));
            points.add(point);
        }
        return points;
    }

    /**
     * 拿到鼻子
     * @return
     */
    public List<Point> getNose(){
        List<Point> points=new ArrayList<Point>();
        for (int i=27;i<=35;i++){
            Point point=new Point(this.x.get(i),this.y.get(i));
            points.add(point);
        }
        return points;
    }

    /**
     * 拿到图片中左边眼睛
     * @return
     */
    public List<Point> getLeftEye(){
        List<Point> points=new ArrayList<Point>();
        for (int i=36;i<=41;i++){
            Point point=new Point(this.x.get(i),this.y.get(i));
            points.add(point);
        }
        return points;
    }

    /**
     * 拿到图片中右边眼睛
     * @return
     */
    public List<Point> getRightEye(){
        List<Point> points=new ArrayList<Point>();
        for (int i=42;i<=47;i++){
            Point point=new Point(this.x.get(i),this.y.get(i));
            points.add(point);
        }
        return points;
    }

    /**
     * 拿到嘴唇外部轮廓
     * @return
     */
    public List<Point> getLipOuterEdge(){
        List<Point> points=new ArrayList<Point>();
        for (int i=48;i<=59;i++){
            Point point=new Point(this.x.get(i),this.y.get(i));
            points.add(point);
        }
        return points;
    }

    /**
     * 拿到上嘴唇
     * @return
     */
    public List<Point> getUpperLips(){
        List<Point> points=new ArrayList<Point>();
        for (int i=48;i<=54;i++){
            Point point=new Point(this.x.get(i),this.y.get(i));
            points.add(point);
        }
        // 按照划线的顺序
        for (int i=64;i>=60;i--){
            Point point=new Point(this.x.get(i),this.y.get(i));
            points.add(point);
        }
        return points;
    }

    /**
     * 获得下嘴唇
     * @return
     */
    public List<Point> getUnderLips(){
        List<Point> points=new ArrayList<Point>();
        for (int i=54;i<=59;i++){
            Point point=new Point(this.x.get(i),this.y.get(i));
            points.add(point);
        }

        Point point_temp=new Point(this.x.get(48),this.y.get(48));
        points.add(point_temp);

        Point point_temp2=new Point(this.x.get(60),this.y.get(60));
        points.add(point_temp2);

        //按照划线的顺序
        for (int i=67;i>=64;i--){
            Point point=new Point(this.x.get(i),this.y.get(i));
            points.add(point);
        }
        return points;
    }

}
