package in.hoptec.iottimer;

import android.content.Context;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by shivesh on 19/9/18.
 */

public class WebServer {

    public static int PORT=12346;
    private static WebServer server;
    private RequestServer requestServer;

    public WebServer(RequestServer requestServer) {
        this.requestServer = requestServer;

    }

    public static interface RequestServer{
        String  onRequest(NanoHTTPD.IHTTPSession session);
    }


    public class App extends NanoHTTPD {

        public App() throws IOException {
            super(PORT);
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            System.out.println("\nRunning! Point your browsers to http://ip:"+PORT+"/ \n");
        }


        @Override
        public Response serve(IHTTPSession session) {
           return newFixedLengthResponse(requestServer.onRequest(session));
        }
    }

    private App httpd;
    public static WebServer getInstance(RequestServer requestServer)
    {
        if(server==null)
            server=new WebServer(requestServer);
        return server;
    }


    public void stop()
    {
        if(httpd!=null)
            httpd.stop();


    }
    public void start()
    {
        try {
            httpd=new App();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
