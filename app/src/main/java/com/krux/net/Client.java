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
    private String response;

    public Client(){
        try {
            init();
            System.out.println("connection made");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void init() throws IOException{
        this.socket = new Socket("52.43.105.55", 1313);
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
        receive();
        send(s);
        return receive();
    }

}
