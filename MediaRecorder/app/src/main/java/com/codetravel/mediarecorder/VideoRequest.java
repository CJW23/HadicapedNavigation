package com.codetravel.mediarecorder;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;

import android.util.Log;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class VideoRequest implements  Runnable{


    //  TCP연결 관련
    private Socket clientSocket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private int port = 8080;
    private final String ip = "113.198.137.96";
    private MyHandler myHandler;
    private MyThread myThread;
    private  String fileName;

    VideoRequest(){

        // StrictMode는 개발자가 실수하는 것을 감지하고 해결할 수 있도록 돕는 일종의 개발 툴
        // - 메인 스레드에서 디스크 접근, 네트워크 접근 등 비효율적 작업을 하려는 것을 감지하여
        //   프로그램이 부드럽게 작동하도록 돕고 빠른 응답을 갖도록 함, 즉  Android Not Responding 방지에 도움
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            clientSocket = new Socket(ip, port);
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socketOut = new PrintWriter(new BufferedWriter(new
                    OutputStreamWriter(clientSocket.getOutputStream())), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        myHandler = new MyHandler();
        myThread = new MyThread();
        myThread.start();


    }
    String file ;
    @Override
    public void run(){
        String fileName = file;
        File sourceFile = new File(file);

        try{
            //socketOut.println("will send");
            socketOut.flush();

            DataInputStream dis = new DataInputStream(new
                    FileInputStream(sourceFile));
            DataOutputStream dos = new
                    DataOutputStream(clientSocket.getOutputStream());
            byte[] buf = new byte[1024];
            while(dis.read(buf)>0)
            {
                Log.d("fff : ", dos.toString());
                dos.write(buf);
                dos.flush();
            }
            dos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    void sendVideo(String file){
       this.file=file;

    }


    class MyThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    // InputStream의 값을 읽어와서 data에 저장
                    String data = socketIn.readLine();
                    // Message 객체를 생성, 핸들러에 정보를 보낼 땐 이 메세지 객체를 이용
                    Message msg = myHandler.obtainMessage();
                    msg.obj = data;
                    myHandler.sendMessage(msg);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d("This is! : ",msg.obj.toString());
        }
    }
}
