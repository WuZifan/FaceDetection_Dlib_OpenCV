package com.wzf.face.landmarks2.Activity;

import android.content.Intent;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.wzf.face.landmarks2.Model.LandMarks;
import com.wzf.face.landmarks2.Model.MyPixel;
import com.wzf.face.landmarks2.Native.NativeClass;
import com.wzf.face.landmarks2.R;
import com.wzf.face.landmarks2.Utils.FillColorUtils;
import com.wzf.face.landmarks2.Utils.ImageUtils;
import com.wzf.face.landmarks2.Utils.InfoUtils;
import com.wzf.face.landmarks2.Utils.OpencvUtils;
import com.wzf.face.landmarks2.View.GPSView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DetailActivity extends AppCompatActivity {
    private ImageView imageView;
    private GPSView gpsView;
    private  Bitmap bitmapInput,bitmapOutput;
    private Mat matInput,matOutput;
    private Bitmap original_bitmap;
    private static final int MAXIMUM_FACE=10;
    private static FaceDetector.Face[] faces=new FaceDetector.Face[MAXIMUM_FACE];
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int faces_num=(int)msg.obj;
            imageView.setImageBitmap(ImageUtils.drawCircleOnFace(faces_num,original_bitmap,faces));
            super.handleMessage(msg);
        }
    };

    // 加载静态库
    static{
        System.loadLibrary("MyLibs");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 得到控件
        imageView=(ImageView)findViewById(R.id.iv_detail);
        gpsView=(GPSView)findViewById(R.id.GPS_detail);

        // 从load界面得到的图片
        Intent intent=this.getIntent();
        Uri uri=(Uri)intent.getParcelableExtra("image");
        original_bitmap=ImageUtils.scaleToAndroidImage(uri,DetailActivity.this);

        // 显示图片
        imageView.setImageBitmap(original_bitmap);
    }

    /**
     * 利用google原生API进行检测
     * @param view
     */
    public void click_BaseDetec(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 1. 得到人脸检测者
                FaceDetector faceDetector = new FaceDetector(original_bitmap.getWidth(), original_bitmap.getHeight(), MAXIMUM_FACE);
                // 2. 得到需要检测对象的备份，设置编码为565
                Bitmap detectImage = ImageUtils.bitmapToRGB565(original_bitmap);
                // 3. 查找人脸
                // 4. 猜测这应该是一个耗时的操作，被放在一个新线程中执行
                // 4.1 不然后面因为部分没有处理完，就进行下一步动作，产生会空指针
                int face_Count = faceDetector.findFaces(detectImage, faces);
                // 4. 发送查找到多少张人脸
                Message message = Message.obtain();
                message.obj = face_Count;
                handler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 利用google-play-service进行检测
     * @param view
     */
    public void click_PlayServiceDetec(View view){
        this.gpsView.setBitmap(original_bitmap);
    }

    /**
     * 为了opencv的人脸检测对图片进行相关处理
     */
    private void init4Detec(){
        bitmapInput=ImageUtils.bitmapToARGB888(original_bitmap);
        // 创建matInput和matOutput两个对象
        matInput=ImageUtils.convertBitmap2Mat(bitmapInput);
        matOutput=new Mat(matInput.rows(),matInput.cols(), CvType.CV_8UC3);
    }

    /**
     * 利用opencv+dib来检测面部以及五官
     * @param view
     */
    public void click_detail(View view){
        init4Detec();
        boolean boo= OpenCVLoader.initDebug();
        if(boo){
            Toast.makeText(DetailActivity.this,"Calling native function",Toast.LENGTH_SHORT).show();
            NativeClass.LandmarkDetection(matInput.getNativeObjAddr(),matOutput.getNativeObjAddr());
            Toast.makeText(DetailActivity.this,"Calling native function222",Toast.LENGTH_SHORT).show();
            bitmapOutput=ImageUtils.convertMat2Bitmap(matOutput);
            imageView.setImageBitmap(bitmapOutput);
        }else{
            Toast.makeText(DetailActivity.this,"失败",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 实现得到坐标点的办法
     * @param view
     */
    public void click_getLocation(View view){
        // 初始化图片
        init4Detec();
        boolean boo= OpenCVLoader.initDebug();
        if(boo){
            // 进行检测，得到所有人脸的landmarks信息
            int[] landmarks=NativeClass.LandMarkDetection_Array(matInput.getNativeObjAddr(),matOutput.getNativeObjAddr());

            // 用来保存不同人脸的信息
            List<LandMarks> landMarksList=new ArrayList<LandMarks>();

            // 每个人的landmarks信息单独存储在一个Landmarks对象里面
            for(int i=0;i<landmarks.length/(2*68);i++){
                List<Integer> xlist=new ArrayList<Integer>();
                List<Integer> ylist=new ArrayList<Integer>();
                for(int j=0;j<2*68;j++){
                    if(j%2==0){
                        xlist.add(landmarks[i*2*68+j]);
                    }else{
                        ylist.add(landmarks[i*2*68+j]);
                    }
                }
                landMarksList.add(new LandMarks(xlist,ylist));
            }

            for(LandMarks lm:landMarksList) {
//                landMarkTest(lm);
//                // 在与原图等大小的图片上，画出指定的轮廓
//                Bitmap edgeBitmap=ImageUtils.drawEdge(this.original_bitmap,lm.getUnderLips(),false);
//                // 选择66和57的中点作为填色初始点
//                Point point_57=lm.getPoint(57);
//                Point point_66=lm.getPoint(66);
//                int init_x=(point_57.x+point_66.x)/2;
//                int init_y=((point_57.y+point_66.y)/2);
//                Bitmap fill_bitmap=FillColorUtils.fillFieldWithColor(this.original_bitmap,edgeBitmap,init_x,init_y,lm.getUnderLips());
//
//                Bitmap final_bitmap=ImageUtils.drawEdge(fill_bitmap,lm.getUnderLips(),true);
//                imageView.setImageBitmap(drawUnderLip(lm));
                Bitmap underLip=drawUnderLip(lm);

                Bitmap edgeUpperLip=ImageUtils.drawEdge(underLip,lm.getUpperLips(),false);
                Point point_51=lm.getPoint(51);
                Point point_62=lm.getPoint(62);
                int init_x=(point_51.x+point_62.x)/2;
                int init_y=(point_51.y+point_62.y)/2;
                Bitmap fill_bitmap2=FillColorUtils.fillFieldWithColor(underLip,edgeUpperLip,init_x,init_y,lm.getUpperLips());

                imageView.setImageBitmap(fill_bitmap2);
            }
        }else{
            Toast.makeText(DetailActivity.this,"失败",Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap drawUnderLip(LandMarks lm){
        // 在与原图等大小的图片上，画出指定的轮廓
        Bitmap edgeBitmap=ImageUtils.drawEdge(this.original_bitmap,lm.getUnderLips(),false);
        // 选择66和57的中点作为填色初始点
        Point point_57=lm.getPoint(57);
        Point point_66=lm.getPoint(66);
        int init_x=(point_57.x+point_66.x)/2;
        int init_y=((point_57.y+point_66.y)/2);
        Bitmap fill_bitmap=FillColorUtils.fillFieldWithColor(this.original_bitmap,edgeBitmap,init_x,init_y,lm.getUnderLips());

        Bitmap final_bitmap=ImageUtils.drawEdge(fill_bitmap,lm.getUnderLips(),true);
        return fill_bitmap;
    }

    /**
     * 楼上方法的中最下面for循环的测试代码
     * @param lm
     */
    private void landMarkTest(LandMarks lm){
        int xMax = lm.getMaxX();
        int xMin = lm.getMinX();
        int yMax = lm.getMaxY();
        int yMin = lm.getMinY();

        // 这里证明了，新剪切出来的图像，其左上角坐标是也是（0.0)
        Bitmap copy2=Bitmap.createBitmap(original_bitmap,xMin,yMin,xMax-xMin,yMax-yMin);

        // 从人脸上去一块矩形，用来判断肤色
        // 初始点用1的y，3的x会比较好,这个是用来判断肤色的
        Point point_1 = lm.getPoint(1);
        Point point_3 = lm.getPoint(3);
        Point point_31 = lm.getPoint(31);
        // 149 188 181 149, 218 188
        // InfoUtils.showInfo(point_1.x+" "+point_1.y+" "+point_31.x+" "+point_1.x+" "+point_3.y+" "+point_1.y,DetailActivity.this);
        // 矩形的宽和高
        int point_width=point_31.x-point_1.x;
        int point_height=point_3.y-point_1.y;
        // 获得平均像素值
        MyPixel targetPixel=ImageUtils.averageROI(original_bitmap,point_3.x,point_1.y,point_width,point_height);

        InfoUtils.showInfo(""+targetPixel,DetailActivity.this);

        Bitmap copy_show=Bitmap.createBitmap(copy2.getWidth(),copy2.getHeight(),copy2.getConfig());
        Paint paint3=new Paint();
        Canvas canvas3=new Canvas(copy_show);
        canvas3.drawBitmap(copy2,new Matrix(),paint3);
        paint3.setColor(0xFFFF0000);
        canvas3.drawLine(point_3.x-xMin,point_1.y-yMin,point_3.x-xMin+point_width,point_1.y-yMin,paint3);
        paint3.setColor(0x00FF0000);
        canvas3.drawLine(point_3.x-xMin,point_1.y-yMin,point_3.x-xMin,point_1.y-yMin+point_height,paint3);
        MyPixel mptest=new MyPixel(copy_show.getPixel(point_3.x-xMin+1,point_1.y-yMin));

        InfoUtils.showInfo("lalala"+mptest.toString(),DetailActivity.this);
        imageView.setImageBitmap(copy_show);
    }

}
