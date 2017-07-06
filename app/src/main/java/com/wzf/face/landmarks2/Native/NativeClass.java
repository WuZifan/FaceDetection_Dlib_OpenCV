package com.wzf.face.landmarks2.Native;

/**
 * Created by qq on 2017/6/7.
 */

public class NativeClass {
    public native static void LandmarkDetection(long addrInput,long addrOutput);
    // 每次加完东西，记得build一下，让新加入的内容，被更新到对应的class文件中去
    public native static int[] LandMarkDetection_Array(long addrInput,long addrOutput);
}
