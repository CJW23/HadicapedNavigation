package com.codetravel.mediarecorder;

import android.util.Log;

public class TimerRecoding implements Runnable {

    MainActivity ma;
    public TimerRecoding(MainActivity ma){
        this.ma=ma;
    }

    @Override
    public void run() {
        int i = 0;
        while(i<10){
            try {
                Log.d("gogo","yaya");
                if(ma.isRecording)
                    continue;
                ma.settingAndRunRecording();
                Thread.sleep(5000);
                ma.endRecording();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
