package com.wzf.face.landmarks2.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;

import com.wzf.face.landmarks2.Model.MyPixel;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq on 2017/6/17.
 */

public class ImageUtils {
    /**
     * 拿到图片的副本，以供修改
     *
     * @return
     */
    public static Bitmap copyOfImage(Bitmap bitmap) {
        // 1.拿到和原图一样分辨率,设置一样的白纸
        // 1.1 由于人脸识别要求图片宽度必须为偶数，这里判断一下
        Bitmap copyImage;
        if (bitmap.getWidth() % 2 == 0) {
            copyImage = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        }else{
            copyImage = Bitmap.createBitmap(bitmap.getWidth()+1, bitmap.getHeight(), bitmap.getConfig());
        }
        // 2. 得到画笔
        Paint paint = new Paint();
        // 3. 得到画布,并把白纸添加进来
        Canvas canvas = new Canvas(copyImage);
        // 4.利用画布开始作画
        // 4.1 第一个参数表示画画时参考的对象
        // 4.2 第二个参数设置一些对称，平移，旋转等效果
        // 4.3 paint为画笔
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        return copyImage;
    }

    /**
     * 将图片进行缩放，使其能够匹配手机屏幕大小
     *
     * @param uri
     * @param activity
     * @return
     */
    @SuppressWarnings({ "deprecation", "unused" })
    public static Bitmap scaleToAndroidImage(Uri uri, Activity activity) {
        // uri通过getPath拿到的路径不是绝对路径，无法读取
        InputStream iStream;
        try {
            // -2. 通过activity调用内容解析者，通过uri拿到输入流
            iStream = activity.getContentResolver().openInputStream(uri);
            // -1. 将InputStream强转为FileInputStream
            FileInputStream fileInputStream = (FileInputStream) iStream;
            // 0. 利用FileInputStream调用getFD方法，得到FileDescriptor方法。
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            // 1. 创建options对象
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 2. 设置opts属性，进行伪加载
            opts.inJustDecodeBounds = true;
            // 3. 调用bitmapfactory,加载FileDescriptor方法得到bitmap对象
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);
            // 4. 得到图片的分辨率
            int image_width = opts.outWidth;
            int image_height = opts.outHeight;
            // 5. 计算缩放比例
            int scale = scaleCalu(activity, image_width, image_height);
            // 7. 设置缩放比例
            opts.inSampleSize = scale;
            // 8. 设置为真加载
            opts.inJustDecodeBounds = false;
            // bitmap = BitmapFactory.decodeStream(iStream, null, opts);
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);
            iStream.close();
            return bitmap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将图片进行缩放，使其能够匹配手机自定义大小
     *
     * @param uri
     * @param activity
     * @return
     */
    @SuppressWarnings({ "deprecation", "unused" })
    public static Bitmap scaleToAndroidImage(Uri uri, Activity activity, int diy_width, int diy_height) {
        // uri通过getPath拿到的路径不是绝对路径，无法读取
        InputStream iStream;
        try {
            // -2. 通过activity调用内容解析者，通过uri拿到输入流
            iStream = activity.getContentResolver().openInputStream(uri);
            // -1. 将InputStream强转为FileInputStream
            FileInputStream fileInputStream = (FileInputStream) iStream;
            // 0. 利用FileInputStream调用getFD方法，得到FileDescriptor方法。
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            // 1. 创建options对象
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 2. 设置opts属性，进行伪加载
            opts.inJustDecodeBounds = true;
            // 3. 调用bitmapfactory,加载FileDescriptor方法得到bitmap对象
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);
            // 4. 得到图片的分辨率
            int image_width = opts.outWidth;
            int image_height = opts.outHeight;
            // 5. 计算缩放比例
            int scale = scale(diy_width, diy_height, image_width, image_height);
            // 7. 设置缩放比例
            opts.inSampleSize = scale;
            // 8. 设置为真加载
            opts.inJustDecodeBounds = false;
            // bitmap = BitmapFactory.decodeStream(iStream, null, opts);
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);
            iStream.close();
            return bitmap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算图片的缩放大小，默认以手机屏幕为标准缩放
     *
     * @param activity
     * @param image_width
     * @param image_height
     * @return
     */
    @SuppressWarnings("deprecation")
    private static int scaleCalu(Activity activity, int image_width, int image_height) {
        // 5.1 得到windowManager对象
        WindowManager wManager = activity.getWindowManager();
        int phone_width = wManager.getDefaultDisplay().getWidth();
        int phone_height = wManager.getDefaultDisplay().getHeight();
        // 6. 计算缩放比例,并返回
        return scale(phone_width, phone_height, image_width, image_height);
    }

    /**
     * 通过四个参数计算缩放比例
     * @param org_width
     * @param org_height
     * @param image_width
     * @param image_height
     * @return
     */
    private static int scale(int org_width, int org_height, int image_width, int image_height) {
        // 6. 计算缩放比例
        int scale = 1;
        int scale_width = image_width / org_width;
        int scale_height = image_height / org_height;
        if (scale_width >= scale_height && scale_width > 1) {
            scale = scale_width;
        } else if (scale_height > scale_width && scale_height > 1) {
            scale = scale_height;
        }
        return scale;
    }

    // Mat转换成Bitmap
    public static Bitmap convertMat2Bitmap(Mat img){
        int width=img.width();
        int height=img.height();
        Bitmap bmp;
        bmp=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Mat tmp;
        tmp=img.channels()==1?new Mat(width,height, CvType.CV_8UC1,new Scalar(1)):new Mat(width,height,CvType.CV_8UC3,new Scalar(3));
        try{
            if(img.channels()==3){
                Imgproc.cvtColor(img,tmp,Imgproc.COLOR_RGB2BGRA);
            }else if(img.channels()==1){
                Imgproc.cvtColor(img,tmp,Imgproc.COLOR_GRAY2BGRA);
            }
            Utils.matToBitmap(tmp,bmp);
        }catch(Exception e){
            Log.d("Exception",e.getMessage());
        }
        return bmp;
    }

    //bitmap转换成Mat
    public static Mat convertBitmap2Mat(Bitmap rgbaImg){
        Mat rgbaMat=new Mat(rgbaImg.getHeight(),rgbaImg.getWidth(),CvType.CV_8UC4);
        Bitmap bmp32=rgbaImg.copy(Bitmap.Config.ARGB_8888,true);
        Utils.bitmapToMat(bmp32,rgbaMat);

        Mat rgbNewMat=new Mat(rgbaImg.getHeight(),rgbaImg.getWidth(),CvType.CV_8UC3);
        Imgproc.cvtColor(rgbaMat,rgbNewMat, Imgproc.COLOR_RGB2BGR,3);
        return rgbNewMat;
    }

    //将Bitmap转换为ARGB888格式
    public static Bitmap bitmapToARGB888(Bitmap bitmap){
        return bitmap.copy(Bitmap.Config.ARGB_8888,true);
    }

    //将Bitmap转换为RGB565格式

    public  static Bitmap bitmapToRGB565(Bitmap bitmap){
        return bitmap.copy(Bitmap.Config.RGB_565, true);
    }

    /**
     * 在人脸周围画圈
     * @param face_Count
     */
    public static Bitmap drawCircleOnFace(int face_Count, Bitmap original_bitmap, FaceDetector.Face[] faces) {
        // 0. 得到图片的副本
        Bitmap copyBitmap = ImageUtils.copyOfImage(original_bitmap);
        if (face_Count > 0) {
            for (int i = 0; i < face_Count; i++) {
                FaceDetector.Face face = faces[i];
                PointF pointF = new PointF();
                // 5. 拿到眉心位置
                face.getMidPoint(pointF);
                int x = (int) pointF.x;
                int y = (int) pointF.y;
                // 6. 拿到眼间距
                int eyeDistance = (int) face.eyesDistance();
                int radio = eyeDistance * 9 / 5;
                for (int xc = -radio; xc <= radio; xc++) {
                    for (int yc = -radio; yc <= radio; yc++) {
                        if (Math.sqrt(xc * xc + yc * yc) <= radio && Math.sqrt(xc * xc + yc * yc) >= 0.95 * radio) {
                            try {
                                copyBitmap.setPixel(x + xc, y + yc, Color.RED);
                            } catch (Exception exception) {
                                System.out.println("有时候会画画越界");
                            }
                        }
                    }
                }
            }
            return copyBitmap;
        }else{
            return null;
        }
    }

    /**
     * 在原图中截取一个矩形窗口，并返回
     * @param xMin
     * @param yMin
     * @param xMax
     * @param yMax
     * @param bitmap
     * @return
     */
    public static Bitmap cutBitmap(int xMin,int yMin,int xMax,int yMax,Bitmap bitmap){
        Bitmap copy_bitmap=Bitmap.createBitmap(bitmap,xMin,yMin,xMax-xMin,yMax-yMin);
        Paint paint=new Paint();
        Canvas canvas=new Canvas(copy_bitmap);
        canvas.drawBitmap(bitmap,new Matrix(),paint);
        return copy_bitmap;
    }

    /**
     * 计算bitmap中，以想x,y为起始点，width和height为高和宽的矩形内的平均像素值
     * @param bitmap
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public static MyPixel averageROI(Bitmap bitmap,int x,int y,int width,int height){
        List<MyPixel> myPixels=new ArrayList<MyPixel>();
        for(int i=x;i<x+width;i++){
            for(int j=y;j<y+height;j++){
                MyPixel temp=new MyPixel(bitmap.getPixel(i,j));
                myPixels.add(temp);
            }
        }
        int temp_R=0;
        int temp_G=0;
        int temp_B=0;
        int temp_sumR=0;
        int temp_sumG=0;
        int temp_sumB=0;
        for(MyPixel mp:myPixels){
            temp_sumR+=mp.getR();
            temp_sumG+=mp.getG();
            temp_sumB+=mp.getB();
        }
        temp_R=temp_sumR/myPixels.size();
        temp_G=temp_sumG/myPixels.size();
        temp_B=temp_sumB/myPixels.size();
        return new MyPixel(temp_R,temp_G,temp_B);
    }

    /**
     * 以BLACK绘制纯黑色边界
     * @param bitmap
     * @param points
     * @return
     */
    public static Bitmap drawEdge(Bitmap bitmap, List<Point> points,boolean flag){
        Bitmap temp_bitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());
        Paint paint=new Paint();
        Canvas canvas=new Canvas(temp_bitmap);
        if(flag){
            canvas.drawBitmap(bitmap,new Matrix(),paint);
        }
        paint.setColor(Color.BLACK);
        for(int i=0;i<points.size();i++){
            // 不是最后一点
            if(i!=points.size()-1){
                Point start=points.get(i);
                Point end=points.get(i+1);
                canvas.drawLine(start.x,start.y,end.x,end.y,paint);
            }else{
                // 是最后一点
                Point start=points.get(i);
                Point end=points.get(0);
                canvas.drawLine(start.x,start.y,end.x,end.y,paint);
            }
        }
        return temp_bitmap;
    }

}
