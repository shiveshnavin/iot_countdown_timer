package in.hoptec.iottimer;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


import in.hoptec.iottimer.utils.GenricCallback;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shivesh on 28/6/17.
 */

public class utl {

    public static boolean DISPLAY_ENABLED=true;
    public static boolean DEBUG_ENABLED=false;

    public static Gson js=new Gson();

    public static Context ctx;

    public static void init(Context ctxx)
    {

        js=new Gson();
        ctx=ctxx;
    }



    public static String  getHtml (Context ctx, Object text ){

        ;

        return "<font color=\"" + utl.getHexFromRes(
                ctx.getResources(), R.color.colorAccent) + "\">"+text+"</font>" ;
    };
    public static void animate_avd(ImageView img)
    {

        try {
            final Drawable drawable = img.getDrawable();

            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            }
        } catch (Exception e) {

            utl.e("ERROR WHILE ANIMATING IMAGEVIEW");
        }


    }

    public static void animate(View app, String property, int initv, int finalv, boolean repeat, int dur)
    {


        ObjectAnimator colorAnim = ObjectAnimator.ofInt(app, property,
                initv, finalv);
        colorAnim.setEvaluator(new ArgbEvaluator());

        if(repeat) {
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        }

        colorAnim.setDuration(dur);
        colorAnim.start();

    }



    public static void animateBackGround(View app, String  initcolor, String finalcolor, boolean repeat, int dur)
    {

        String property="backgroundColor";
        ObjectAnimator colorAnim = ObjectAnimator.ofInt(app, property,
                Color.parseColor(initcolor), Color.parseColor(finalcolor));
        colorAnim.setEvaluator(new ArgbEvaluator());

        if(repeat) {
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        }

        colorAnim.setDuration(dur);
        colorAnim.start();

    }




    public static String getHrsFromMin(int mins)
    {
        String st="";

        if(mins<60)
        {
            return st+mins+" mins";
        }
        int hrs=mins/60;
        st=""+hrs+" hr ";
        int mns=mins-hrs*60;
        st=st+" "+mns+" mins";

        return st;

    }


    public static int getColor(@ColorRes int c)
    {
        int col=0;
        col=ctx.getResources().getColor(c);
        return col;
    }



    public static void setStatusBarColor(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window w = context.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight =  getStatusBarHeight(context);

            View view = new View(context);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
    }



    public static int getStatusBarHeight(Activity context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public static void fullScreen(Activity act)
    {
        try {
            act.requestWindowFeature(Window.FEATURE_NO_TITLE);
            act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int getH(Context ctx)
    {
        WindowManager windowManager = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getHeight();

    }


    public static int getW(Context ctx)
    {
        WindowManager windowManager = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();

    }




    public static String BOLD="font_bold.otf",
            REGULAR="font_regular.ttf"
            ,TEXT="font_text.ttf"
            , THIN="font_thin.ttf" ;


    public static Typeface getFace(String font,Context ctx)
    {
        Typeface face = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/"+font);

        if(face==null)
        {
            face=Typeface.createFromAsset(ctx.getAssets(),
                    "fonts/"+TEXT);
        }
        return face;

    }



    public static Typeface setFace(String font,TextView textView)
    {
        Context ctx=textView.getContext();
        Typeface face = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/"+font);

        if(face==null)
        {
            face= Typeface.createFromAsset(ctx.getAssets(),
                    "fonts/"+TEXT);
        }
        textView.setTypeface(face);
        return face;

    }


    public static void changeTextColor(final TextView textView, int startColor, int endColor,
                                       final long animDuration, final long animUnit ){
        if (textView == null) return;

        final int startRed = Color.red(startColor);
        final int startBlue = Color.blue(startColor);
        final int startGreen = Color.green(startColor);

        final int endRed = Color.red(endColor);
        final int endBlue = Color.blue(endColor);
        final int endGreen = Color.green(endColor);

        final CountDownTimer ct= new CountDownTimer(animDuration, animUnit) {
            //animDuration is the time in ms over which to run the animation
            //animUnit is the time unit in ms, updateImages color after each animUnit

            @Override
            public void onTick(long l) {
                int red = (int) (endRed + (l * (startRed - endRed) / animDuration));
                int blue = (int) (endBlue + (l * (startBlue - endBlue) / animDuration));
                int green = (int) (endGreen + (l * (startGreen - endGreen) / animDuration));

                textView.setTextColor(Color.rgb(red, green, blue));
            }

            @Override
            public void onFinish() {
                textView.setTextColor(Color.rgb(endRed, endGreen, endBlue));

            }
        };

        ct.start();
    }



    public static String refineString(String red,String rep)
    {
        red = red.replaceAll("[^a-zA-Z0-9]", rep);
        return  red;
    }


    public static boolean isEmpty(String s)
    {
        return isNull(s) || s.length()<1;
    }

    public static boolean isNull(String is)
    {
        return is==null||(""+is).equals("null");
    }
    public static  boolean isValidMobile(String phone)
    {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private boolean isValidMail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static String TAG="TAG UTL";
    public static void log(String t)
    {

        Log.d(""+TAG, ""+t);
    }


    public static void log(Object t)
    {

        Log.d(""+TAG, ""+utl.js.toJson(t));
    }

    public static void log(String t,String tt)
    {

        Log.d(""+t, ""+tt);
    }


    public static void l(String t)
    {

        Log.d(""+TAG, ""+t);
    }


    public static void l(String t,String tt)
    {

        Log.d(""+t, ""+tt);
    }




    public static void e(String  TAG,Object t)
    {

        Log.e(""+TAG, ""+js.toJson(t));
    }



    public static void e(Object t)
    {

        Log.e(""+TAG, ""+js.toJson(t));
    }

    public static void e(String t)
    {

        Log.e(""+TAG, ""+t);
    }
    public static void e(String t,String tt)
    {

        Log.e(""+t, ""+tt);
    }

    public static void l(Object t)
    {

        Log.d(""+TAG, ""+t);
    }


    public static void l(String t,Object tt)
    {

        Log.d(""+t, ""+tt);
    }



    public static void logout()
    {
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }





        try {
            removeUserData();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public static String  getHexFromRes(Resources resources, @ColorRes int res){
        return   String.format("#%06X", (0xFFFFFF & resources.getColor( res)));

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

        }

        return inSampleSize;
    }


    public static Bitmap decodeSampledBitmapFromResource(String pathToFile,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToFile, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathToFile, options);
    }


    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth ) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) ;
        float scaleHeight = ((float) newHeight) ;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap
                (bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }


    public static  File savebitmap(String filename,Bitmap bitmap,int q) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(filename  );
        if (file.exists()) {
            file.delete();
            file = new File(filename);

        }
        try {



            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, q, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }


    public static int getDominantColor1(Bitmap bitmap) {

        if (bitmap == null)
            throw new NullPointerException();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];

        Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);

        bitmap2.getPixels(pixels, 0, width, 0, 0, width, height);

        final List<HashMap<Integer, Integer>> colorMap = new ArrayList<HashMap<Integer, Integer>>();
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());

        int color = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        Integer rC, gC, bC;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);

            rC = colorMap.get(0).get(r);
            if (rC == null)
                rC = 0;
            colorMap.get(0).put(r, ++rC);

            gC = colorMap.get(1).get(g);
            if (gC == null)
                gC = 0;
            colorMap.get(1).put(g, ++gC);

            bC = colorMap.get(2).get(b);
            if (bC == null)
                bC = 0;
            colorMap.get(2).put(b, ++bC);
        }

        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            int max = 0;
            int val = 0;
            for (Map.Entry<Integer, Integer> entry : colorMap.get(i).entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    val = entry.getKey();
                }
            }
            rgb[i] = val;
        }

        int dominantColor = Color.rgb(rgb[0], rgb[1], rgb[2]);

        return dominantColor;
    }

    public static Float distance (Double lat_a, Double lng_a, Double lat_b, Double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    public static int getApkVerison(Context ctx)
    {
        try{
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;

        }
    }


    @SuppressWarnings("ResourceType")
    public static void changeColorDrawable(ImageView imageView, @DrawableRes int res) {

        DrawableCompat.setTint(imageView.getDrawable(), ContextCompat.getColor(ctx, res));



    }



    public static Float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static Float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }



    public static void showSoftKeyboard(Activity activity, View linearLayout) {

        InputMethodManager inputMethodManager =
                (InputMethodManager)activity. getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                linearLayout.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }



    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.toLowerCase().contains(context.getPackageName())||
                                context.getPackageName().toLowerCase().contains(activeProcess.toLowerCase())
                                ) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().toLowerCase().contains(context.getPackageName())||
                    context.getPackageName().toLowerCase().contains(componentInfo.getPackageName().toLowerCase())
                    ) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(900);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
    public static void addPressReleaseAnimation(final View base)
    {

        final Animation press= AnimationUtils.loadAnimation(base.getContext(),R.anim.rec_zoom_in);
        final Animation release= AnimationUtils.loadAnimation(base.getContext(),R.anim.rec_zoom_nomal);

        base.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        base.startAnimation(press);
                        break;
                    case MotionEvent.ACTION_UP:

                        base.startAnimation(release);
                        break;
                    case MotionEvent.ACTION_CANCEL:

                        base.startAnimation(release);
                        break;
                    default:
                        break;
                }



                return false;
            }
        });


    }
    @SuppressWarnings("ResourceAsColor")
    public static void snack(Activity act, String t)
    {

        View rootView = act.getWindow().getDecorView().getRootView();
        Snackbar snackbar = Snackbar.make(rootView,  ""+t, Snackbar.LENGTH_LONG);
        // snackbar.setActionTextColor(act.getResources().getColor(R.color.material_red_A400));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.colorAccent));

        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if(DISPLAY_ENABLED)
            snackbar.show();

    }




    public static void snack(View rootView,String t)
    {

        Context act=rootView.getContext();

        Snackbar snackbar = Snackbar.make(rootView,  ""+t, Snackbar.LENGTH_LONG);
        // snackbar.setActionTextColor(act.getResources().getColor(R.color.material_red_A400));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.colorAccent));

        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if(DISPLAY_ENABLED)
            snackbar.show();

    }


    public static void snack(Activity act,String t,String a,final GenricCallback cb)
    {

        View rootView = act.getWindow().getDecorView().getRootView();

        Snackbar snackbar = Snackbar.make(rootView,  ""+t, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(act.getResources().getColor(R.color.grey_100));
        snackbar.setAction("" + a, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.onStart();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.colorAccent));

        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if(DISPLAY_ENABLED)
            snackbar.show();

    }






    public static void snack(View rootView,String t,String a,final GenricCallback cb)
    {


        Context act=rootView.getContext();

        Snackbar snackbar = Snackbar.make(rootView,  ""+t, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(act.getResources().getColor(R.color.grey_100));
        snackbar.setAction("" + a, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.onStart();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.colorAccent));

        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if(DISPLAY_ENABLED)
            snackbar.show();

    }



    public static void notify(String title,String message,Context ctx){

            Intent intent = new Intent(ctx, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx)
                    .setSmallIcon(
                            R.drawable.ic_notifications_black_24dp)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());



    }




    public static interface ClickCallBack{

        public void done(DialogInterface dialogInterface);

    }
    public static void diag(Context c,String title,String desc,String action,final ClickCallBack click)
    {



        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(desc);
        alertDialogBuilder.setNeutralButton(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                click.done(dialogInterface);

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static void diag(Context c,String title,String desc,boolean isCancellable,String action,final ClickCallBack click)
    {



        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(desc);
        alertDialogBuilder.setCancelable(isCancellable);
        alertDialogBuilder.setNeutralButton(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                click.done(dialogInterface);

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static String  getRoom (String uid1,String uid2 ){

        ;
        String cr=uid1+uid2;
        char [] chr=cr.toCharArray();
        Arrays.sort(chr);

        return new String(chr);

    };

    public static void diag(Context c,String title,String desc)
    {
        try {
            final AlertDialog.Builder
                    alertDialogBuilder = new AlertDialog.Builder
                    (c);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(Html.fromHtml(desc));
            alertDialogBuilder.setNeutralButton("OK", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface
                                                    dialog, int id) {
                            dialog.cancel();
                        }
                    });


            AlertDialog alertDialog
                    = alertDialogBuilder.create();


            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void toast(Context c,String t) {


        try {
            if(DISPLAY_ENABLED)
                Toast.makeText(c, t, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static interface InputDialogCallback {
        public void onDone(String text);
    }

    public static  Integer TYPE_EMAIL=120,TYPE_PHONE=293,TYPE_DEF=101;


    public static EditText input  ;

   public static void inputDialogBottom(Context ctx,String title,String hint,final int TYPE,final InputDialogCallback callback)
    {
         final BottomSheetDialog mBottomSheetDialog;;


        mBottomSheetDialog = new BottomSheetDialog(ctx);
        mBottomSheetDialog.setTitle(title);
        LayoutInflater inf=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inf.inflate(R.layout.diag_input_bottom, null);

        final EditText input=(EditText)sheetView.findViewById(R.id.text);
        Button done=(Button)sheetView.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = input.getText().toString();
                Log.d("", "Text Value : " + value);

                mBottomSheetDialog.dismiss();
                callback.onDone(value);
            }
        });


        input.setHint(hint);


        if(TYPE==TYPE_EMAIL)
            input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        if(TYPE==TYPE_PHONE)
            input.setInputType(InputType.TYPE_CLASS_PHONE);
        else
            input.setInputType(InputType.TYPE_CLASS_TEXT);

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    {
                        String value = input.getText().toString();
                        Log.d("", "Text Value : " + value);

                        mBottomSheetDialog.dismiss();
                        callback.onDone(value);
                    }
                    return true;
                }
                return false;
            }
        });



        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();



    }


    public static void diagBottom(Context ctx, String text )
    {

        diagBottom(ctx, "", text,true, "DISMISS",  new GenricCallback() {
            @Override
            public void onStart() {

            }
        });
    }
    public static void diagBottom(Context ctx,String title,String text,boolean cancellableOnTouchOutside,String actionText,final GenricCallback callback)
    {
        final BottomSheetDialog mBottomSheetDialog;;


        mBottomSheetDialog = new BottomSheetDialog(ctx);
        LayoutInflater inf=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inf.inflate(R.layout.diag_bottom, null);

        final TextView textT=(TextView)sheetView.findViewById(R.id.text);
        final TextView titleT=(TextView)sheetView.findViewById(R.id.title);


        Button done=(Button)sheetView.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();
                callback.onStart();
            }
        });

        mBottomSheetDialog.setCanceledOnTouchOutside(cancellableOnTouchOutside);




        if(title.length()<2)
        {
            titleT.setVisibility(View.GONE);
        }
        done.setText(actionText);
        textT.setText(Html.fromHtml(text));
        titleT.setText(title);


        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();



    }



    public static BottomSheetDialog diagBottomList(Context ctx, String title, RecyclerView.Adapter adapter, boolean cancellableOnTouchOutside, String actionText, final GenricCallback callback)
    {
        final BottomSheetDialog mBottomSheetDialog;;


        mBottomSheetDialog = new BottomSheetDialog(ctx);
        LayoutInflater inf=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inf.inflate(R.layout.diag_bottom_list, null);

        final RecyclerView list=(RecyclerView)sheetView.findViewById(R.id.list);
        final TextView titleT=(TextView)sheetView.findViewById(R.id.title);


        Button done=(Button)sheetView.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();
                callback.onStart();
            }
        });

        mBottomSheetDialog.setCanceledOnTouchOutside(cancellableOnTouchOutside);




        if(title.length()<2)
        {
            titleT.setVisibility(View.GONE);
        }
        done.setText(actionText);
        titleT.setText(title);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(adapter);


        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();


        return mBottomSheetDialog;


    }



    public static BottomSheetDialog diagBottomGrid(Context ctx, String title, RecyclerView.Adapter adapter,int numberOfColumns, boolean cancellableOnTouchOutside, String actionText, final GenricCallback callback)
    {
        final BottomSheetDialog mBottomSheetDialog;;


        mBottomSheetDialog = new BottomSheetDialog(ctx);
        LayoutInflater inf=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inf.inflate(R.layout.diag_bottom_list, null);

        final RecyclerView list=(RecyclerView)sheetView.findViewById(R.id.list);
        final TextView titleT=(TextView)sheetView.findViewById(R.id.title);


        Button done=(Button)sheetView.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();
                callback.onStart();
            }
        });

        mBottomSheetDialog.setCanceledOnTouchOutside(cancellableOnTouchOutside);




        if(title.length()<2)
        {
            titleT.setVisibility(View.GONE);
        }
        done.setText(actionText);
        titleT.setText(title);
       // LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(new GridLayoutManager(ctx, numberOfColumns));

        list.setAdapter(adapter);


        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();


        return mBottomSheetDialog;


    }




    public static String getRealPathFromUri(Uri uri) {
        String result = null;
        try {
            if (uri == null)
                return null;

            String documentID;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                String[] pathParts = uri.getPath().split("/");
                documentID = pathParts[pathParts.length - 1];
            } else {
                String pathSegments[] = uri.getLastPathSegment().split(":");
                documentID = pathSegments[pathSegments.length - 1];
            }
            String mediaPath = MediaStore.Images.Media.DATA;

            Cursor imageCursor = ctx.getContentResolver().query(uri, new String[]{mediaPath}, MediaStore.Images.Media._ID + "=" + documentID, null, null);
            if (imageCursor.moveToFirst()) {
                result = imageCursor.getString(imageCursor.getColumnIndex(mediaPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null)
            result = uri.getPath().replace("file://", "").replace("%20", " ");
        utl.l("Found File path " + result);
        return result;
    }

    public static FirebaseUser getUser()
    {

        return FirebaseAuth.getInstance().getCurrentUser();

    }

    public static String getFCMTOken()
    {

      //  String refreshedToken = FirebaseInstanceId.getInstance().getToken();
      //  return refreshedToken;
        return "";
    }




    public static final String MY_PREFS_NAME = "smartnglow";
    public static  SharedPreferences.Editor editor;// = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

    public static void setKey(String k,String v,Context ctx)
    {

        utl.l("KEY SET Key:  -"+k+ "- VAL: "+v);
        editor = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(k,v);

        editor.commit();

    }

    public static String getKey(String k,Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(k, null);

        utl.l("KEY GET : "+k+" VAL: "+restoredText);

        return restoredText;
    }

    public static void setShared(Context ctx)
    {
        /*
        editor.putString("shopName", "Elena");
        editor.putInt("idName", 12);
        editor.commit();*/


        SharedPreferences prefs = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("installed", null);
        if (restoredText != null) {

            String name = prefs.getString("installed", "true");

            Log.d("INSTALL "," ALREADY INSTALL ");
        }
        else {
            Log.d("INSTALL "," FIRST INSTALL ");

            editor = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("installed", "true");

            editor.commit();
        }

    }


    public static void copyFile(File src,File dst)
    {
        try{

            if(!src.exists())
                return;
            InputStream in=new FileInputStream(src);
            OutputStream os=new FileOutputStream(dst);

            byte []buf=new byte[1024];
            int len;
            while((len=in.read(buf))>0)
            {
                os.write(buf,0,len);
            }
            in.close();
            os.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }



    }



    public static  boolean removeUserData()
    {


        utl.setKey("data",null,ctx);
        return  true;
    }



    public static  boolean writeUserData(GenricUser guser, Context ctx)
    {
        utl.setKey("data",js.toJson(guser),ctx);
        Log.d("DATA WROTE",""+utl.getKey("data",ctx));
        return  true;
    }


    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }



    public static String uid(int l)
    {
        final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        System.out.println("uuid Full= " + uuid);
        String ret= uuid.substring(0, Math.min(uuid.length(), l));;
        System.out.println("uuid "+l+" = " + ret);
        return ret;
    }


    public static int randomInt( )
    {

        int ret=1;
        Random random=new Random();
        ret=random.nextInt();
        return ret;
    }


    public static int randomInt( int len)
    {

        int ret=1;
        Random random=new Random();
        ret=random.nextInt();
        if(ret<0)
        {
            ret=ret*-1;
        }


        while (getIntLen(ret)>len){
            ret=ret/10;
        }


        return ret;
    }

    public static  int getIntLen(int n){

         int len=0;

        while(n>0)
        {
            len++;
            n=n/10;
        }

         return len;

    }


    public static GenricUser readUserData()
    {
         if(getKey("data",App.getAppContext())==null)
            return null;

         Gson g=new Gson();
        try {
            Log.d("DATA READ","");
            //Log.d("DATA READ",""+fop.read(data));
            GenricUser guser=g.fromJson(getKey("data",App.getAppContext()),GenricUser.class);
            return  guser;


        } catch (JsonSyntaxException e) {

            e.printStackTrace();

            return  null;
        }
    }


    public static String dev()
    {
        return "_"+utl.refineString(Build.DEVICE,"_")+"_"+utl.refineString(Build.MANUFACTURER,"_");
    }

    public static String getRealPathFromUri(Context ctx,Uri uri) {
        String result = "";
        String documentID;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String[] pathParts = uri.getPath().split("/");
            documentID = pathParts[pathParts.length - 1];
        } else {
            String pathSegments[] = uri.getLastPathSegment().split(":");
            documentID = pathSegments[pathSegments.length - 1];
        }
        String mediaPath = MediaStore.Images.Media.DATA;
        Cursor imageCursor = ctx.getContentResolver().query(uri, new String[]{mediaPath}, MediaStore.Images.Media._ID + "=" + documentID, null, null);
        if (imageCursor.moveToFirst()) {
            result = imageCursor.getString(imageCursor.getColumnIndex(mediaPath));
        }
        return result;
    }




    public static Dialog dialog;
    public static Dialog showDig(boolean show,Context ctx)
    {
        try {
            if(dialog!=null)
                if(dialog.isShowing()&&show)
                {
                    return dialog;
                }
            if(show)
            {

                utl.log("DIAG_OPEN : "+ctx.getClass());
                dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.gen_load);
                final Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(ctx,R.color.transblack2)));
                //dialog.getWindow().getAttributes().alpha = 0.7f;


                dialog.setContentView(R.layout.gen_load);
                //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);

                dialog.show();

                ImageView mIcDownloadAnimator = (ImageView) dialog.findViewById(R.id.loading);
                final Drawable drawable = mIcDownloadAnimator.getDrawable();

                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
/*

                Animation rotation = AnimationUtils.loadAnimation(ctx, R.anim.rot_3d);
                rotation.setFillAfter(true);
                mIcDownloadAnimator.startAnimation(rotation);



                AVLoadingIndicatorView splashView=(AVLoadingIndicatorView)dialog.findViewById(R.id.splash_view2);
                splashView.show();
*/


            }
            else   {



                utl.log("DIAG_CLOSE: "+ctx.getClass());
                if(dialog!=null)
                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

            }
        } catch (Exception e) {

        }

        return dialog;
    }


    public static boolean saveVideoThumb(String filenameTarget,String video)
    {
        Bitmap bmp;

        bmp = ThumbnailUtils.createVideoThumbnail(video, MediaStore.Video.Thumbnails.MINI_KIND);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filenameTarget);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        }


        finally {
            try {
                if (out != null) {
                    out.close();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return false;
        }


    }


    public static void logEvent(String title,Object b){


        try {
            String ins=utl.getDateTime();

            //  App.ses.child(utl.refineString( ins,"_")).child("data").setValue((new Gson()).toJson(b).replace("\\\"","\"").replace("\"{","{").replace("}\"","}"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isMyServiceRunning(Class<?> serviceClass,Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }









}