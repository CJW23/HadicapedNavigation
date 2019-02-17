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
                Thread.sleep(5000);
                Log.d("gogo","yaya");
                if(ma.isRecording)
                    continue;
                ma.settingAndRunRecording();
                Thread.sleep(4000);
                ma.settingAndRunRecording();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
