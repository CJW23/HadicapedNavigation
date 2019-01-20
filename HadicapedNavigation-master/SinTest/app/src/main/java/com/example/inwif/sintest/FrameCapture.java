package com.example.inwif.sintest;

public class FrameCapture implements Runnable {

    CameraPreview Cp;
    public FrameCapture(CameraPreview Cp) {
        this.Cp = Cp;
    }

    @Override
    public void run() {
        int i=0;
        while(i<10){
            try {
                Thread.sleep(5000);
                Cp.takePicture();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
