package com.codetravel.mediarecorder;


import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pedro.rtplibrary.rtsp.RtspCamera1;
import com.pedro.rtsp.utils.ConnectCheckerRtsp;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, ConnectCheckerRtsp {

    public static RtspCamera1 rtspCamera1;
    private SurfaceHolder mHolder;


    public CameraPreview(Context context) {
        super(context);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        rtspCamera1 = new RtspCamera1(this, this);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        rtspCamera1.startPreview();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        rtspCamera1.stopPreview();
    }

    @Override
    public void onConnectionSuccessRtsp() {

    }

    @Override
    public void onConnectionFailedRtsp(String s) {

    }

    @Override
    public void onDisconnectRtsp() {

    }

    @Override
    public void onAuthErrorRtsp() {

    }

    @Override
    public void onAuthSuccessRtsp() {

    }


}




