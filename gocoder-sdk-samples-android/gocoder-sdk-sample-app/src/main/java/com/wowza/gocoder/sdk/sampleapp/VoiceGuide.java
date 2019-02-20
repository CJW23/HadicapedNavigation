package com.wowza.gocoder.sdk.sampleapp;

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
    public void sample(char status){
        if(status == '1') {
            tts.speak("앞에 장애물이 있습니다.", TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    public void sample1(String status){

        if(status == "left") {
            tts.speak("아가리닥쳐.", TextToSpeech.QUEUE_FLUSH, null);
        }else if(status == "right") {
            tts.speak("안물어봤어.", TextToSpeech.QUEUE_FLUSH, null);
        }else if(status == "center") {
            tts.speak("안궁금해.", TextToSpeech.QUEUE_FLUSH, null);
        }else{
            tts.speak("치킨치킨치킨.", TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
