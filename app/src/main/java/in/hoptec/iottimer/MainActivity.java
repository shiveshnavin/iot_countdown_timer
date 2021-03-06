package in.hoptec.iottimer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import in.hoptec.iottimer.utils.GenricCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Context ctx;
    private Activity act;
    ArrayList<Long> laps=new ArrayList<>();

    public static String END_POINT  ="/reset";

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


        public void runHttp(String ip)
        {
            try {
               String  url="http://"+url_reg;
               utl.e(url);


                JSONObject jsonObject=new JSONObject();
                jsonObject.put("ip","http://"+ip+":"+WebServer.PORT+END_POINT);


                JSONObject jo = new JSONObject();
                try {

                    jo.put("ip", ip);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AndroidNetworking.initialize(ctx);
                AndroidNetworking.post(url).addJSONObjectBody(jsonObject).build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        utl.e(response);

                    }

                    @Override
                    public void onError(ANError ANError) {

                        utl.e(ANError.getErrorDetail());

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    private void makeRequest (String ip ){


        runHttp(ip);

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

        WebServer.RequestServer server= (session) -> {

            String res="404 Not Found at "+session.getUri();



            if(session.getUri().contains(END_POINT))
            {
                 JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("status",true);
                    jsonObject.put("cur_time",System.currentTimeMillis());
                    jsonObject.put("elapsed",lastStartedAt==0?0:System.currentTimeMillis()-lastStartedAt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                res=jsonObject.toString();
                utl.e("Click Served");
                res=res.toString();
                dispatchBroadCast();



            }

            utl.e("Serving Req : "+res);
            return res;
        };

        webServer=WebServer.getInstance(server);
        try {
            webServer.start();

            wifiConnected("Wifi Connected\n Server Started at http://"+IP+":"+WebServer.PORT+END_POINT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        intiBroadCast();

    };


    public void onBroadCast(String action)
    {
        if(countDownTimer!=null)

        resetTimer();
    }
    BroadcastReceiver r=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadCast(intent.getStringExtra("action"));
            utl.e("Got broadcast : "+intent.getStringExtra("action"));
        }
    };
    static String BROADCAST="iot.click";
    public   void dispatchBroadCast ( ){

        try {
            LocalBroadcastManager manager=LocalBroadcastManager.getInstance(act.getApplicationContext());
            Intent intent=new Intent();
            intent.setAction(BROADCAST);
            manager.sendBroadcast(intent);
            utl.e("Dispatching broadcast : ");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void intiBroadCast ( ){


        try{
            LocalBroadcastManager  manager=LocalBroadcastManager.getInstance(act.getApplicationContext());

            IntentFilter i=new IntentFilter(BROADCAST);
            manager.registerReceiver(r,i);
        }catch (Exception e)
        {
            e.printStackTrace();
        };

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
                resetTimer();
                break;
            case R.id.imageViewStartStop:
                startStop();
                break;
        }
    }

    /**
     * method to resetTimer count down timer
     */
    public   void resetTimer() {
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
            // showing the resetTimer icon
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

            // hiding the resetTimer icon
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
                // hiding the resetTimer icon
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

        LocalBroadcastManager  manager=LocalBroadcastManager.getInstance(act.getApplicationContext());
        try{
            manager.unregisterReceiver(r);

        }catch (Exception e)
        {
            e.printStackTrace();
        }


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
