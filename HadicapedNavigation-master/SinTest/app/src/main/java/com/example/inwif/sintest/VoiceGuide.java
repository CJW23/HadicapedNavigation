package com.example.inwif.sintest;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
import static android.speech.tts.TextToSpeech.ERROR;

public class VoiceGuide {
    private TextToSpeech tts;              // TTS 변수 선언
    private String a = "안녕";
    public VoiceGuide(Context context){

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != ERROR){
                    tts.setLanguage(Locale.KOREA);
                }
            }
        });
    }
    public void sample(){
        tts.speak(a, TextToSpeech.QUEUE_FLUSH, null);
    }

}
