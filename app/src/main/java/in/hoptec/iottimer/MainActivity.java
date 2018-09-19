package in.hoptec.iottimer;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import fi.iki.elonen.NanoHTTPD;
import in.hoptec.iottimer.utils.GenricCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Context ctx;
    private Activity act;
    ArrayList<Long> laps=new ArrayList<>();


    private long timeCountInMilliSeconds = 1 * 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;
    private EditText editTextMinute;
    private TextView textViewTime,wifi;
    private ImageView imageViewReset;
    private ImageView imageViewStartStop;
    private CountDownTimer countDownTimer;


    String url_reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this;
        act=this;
        setContentView(R.layout.activity_main);

        // method call to initialize the views
        initViews();
        // method call to initialize the listeners
        initListeners();

        initWifiState();

        url_reg=utl.getKey("url",ctx);
        if(url_reg==null)
        {
            utl.inputDialogBottom(ctx, "Enter IP registry path ","eg: 192.168.4.1/reg_ip", utl.TYPE_DEF, new utl.InputDialogCallback() {
                @Override
                public void onDone(String text) {

                    utl.setKey("url",text.replace("http://","").replace("https://",""),ctx);
                    url_reg=utl.getKey("url",ctx);
                }
            });
        }



        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        if(ip!=null && !ip.contains("0.0.0.0"))
        {

            utl.e("Got IP : "+ip);
            wifiConnected("Wifi Connected");
            initWebServer(ip);
            makeRequest(ip);
        }


    }

    private void flash ( ){

        View activity_main=findViewById(R.id.activity_main);
        utl.animateBackGround(activity_main,"#37474f","#151B29",false,100);

    };

    public static class HttpCon extends Thread
    {
        String url;
        String ip;
        public HttpCon(String url,String ip)
        {
            this.url=url;
            this.ip=ip;
        }
        public void run()
        {
            try {
                url="http://"+url;
                URL ur=new URL(url);
                utl.e("HttpCon","Call : "+url);
                HttpURLConnection con =  (HttpURLConnection)ur.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type","application/json");
                con.setRequestProperty("Accept","application/json");

                HttpURLConnection.setFollowRedirects(true);
                con.setInstanceFollowRedirects(false);
                con.setDoOutput(true);


                OutputStream ops= con.getOutputStream();
                InputStream ips=con.getInputStream();

                JSONObject jsonObject=new JSONObject();
                jsonObject.put("ip",ip);

                ops.write(jsonObject.toString().getBytes());
                utl.e("HttpCon","Bodt : "+jsonObject.toString());

                utl.e("HttpCon","Response Code : "+con.getResponseCode());

                BufferedReader b=new BufferedReader(new InputStreamReader(ips));
                StringBuffer bf=new StringBuffer();
                String singleRes;
                while ((singleRes=b.readLine())!=null)
                {
                    bf.append(singleRes);
                }

                utl.e("HttpCon","Response : "+bf.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private void makeRequest (String ip ){
        HttpCon con=new HttpCon(url_reg,ip);
        con.start();


    };

    WifiStateListener listener;
    private void initWifiState ( ){

         listener=new WifiStateListener(ctx, new WifiStateListener.OnStateChange() {
            @Override
            public void onStateChanged(int newState) {

                if(newState==WifiStateListener.STATE_CONNECTED)
                {
                    WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                    utl.e("Got IP : "+ip);
                    wifiConnected("Wifi Connected");
                    initWebServer(ip);
                    makeRequest(ip);
                }
                else {

                    wifiConnected("Wifi Dis Connected");
                }

            }
        });


        try {
            listener.listen();
        } catch (Exception e) {
            e.printStackTrace();
        }


    };

    private void wifiConnected (String t ){

        wifi.setTextColor(getResources().getColor(R.color.material_green_300));
        wifi.setText(t);

    };


    private void wifiDisconnected (String t ){

        wifi.setTextColor(getResources().getColor(R.color.material_grey_300));
        wifi.setText(t);

    };

    WebServer webServer;
    private void initWebServer (String IP ){

        WebServer.RequestServer server= (uri, method, headers, parms, files) -> {

            String res="404 Not Found";
            NanoHTTPD.Response.IStatus status=NanoHTTPD.Response.Status.NOT_FOUND;
            String mime=NanoHTTPD.MIME_PLAINTEXT;


            if(uri.contains("click"))
            {
                status=NanoHTTPD.Response.Status.OK;
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("status",true);
                    jsonObject.put("cur_time",System.currentTimeMillis());
                    jsonObject.put("elapsed",lastStartedAt==0?0:System.currentTimeMillis()-lastStartedAt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                res=jsonObject.toString();

            }

            return new NanoHTTPD.Response(status,mime,res);
        };

        webServer=WebServer.getInstance(server);
        try {
            webServer.start();

            wifiConnected("Wifi Connected\n Server Started at http://"+IP+"/click");
        } catch (Exception e) {
            e.printStackTrace();
        }

    };








    /**
     * method to initialize the views
     */
    private void initViews() {
        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
        editTextMinute = (EditText) findViewById(R.id.editTextMinute);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        imageViewReset = (ImageView) findViewById(R.id.imageViewReset);
        imageViewStartStop = (ImageView) findViewById(R.id.imageViewStartStop);
        wifi=(TextView)findViewById(R.id.wifi);
    }

    /**
     * method to initialize the click listeners
     */
    private void initListeners() {
        imageViewReset.setOnClickListener(this);
        imageViewStartStop.setOnClickListener(this);
    }

    /**
     * implemented method to listen clicks
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewReset:
                reset();
                break;
            case R.id.imageViewStartStop:
                startStop();
                break;
        }
    }

    /**
     * method to reset count down timer
     */
    private void reset() {
        flash();
        stopCountDownTimer();
        startCountDownTimer();
    }


    /**
     * method to start and stop count down timer
     */
    private void startStop() {
        stopTimer();
        if (timerStatus == TimerStatus.STOPPED) {

            laps=new ArrayList<>();
            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            // showing the reset icon
            imageViewReset.setVisibility(View.VISIBLE);
            // changing play icon to stop icon
            imageViewStartStop.setImageResource(R.drawable.icon_stop);
            // making edit text not editable
            editTextMinute.setEnabled(false);
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {

            // hiding the reset icon
            imageViewReset.setVisibility(View.GONE);
            // changing stop icon to start icon
            imageViewStartStop.setImageResource(R.drawable.icon_start);
            // making edit text editable
            editTextMinute.setEnabled(true);
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues() {
        Double time = 0d;
        if (!editTextMinute.getText().toString().isEmpty()) {
            // fetching value from edit text and type cast to integer
            time = Double.parseDouble(editTextMinute.getText().toString().trim());
        } else {
            // toast message to fill edit text
            Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
        }
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds =  (Double.valueOf(time * 60 * 1000).longValue());
    }

    long lastStartedAt=0;
    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        lastStartedAt=System.currentTimeMillis();
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {
                /*
                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
                // hiding the reset icon
                //imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                imageViewStartStop.setImageResource(R.drawable.icon_start);
                // making edit text editable
                editTextMinute.setEnabled(true);
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;*/
                startTimer(timeCountInMilliSeconds);
            }

        }.start();
        countDownTimer.start();
    }

    Timer timer;
    private void startTimer (final Long startOffset ){

        if(timer!=null)
        {
            timer.cancel();
        }
        timer=new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewTime.setText(hmsTimeFormatter(System.currentTimeMillis()-lastStartedAt-startOffset));
                    }
                });

            }
        }, 0, 10);


    };

    private void stopTimer ( ){

        if(timer!=null)
        {
            timer.cancel();
        }

    };


    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {

        laps.add(System.currentTimeMillis()-lastStartedAt);
        lastStartedAt=0;
        stopTimer();
        if(countDownTimer!=null)
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return mm:ss:mm time formatted string
     */
    public static String hmsTimeFormatter(long milliSeconds) {

       /* String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)),
                milliSeconds - 1000*(TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds))));
*/
       TimeCounter tc=new TimeCounter(milliSeconds);
        return tc.getTimeString();


    }


    @Override
    protected void onDestroy() {
        stopCountDownTimer();
        if(webServer!=null)
            webServer.stop();
        listener.stopListen();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater m=getMenuInflater();
        m.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.history)
        {
            ArrayList<RecAdapter.Dummy> list=new ArrayList<>();
            int sum=0;
            int i=0;
            for (i=0;i<laps.size();i++)
            {
                sum+=laps.get(i);
                list.add(new RecAdapter.Dummy(""+(i+1),""+laps.get(i)));
            }



            RecAdapter adapter=new RecAdapter(ctx,list);
            String avg=hmsTimeFormatter((Double.valueOf(Math.round((sum/(i==0?1:i)) * 1000d) / 1000d).longValue()));
            utl.diagBottomList(ctx, "Your Avg : " + avg, adapter, true, "DISMISS", new GenricCallback() {
                @Override
                public void onStart() {

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
