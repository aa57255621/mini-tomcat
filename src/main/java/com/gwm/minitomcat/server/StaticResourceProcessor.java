package com.gwm.minitomcat.server;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class StaticResourceProcessor {

    private static final int BUFFER_SIZE = 1024;

    private static String fileNotFoundMessage = "HTTP/1.1 404 Not Found\r\n" +
            "Content-Type: text/html\r\n" +
            "Content-Length: 23\r\n" +
            "\r\n" +
            "<h1>File Not Found</h1>\r\n";
    private static String OKMessage = "HTTP/1.1 ${StatusCode} ${StatusName}\r\n" +
            "Content-Type: ${ContentType}\r\n" +
            "Content-Length: ${ContentLength}\r\n" +
            "Server: MiniTomcat/1.0.0\r\n" +
            "Date: ${ZonedDateTime}\r\n" +
            "\r\n";

    public void process(Request request, Response response) throws Exception {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        OutputStream output = null;

        try {
            output = response.getOutput();
            File file = new File(HttpServer.WEB_ROOT, request.getUri());
            if (file.exists()) {
               String head = composeResponseHead(file);
               output.write(head.getBytes(StandardCharsets.UTF_8));

               fis = new FileInputStream(file);
               int ch = fis.read(bytes, 0, BUFFER_SIZE);
               while (ch != -1) {
                   output.write(bytes, 0, ch);
                   ch = fis.read(bytes, 0, BUFFER_SIZE);
               }
               output.flush();

            } else {
                output.write(fileNotFoundMessage.getBytes());
            }
        }catch (IOException e) {
            System.out.println("Error processing request: " + e.getMessage());
        }finally {
            if (output != null) {
                fis.close();
            }
        }
    }

    private String composeResponseHead(File file) {
        long fileLength = file.length();
        Map<String, Object> valuesMap = new HashMap<String, Object>();
        valuesMap.put("StatusCode", "200");
        valuesMap.put("StatusName", "OK");
        valuesMap.put("Content-Type", "text/html;charset=UTF-8");
        valuesMap.put("Content-Length", fileLength);
        valuesMap.put("ZonedDateTime", ZonedDateTime.now().toString());
        valuesMap.put("Server", "MiniTomcat/1.0");
        StrSubstitutor subitutor = new StrSubstitutor(valuesMap);
        String responseHead = subitutor.replace(OKMessage);
        return responseHead;
    }
}
