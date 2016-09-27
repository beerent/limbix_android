package com.krux.net;

import android.os.AsyncTask;

import com.krux.session.ActiveSession;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by brent on 8/23/16.
 */
public class Client{
    private SocketReader in;
    private SocketWriter out;
    private Socket socket;

    public Client(){
        try {
            init();
            System.out.println("connection made");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void init() throws IOException{
        String key = ActiveSession.getDeveloperServerKey();
        if(key == null || key.equals(""))
            key = "pro";
        if(key.equals("aws"))
            this.socket = new Socket("52.43.169.37", 1313); // aws
        else if(key.equals("usb"))
            this.socket = new Socket("10.0.2.2", 1313); // local - no network
        else if(key.equals("phone wifi"))
            this.socket = new Socket("192.168.43.37", 1313); // phones wifi
        else if(key.equals("air"))
            this.socket = new Socket("192.168.1.105", 1313);  // macbook air
        else if(key.equals("pro"))
            this.socket = new Socket("192.168.1.125", 1313);  // macbook pro
        this.in = new SocketReader(this.socket);
        this.out = new SocketWriter(this.socket);
    }

    public String receive(){
        return this.in.read();
    }

    public void send(String s){
        this.out.write(s);
    }

    public String handleTransaction(String s){
        System.out.println(s);
        receive();
        send(s);
        String response = receive();
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}
