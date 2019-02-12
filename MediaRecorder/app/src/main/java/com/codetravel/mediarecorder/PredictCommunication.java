package com.codetravel.mediarecorder;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PredictCommunication implements Runnable {
    private VoiceGuide vg;
    private boolean endFlag;

    PredictCommunication(Context context){
        vg = new VoiceGuide(context);
        endFlag = false;
    }
    @Override
    public void run() {
        Socket socket = null;
        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;


        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String data;

        // 클라이언트
        try {
            // Log.d("communicationSuccess","  ");
            socket = new Socket("172.30.4.145", 90);
            while (true) {
                Log.d("communicationSuccess122","  ");
                os = socket.getOutputStream();
                osw = new OutputStreamWriter(os);
                bw = new BufferedWriter(osw); // 클라이언트로 전송을 위한 OutputStream

                if(endFlag){
                    //프로그램 끝날때
                    //bw.write("end");
                    //bw.newLine();
                    //bw.flush();
                    break;
                }
                /////
                bw.write("end");
                bw.newLine();
                bw.flush();
                is = socket.getInputStream();	//클라이언트에서 받은 메시지 가져옴
                byte[] receiveData = new byte[1024];
                int a = is.read(receiveData);		//byte데이터 저장
                /////

                String reply = new String(receiveData);	//byte string으로 변환
                char replyResult;
                replyResult = reply.charAt(0);

                if(replyResult == '1'){     //예측값에 따른 음성 출력
                    vg.sample(replyResult);
                }
                System.out.println("클라이언트로부터 받은 데이터 : " + reply.charAt(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                osw.close();
                os.close();
                br.close();
                isr.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void EndCommunication(){         //통신을 끝내고 싶을 때
        endFlag = true;
    }
}
