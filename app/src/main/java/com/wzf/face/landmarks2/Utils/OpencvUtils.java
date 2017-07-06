package com.wzf.face.landmarks2.Utils;

import android.graphics.Bitmap;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by qq on 2017/6/19.
 */

public class OpencvUtils {

    /**
     * 转换为灰度图
     * @param bitmap
     * @return
     */
    public static Bitmap toGray(Bitmap bitmap){
        Mat mat=ImageUtils.convertBitmap2Mat(bitmap);
        Mat gray=new Mat();
        Imgproc.cvtColor(mat,gray,Imgproc.COLOR_BGRA2GRAY);
        return ImageUtils.convertMat2Bitmap(gray);
    }

    /**
     * 边缘检测，但不是很好用= -
     * @param bitmap
     * @return
     */
    public static Bitmap edgeDetec(Bitmap bitmap){
        bitmap=toGray(bitmap);
        Mat mat=ImageUtils.convertBitmap2Mat(bitmap);
        Mat edge=new Mat();
        Imgproc.Canny(mat,edge,3,9);
        return ImageUtils.convertMat2Bitmap(edge);
    }


}
