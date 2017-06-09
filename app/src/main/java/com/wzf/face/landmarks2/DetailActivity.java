package com.wzf.face.landmarks2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class DetailActivity extends AppCompatActivity {
    ImageView imageView;
    Button btnProceess;
    Bitmap bitmapInput,bitmapOutput;
    Mat matInput,matOutput;

    static{
        System.loadLibrary("MyLibs");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView=(ImageView)findViewById(R.id.iv_detail);
        btnProceess=(Button)findViewById(R.id.btn_detail);

        String photoPath= Environment.getExternalStorageDirectory()+"/frames/frame.png";

        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig=Bitmap.Config.ARGB_8888;
        bitmapInput=BitmapFactory.decodeFile(photoPath,options);

        imageView.setImageBitmap(bitmapInput);

        matInput=convertBitmap2Mat(bitmapInput);
        matOutput=new Mat(matInput.rows(),matInput.cols(), CvType.CV_8UC3);

        btnProceess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean boo= OpenCVLoader.initDebug();
                if(boo){
                Toast.makeText(DetailActivity.this,"Calling native function",Toast.LENGTH_SHORT).show();
                NativeClass.LandmarkDetection(matInput.getNativeObjAddr(),matOutput.getNativeObjAddr());
                Toast.makeText(DetailActivity.this,"Calling native function222",Toast.LENGTH_SHORT).show();
                bitmapOutput=convertMat2Bitmap(matOutput);
                imageView.setImageBitmap(bitmapOutput);
                }else{
                    Toast.makeText(DetailActivity.this,"失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    // 按钮点击事件 在非mainActivity，不能用这种方式设置点击事件
    public void click_detail(View view){}

    // Mat转换成Bitmap
    Bitmap convertMat2Bitmap(Mat img){
        int width=img.width();
        int height=img.height();

        Bitmap bmp;
        bmp=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Mat tmp;
        tmp=img.channels()==1?new Mat(width,height,CvType.CV_8UC1,new Scalar(1)):new Mat(width,height,CvType.CV_8UC3,new Scalar(3));
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
    Mat convertBitmap2Mat(Bitmap rgbaImg){
        Mat rgbaMat=new Mat(rgbaImg.getHeight(),rgbaImg.getWidth(),CvType.CV_8UC4);
        Bitmap bmp32=rgbaImg.copy(Bitmap.Config.ARGB_8888,true);
        Utils.bitmapToMat(bmp32,rgbaMat);

        Mat rgbNewMat=new Mat(rgbaImg.getHeight(),rgbaImg.getWidth(),CvType.CV_8UC3);
        Imgproc.cvtColor(rgbaMat,rgbNewMat, Imgproc.COLOR_RGB2BGR,3);
        return rgbNewMat;
    }
}
