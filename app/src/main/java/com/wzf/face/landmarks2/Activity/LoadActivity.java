package com.wzf.face.landmarks2.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wzf.face.landmarks2.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by qq on 2017/6/17.
 */

public class LoadActivity extends AppCompatActivity {

    private static final int LOAD_IMAGE_PHONE=0;
    private static final int LOAD_IMAGE_CARMER=1;
    // 用来创建保存相片的文件夹
    private File file;
    // 用来临时指向拍摄的照片
    private File cameraImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        // 创建文件夹，用于存放拍摄的照片
        this.file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GraduateProject");
        if (!file.exists()) {
           file.mkdirs();
        }
    }

    /**
     * 从手机加载内容
     * @param view
     */
    public void click_phone(View view){
        // 2. 调用隐式意图，跳转到gallery界面
        Intent intent = new Intent();
        // 3. 设置action和MimeType
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 4. 跳转到选照片页面页面
        startActivityForResult(intent, LOAD_IMAGE_PHONE);
    }

    /**
     * 从相机中拍摄内容
     * @param view
     */
    public void click_camera(View view){
        try {
            // 2. 调用隐式意图，跳转到照相机页面
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            // 4. 创建文件存储照片
            cameraImage = new File(file, System.currentTimeMillis() + ".png");
            boolean foo=cameraImage.createNewFile();
            System.out.println("create file: "+ foo);
            System.out.println(cameraImage.getAbsolutePath());
            // 4. 设置传递信息,第一个参数会告诉相机将拍摄到的内容怎么办，
            // 4.1 此时是告诉相机，将拍摄到的内容存储起来
            // 4.2 存储路径就是第二个参数
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraImage));
            // 5. 开启activity
            startActivityForResult(intent, LOAD_IMAGE_CARMER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拿到返回结果后怎么办
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case LOAD_IMAGE_PHONE:
                loadImagePhone(data);
                break;
            case LOAD_IMAGE_CARMER:
                loadImageCarmer(data);
                break;
            default:{
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * 将手机图库中的图片传递到新activity中显示
     *
     * @param data
     */
    private void loadImagePhone(Intent data) {
        if (data != null) {
            // 6. 返回的是图片的路径
            Uri uri = data.getData();
            // 7. 跳转到新的页面
            toFaceActivity(uri);
        }
    }

    /**
     * 将拍照得到的图片传递到新activity中显示
     * @param data
     */
    private void loadImageCarmer(Intent data) {
        //1. 发送广播，让图库加载sd卡图片
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(cameraImage));
        sendBroadcast(intent);
        //2. 得到图片的uri
        Uri uri=Uri.fromFile(cameraImage);
        // 3. 在新界面中显示
        toFaceActivity(uri);
    }

    /**
     * 跳转到Face页面
     */
    private void toFaceActivity(Uri uri){
        //1. 设置要跳转的页面
        Intent intent=new Intent(getApplicationContext(), DetailActivity.class);
        //2. 配置需要传递的数据
        // 3. 利用putExtra中接受序列化Parcelable参数的方法
        intent.putExtra("image", uri);
        startActivity(intent);
    }
}
