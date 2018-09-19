package in.hoptec.iottimer;

import android.app.Application;
import android.content.Context;


/**
 * Created by shivesh on 2/8/18.
 */

public class App extends Application {


    private static GenricUser userModel;
    public static Context mContext;

    public static GenricUser getGenricUser() {
        if(userModel==null)
        {
            userModel=utl.readUserData();
        }
        return userModel;
    }

    public static void setGenricUser(GenricUser userModel) {
        App.userModel = userModel;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;

    }

    public static Context getAppContext()
    {
        return mContext;
    }


}
