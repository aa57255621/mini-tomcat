package com.gwm.minitomcat.server;

import java.io.File;

public class HttpServer {
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public static void main(String[] args) throws Exception {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }
}
