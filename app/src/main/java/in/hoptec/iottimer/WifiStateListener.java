package in.hoptec.iottimer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.Optional;

/**
 * Created by shivesh on 19/9/18.
 */

public class WifiStateListener {

    public static int STATE_CONNECTED=0,STATE_DISCONNECTED=(STATE_CONNECTED+1);
    public static interface OnStateChange{

        public void onStateChanged(int newState);


    }

    public class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetInfo != null
                    && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                utl.e("WifiStateListener", "Wifi Connected!");
                if(callback!=null)
                     callback.onStateChanged(STATE_CONNECTED);
            } else {
                utl.e("WifiStateListener", "Wifi Not Connected!");
                if(callback!=null)
                    callback.onStateChanged(STATE_DISCONNECTED);
            }
        }
    }

    private ConnectionChangeReceiver connectionChangeReceiver=new ConnectionChangeReceiver();
    private Context ctx;
    private OnStateChange callback;
    public WifiStateListener(Context ctx,OnStateChange cb)
    {
        this.callback=cb;
        this.ctx=ctx;

    }

    public void stopListen()
    {
        try {
            ctx.unregisterReceiver(connectionChangeReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void listen() throws Exception
    {
        int result = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_NETWORK_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {



            IntentFilter filter=new IntentFilter(Manifest.permission.ACCESS_NETWORK_STATE);
            ctx.registerReceiver(connectionChangeReceiver,filter);


        } else {
            throw new Exception("No Permission Please request android.permission.ACCESS_NETWORK_STATE");

        }
    }

}
