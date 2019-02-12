package com.codetravel.mediarecorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.media.CamcorderProfile;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, ActivityCompat.OnRequestPermissionsResultCallback {
    private final static String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    private static final int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;
    private MediaRecorder mRecorder = null;
    private String mPath = null;
    private MediaPlayer mPlayer = null;
    private CamcorderProfile profile;
    boolean isRecording = false;
    boolean isPlaying = false;
    boolean hasVideo = false;

    Button mBtPlay = null;
    Button mBtCamcording = null;

    SurfaceView mSurface = null;
    SurfaceHolder mSurfaceHolder = null;
    private Runnable startCamrecoding;
    private Thread startCamThread;

    private PredictCommunication pc;
    private Thread startCommunication;

    Camera mCamera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.mp4";        //동영상 경로
        accessPermission();     //Permission 때려박은 함수
        mRecorder = new MediaRecorder();

        mBtCamcording = (Button)findViewById(R.id.bt_camcording);
        mBtCamcording.setOnClickListener(new View.OnClickListener(){        //동영상 촬영 버튼
            @Override
            public void onClick(View v) {
                timerRecoding();
            }
        });

        mSurface = (SurfaceView)findViewById(R.id.sv);



    }

    void settingAndRunRecording(){
        hasVideo = true;
        initVideoRecorder();
        startVideoRecorder();
    }

    void timerRecoding(){
        startCamrecoding = new TimerRecoding(this);
        startCamThread = new Thread(startCamrecoding);

        startCamThread.start();
    }

    void startVideoRecorder() {     //녹화 실행 함수
        if(isRecording) {           //녹화를 끝내면 실행 -> 파일 전송 (isRecording = true);
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

            mCamera.lock();
            isRecording = false;
            mBtCamcording.setText("Start Camcording");
            uploadVideo();      //동영상 전송
        }
        else { //(isRecording = false)
            runOnUiThread(new Runnable() {      //동영상 촬영 하면 실행
                @Override
                public void run() {
                    mRecorder = new MediaRecorder();
                    mCamera.unlock();
                    mRecorder.setCamera(mCamera);
                    mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                    //mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
                   // mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                   // mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
                    mRecorder.setOrientationHint(90);
                    mRecorder.setProfile(profile);


                    mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + String.format("/%d_record.mp4", System.currentTimeMillis());
                    Log.d(TAG, "file path is " + mPath);
                    mRecorder.setOutputFile(mPath);

                    mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
                    try {
                        mRecorder.prepare();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    mRecorder.start();
                    isRecording = true;

                    mBtCamcording.setText("Stop Camcording");
                }
            });
        }
    }
    private void uploadVideo() {        //동영상 업로드 함수 -> 파일 경로(mPath) Upload클래스에 보내면 알아서 서버로 보냄 안봐도 됨
        class UploadVideo extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                File delFile = new File(mPath);
                delFile.delete();
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(mPath);  ///////////////////////경로 보내는 곳
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();       //실행
    }

    void initVideoRecorder() {
        mCamera = Camera.open();

        mCamera.setDisplayOrientation(90);

        mSurfaceHolder = mSurface.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

        if(mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (mCamera == null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        if ( requestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if ( check_result ) {
                finish();
            }
            else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[2])) {

                    Snackbar.make(mSurface, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                        }
                    }).show();

                }else {

                    Snackbar.make(mSurface, "설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    public void accessPermission(){
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int AudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);


            if ( cameraPermission == PackageManager.PERMISSION_GRANTED
                    && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED
                    && AudioPermission == PackageManager.PERMISSION_GRANTED) {

            }else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[2])) {

                    Snackbar.make(mSurface, "이 앱을 실행하려면 카메라와 외부 저장소 접근 권한이 필요합니다.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            ActivityCompat.requestPermissions( MainActivity.this, REQUIRED_PERMISSIONS,
                                    PERMISSIONS_REQUEST_CODE);
                        }
                    }).show();


                } else {
                    // 2. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                    // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                    ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE);
                }

            }

        } else {

            final Snackbar snackbar = Snackbar.make(mSurface, "디바이스가 카메라를 지원하지 않습니다.",
                    Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("확인", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }

    }

    public void setPrdtCmn(){   //예측값 소켓 통신 쓰레드
        pc = new PredictCommunication(this);
        startCommunication = new Thread(pc);
        startCommunication.start();
    }
}