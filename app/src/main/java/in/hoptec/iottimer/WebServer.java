package in.hoptec.iottimer;

import android.content.Context;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by shivesh on 19/9/18.
 */

public class WebServer {

    private static WebServer server;
    private RequestServer requestServer;

    public WebServer(RequestServer requestServer) {
        this.requestServer = requestServer;
    }

    public static interface RequestServer{
        NanoHTTPD.Response onRequest(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files);
    }


    private NanoHTTPD httpd=new NanoHTTPD(80) {

        @Override
        public void start() throws IOException {
            utl.e("WebServer","Started !");
            super.start();
        }

        @Override
        public void stop() {
            utl.e("WebServer","Stopped !");
            super.stop();
        }

        @Override
        public NanoHTTPD.Response serve(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {

            utl.e("WebServer","Serving "+uri);
            return requestServer.onRequest(uri, method, headers, parms, files);
        }


    };
    public static WebServer getInstance(RequestServer requestServer)
    {
        if(server==null)
            server=new WebServer(requestServer);
        return server;
    }


    public void stop()
    {
        httpd.stop();
    }
    public void start()
    {
        try {
            httpd.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
