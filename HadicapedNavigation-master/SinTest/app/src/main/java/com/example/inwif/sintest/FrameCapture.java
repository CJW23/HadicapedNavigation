package com.example.inwif.sintest;

public class FrameCapture implements Runnable {

    CameraPreview Cp;
    private VoiceGuide vg;
    int i=0;
    public FrameCapture(CameraPreview Cp) {
        this.Cp = Cp;
        this.vg = new VoiceGuide(this.Cp.getMaincontext());
    }

    @Override
    public void run() {

        while(i<10){
            try {
                Thread.sleep(5000);
                Cp.takePicture();
                i++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
