package com.gwm.minitomcat;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MiniTomcatApplication {

    public static void main(String[] args) {

        Thread thread = new Thread(new Runnable() {
            public void run() {
                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(8081, 1, InetAddress.getByName("127.0.0.1"));
                    while (true) {
                        Socket accept = serverSocket.accept();
                        InputStream inputStream = accept.getInputStream();
                        StringBuffer sb = new StringBuffer(2048);
                        byte[] buffer = new byte[2048];
                        int len = 0;
                        try {
                            len = inputStream.read(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(len > 0) {
                            sb.append(new String(buffer, 0, len));
                        }
                        String request = sb.toString();
                        System.out.println(request);
                        String message = "<h1>hello world hahahah 123<h1>";
                        String response = "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: text/html\r\n" +
                                "Content-Length: " + message.length() + "\r\n" +
                                "\r\n" +
                                message;
                        accept.getOutputStream().write(response.getBytes());
                        accept.close();
                    }
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();

    }
}
