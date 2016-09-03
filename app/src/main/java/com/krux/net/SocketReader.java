package com.krux.net;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketReader {
    private BufferedReader in;

    public SocketReader(Socket socket){
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read(){
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close(){
        try {
            this.in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
