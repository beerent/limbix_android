package com.krux.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketWriter {
    private PrintWriter out;

    public SocketWriter(Socket socket){
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String s){
        out.println(s);
    }

    public void close(){
        this.out.close();
    }
}
