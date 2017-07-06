package com.wzf.face.landmarks2.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by qq on 2017/6/20.
 */

public class FillColorUtils {
    private static int BorderColor=0xFF000000;
    private static Stack<Point> mStacks = new Stack<Point>();
    private static boolean hasBorderColor=true;
    private static int xMax;
    private static int xMin;
    private static int yMax;
    private static int yMin;
    private static Bitmap edge_bitmap;
    private static int[] edge_pixels;
    private static int newColor;
    /**
     * bitmap为被画的原始图片
     * edge为确定范围的图片
     * x，y为初始变色坐标点
     * @param bitmap
     * @param edge
     * @param x
     * @param y
     * @return
     */
    public static Bitmap fillFieldWithColor(Bitmap bitmap, Bitmap edge, int x, int y, List<Point> pointList){
        edge_bitmap=edge;

        int pixel=edge.getPixel(x,y);
        if(pixel==BorderColor){
            return null;
        }
        newColor= Color.RED;

        // 获取这一区域的最大最小 x/y值
        xMax=FillColorUtils.getMaxX(pointList);
        xMin=FillColorUtils.getMinX(pointList);
        yMax=FillColorUtils.getMaxY(pointList);
        yMin=FillColorUtils.getMinY(pointList);
        // 获取这一区域的宽和高
        int width=xMax-xMin;
        int height=yMax-yMin;
        // 建立这一区域的像素点，并获取
        int[] pixels=new int[width*height];
        bitmap.getPixels(pixels,0,width,xMin,yMin,width,height);

        edge_pixels=new int[width*height];
        edge.getPixels(edge_pixels,0,width,xMin,yMin,width,height);

        fillColor(pixels,width,height,pixel,newColor,x,y);

        Bitmap temp=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());
        Paint paint=new Paint();
        Canvas canvas=new Canvas(temp);
        canvas.drawBitmap(bitmap,new Matrix(),paint);


        temp.setPixels(pixels,0,width,xMin,yMin,width,height);

        return temp;
    }

    /**
     * 修改指定区域的像素颜色值
     * @param pixels  该区域所有像素的颜色值
     * @param width   该区域的宽
     * @param height  该区域的高
     * @param pixel   初始点像素颜色
     * @param newColor 目标颜色
     * @param x        初始点位置x
     * @param y        初始点位置y
     */
    private static void fillColor(int[] pixels,int width,int height,int pixel,int newColor,int x,int y){
        mStacks.push(new Point(x-xMin,y-yMin));

        while(!mStacks.isEmpty()){
            Point seed=mStacks.pop();
            int count = fillLineLeft(pixels, pixel, width, height, newColor, seed.x, seed.y);
            int left = seed.x - count + 1;
            count = fillLineRight(pixels, pixel, width, height, newColor, seed.x + 1, seed.y);
            int right = seed.x + count;

            //从y+1找种子
            if (seed.y + 1 < height)
                findSeedInNewLine(pixels, pixel, width, height, seed.y + 1, left, right);
            if (seed.y - 1 >= 0)
                findSeedInNewLine(pixels, pixel, width, height, seed.y - 1, left, right);

        }
    }

    /**
     * 在新行找种子节点
     *
     * @param pixels
     * @param pixel
     * @param w
     * @param h
     * @param i
     * @param left
     * @param right
     */
    private static void findSeedInNewLine(int[] pixels, int pixel, int w, int h, int i, int left, int right)
    {
        /**
         * 获得该行的开始索引
         */
        int begin = i * w + left;
        /**
         * 获得该行的结束索引
         */
        int end = i * w + right;

        boolean hasSeed = false;

        int rx = -1, ry = -1;

        ry = i;

        /**
         * 从end到begin，找到种子节点入栈（AAABAAAB，则B前的A为种子节点）
         */
        while (end >= begin)
        {
//            if (pixels[end] == pixel)
            if(pixels[end]!=newColor && edge_pixels[end]!=BorderColor)
            {
                if (!hasSeed)
                {
                    rx = end % w;
                    mStacks.push(new Point(rx, ry));
                    hasSeed = true;
                }
            } else
            {
                hasSeed = false;
            }
            end--;
        }
    }


    /**
     * 往右填色，返回填充的个数
     *
     * @return
     */
    private static int fillLineRight(int[] pixels, int pixel, int w, int h, int newColor, int x, int y)
    {
        int count = 0;

        while (x < w)
        {
            //拿到索引
            int index = y * w + x;
            if (needFillPixel(pixels, pixel, index))
            {
                pixels[index] = newColor;
                count++;
                x++;
            } else
            {
                break;
            }

        }

        return count;
    }

    /**
     * 往左填色，返回填充的个数
     *
     * @return
     */
    private static int fillLineLeft(int[] pixels, int pixel, int w, int h, int newColor, int x, int y){
        int count = 0;
        while (x >= 0)
        {
            //计算出索引
            int index = y * w + x;

            if (needFillPixel(pixels, pixel, index))
            {
                pixels[index] = newColor;
                count++;
                x--;
            } else
            {
                break;
            }

        }
        return count;
    }

    /**
     * 判断该像素点是否需要改颜色
     * @param pixels
     * @param pixel
     * @param index
     * @return
     */
    private static boolean needFillPixel(int[] pixels, int pixel, int index)
    {
        if (hasBorderColor)
        {
            boolean boo=(edge_pixels[index] !=BorderColor);
            return boo;
//            return pixels[index] != BorderColor;
        } else
        {
            return pixels[index] == pixel;
        }
    }


    /**
     *
     * @param pointList
     * @return
     */
    private static int getMaxX(List<Point> pointList){
        List<Integer> xList=new ArrayList<Integer>();
        for(Point p:pointList){
            xList.add(p.x);
        }
        return Collections.max(xList);
    }

    private static int getMinX(List<Point> pointList){
        List<Integer> xList=new ArrayList<Integer>();
        for(Point p:pointList){
            xList.add(p.x);
        }
        return Collections.min(xList);
    }

    private static int getMaxY(List<Point> pointList){
        List<Integer> yList=new ArrayList<Integer>();
        for(Point p:pointList){
            yList.add(p.y);
        }
        return Collections.max(yList);
    }

    private static int getMinY(List<Point> pointList){
        List<Integer> yList=new ArrayList<Integer>();
        for(Point p:pointList){
            yList.add(p.y);
        }
        return Collections.min(yList);
    }

    /**
     * 千万别用
     */
//    private static void getPixels(Bitmap bitmap){
//        // 拿到整张图片的宽和高
//        int width=bitmap.getWidth();
//        int height=bitmap.getHeight();
//        // 定义一个数组，用来存储图片内每个像素点的颜色值（我觉得这样内存肯定爆炸）
//        int[] pixels=new int[width*height];
//        // 将所有点的颜色值，存储仅pixels数组内
//        // 第一个0 表示指定偏移位置，不需要就写0
//        // 第二个width表示每过width个像素点流换行
//        // 第三个第四个表示获取的起始点
//        // 第五个第六个表示获取区域的宽和高
//        bitmap.getPixels(pixels,0,width,0,0,width,height);
//    }

}
