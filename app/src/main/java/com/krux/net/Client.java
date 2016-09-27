package com.krux.net;

import android.os.AsyncTask;

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
        //this.socket = new Socket("52.43.169.37", 1313); // aws
        //this.socket = new Socket("10.0.2.2", 1313); // local - no network
        //this.socket = new Socket("192.168.43.37", 1313); // phones wifi
        //this.socket = new Socket("192.168.1.105", 1313);  // macbook air
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
