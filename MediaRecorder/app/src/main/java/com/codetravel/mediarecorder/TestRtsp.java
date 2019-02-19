package com.codetravel.mediarecorder;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.pedro.rtplibrary.rtsp.RtspCamera1;
import com.pedro.rtsp.rtsp.Protocol;

import java.util.ArrayList;

import butterknife.BindView;

import static com.codetravel.mediarecorder.CameraPreview.rtspCamera1;

public class TestRtsp extends AppCompatActivity {

    private CameraPreview preview;
    private int RECORD = 0;


    public static String url = "rtsp://e4f15c.entrypoint.cloud.wowza.com/app-dc3f/bcaafa3d";
    public static int samplerate = 44100;
    public static int fps = 30;

    public static int bitrate = 400000;
    public static int width = 640;
    public static int height = 480;
    FrameLayout cameralayout;
    Button finishBtn;

   /* @BindView(R.id.sview)   // surfaceview
            FrameLayout cameralayout;
    @BindView(R.id.finish1)   // 카메라 전환 버튼
            Button finishBtn;*/

    Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_rtsp);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);    // 화면꺼짐 방지

        cameralayout = (FrameLayout) findViewById(R.id.sview);

    /*    finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopStream();
                finish();
            }
        });*/


        initView();
        camera = Camera.open();
        startOrstopStream();
    }

    // 기본 세팅
    private void initView() {
        preview = new CameraPreview(this);
        cameralayout.addView(preview);
    }

    private void startOrstopStream() {
        if (RECORD != 0) {
            RECORD--;
        } else {
            connectRtsp(url);
        }
    }

    public void successCallback() {
        RECORD++;
    }

    // 스트리밍 서버에 연결
    public void connectRtsp(String url) {

        rtspCamera1.setVideoBitrateOnFly(bitrate);
        rtspCamera1.setProtocol(Protocol.UDP);
        rtspCamera1.prepareAudio(128 * 1024, samplerate, true, false, false);
        rtspCamera1.prepareVideo(width, height, fps, bitrate, false, 0);
        rtspCamera1.getResolutionsBack().get(8);

        rtspCamera1.enableVideo();
        rtspCamera1.enableAudio();
        rtspCamera1.startStream(url);

    }

    // 스트리밍 종료
    public void stopStream() {
        try {
            rtspCamera1.disableVideo();
            rtspCamera1.disableAudio();
            rtspCamera1.stopStream();
        } catch (Exception e) {

        }
    }
}
