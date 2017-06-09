package com.wzf.face.landmarks2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    public static final String TAG="LMD";
    JavaCameraView javaCameraView;
    Mat mRGBA;
    BaseLoaderCallback baseLoaderCallback=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:{
                    javaCameraView.enableView();
                    break;
                }
                default:{
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };
    static{
        System.loadLibrary("MyLibs");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        javaCameraView=(JavaCameraView)findViewById(R.id.javaCVLMD);
        javaCameraView.setVisibility(View.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(javaCameraView!=null){
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(javaCameraView!=null){
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(OpenCVLoader.initDebug()){
            Log.i(TAG,"成功");
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }else{
            Log.i(TAG,"失败");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,this,baseLoaderCallback);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBA=new Mat(height,width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRGBA.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGBA=inputFrame.rgba();
        return mRGBA;
    }

    public void click1(View view){
        saveImage(mRGBA);
        Intent intent=new Intent(this,DetailActivity.class);
        startActivity(intent);
    }

    public void saveImage(Mat subImg){
        Bitmap bmp=null;

        try{
            bmp= Bitmap.createBitmap(subImg.cols(),subImg.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(subImg,bmp);
        }catch (Exception e){
            Log.d(TAG,e.getMessage());
        }

        subImg.release();

        FileOutputStream out=null;

        String fileName="frame.png";

        File sd=new File(Environment.getExternalStorageDirectory()+"/frames");
        boolean success=true;
        if(!sd.exists()){
            success=sd.mkdir();
        }
        if(success){
            File dest=new File(sd,fileName);
            try{
                out=new FileOutputStream(dest);
                bmp.compress(Bitmap.CompressFormat.PNG,100,out);
            }catch (Exception e){
                Log.d(TAG,e.getMessage());
            }finally {
                try{
                    if(out!=null){
                        out.close();
                        Log.d(TAG,"成功赋值");
                    }
                }catch (Exception e){
                    Log.d(TAG,e.getMessage());
                }
            }
        }
    }
}
