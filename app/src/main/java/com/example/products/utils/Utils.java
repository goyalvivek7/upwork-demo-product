package com.example.products.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.ViewCompat;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Utils {
    private static final String TAG = "TagAlong-Utils";
    private static Utils utilsInstance;
    private MediaPlayer mediaPlayer;
    public static Utils getInstance() {
        if (utilsInstance == null) {
            utilsInstance = new Utils();
        }
        return utilsInstance;
    }

    public String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public String secondsToString(int pTime) {
        final int min = pTime/60;
        final int sec = pTime-(min*60);
        final String strMin = placeZeroIfNeede(min);
        final String strSec = placeZeroIfNeede(sec);
        return String.format("%s:%s",strMin,strSec);
    }
    private String placeZeroIfNeede(int number) {
        return (number >=10)? Integer.toString(number): String.format("0%s", Integer.toString(number));
    }
    public void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e("Print", "Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    public <T> void startActivity(Activity startActivity, Class<T> tClass, View callingView,
                                  String transitionName, JSONObject jsonObject) {
        hideKeyboard(startActivity);
        Intent intent = new Intent(startActivity, tClass);
        if (jsonObject != null) {
            try {
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = jsonObject.getString(key);
                    intent.putExtra(key, value);
                }
            } catch (Exception e) {
                Log.e("startActivity", e.getMessage());
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(startActivity, callingView, transitionName);
            startActivity.startActivity(intent, options.toBundle());

        } else {
            startActivity.startActivity(intent);
        }
    }

    public void finishActivity(Activity activity) {
        hideKeyboard(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.finishAfterTransition();
        } else {
            activity.finish();
        }
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void strikeThroughTextView(TextView textView) {
        textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }


    public void setVGTransition(ViewGroup container, View view, boolean visible) {
        view.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    public String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public boolean isValidEmail(CharSequence target) {
        Pattern emailPattern = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
        );
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches() && emailPattern.matcher(target).matches());
    }

    public boolean checkPassword(String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (Character.isDigit(ch)) {
                numberFlag = true;
            } else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if (numberFlag && capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }

    public String getVersionCode(Context context){
        String version="";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
             version = pInfo.versionName;
//             version = pInfo.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
    public void singleButtonDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void openAppInStore(Context context) {
        Intent mIntent = new Intent();
        mIntent.setAction(Intent.ACTION_VIEW);
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        mIntent.setData(Uri.parse("market://details?id=" + appPackageName));
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        try {
            context.startActivity(mIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            mIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
            context.startActivity(mIntent);
        }
    }

    public int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }
    public void setTabBG(int tab1, int tab2, TabLayout tabLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ViewGroup tabStrip = (ViewGroup) tabLayout.getChildAt(0);
            View tabView1 = tabStrip.getChildAt(0);
            View tabView2 = tabStrip.getChildAt(1);
            if (tabView1 != null) {
                int paddingStart = tabView1.getPaddingStart();
                int paddingTop = tabView1.getPaddingTop();
                int paddingEnd = tabView1.getPaddingEnd();
                int paddingBottom = tabView1.getPaddingBottom();
                ViewCompat.setBackground(tabView1, AppCompatResources.getDrawable(tabView1.getContext(), tab1));
                ViewCompat.setPaddingRelative(tabView1, paddingStart, paddingTop, paddingEnd, paddingBottom);
            }
            if (tabView2 != null) {
                int paddingStart = tabView2.getPaddingStart();
                int paddingTop = tabView2.getPaddingTop();
                int paddingEnd = tabView2.getPaddingEnd();
                int paddingBottom = tabView2.getPaddingBottom();
                ViewCompat.setBackground(tabView2, AppCompatResources.getDrawable(tabView2.getContext(), tab2));
                ViewCompat.setPaddingRelative(tabView2, paddingStart, paddingTop, paddingEnd, paddingBottom);
            }
        }
    }


    public void killMediaPlayer(ImageView imageView) {
        try {
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.release();
                    mediaPlayer=null;
                } catch (Exception ignored) {
                }
            }
        }catch (Exception ignored){
        }
    }
    public String reduceValue(Double number){
        DecimalFormat decimalFormat =new DecimalFormat("#.00");
        String numberAsString = decimalFormat.format(number);
        return  numberAsString;
    }
    public String formatTime(long millis) {
        String output = "00:00:00";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 60;
        String secondsD = String.valueOf(seconds);
        String minutesD = String.valueOf(minutes);
        String hoursD = String.valueOf(hours);
        if (seconds < 10)
            secondsD = "0" + seconds;
        if (minutes < 10)
            minutesD = "0" + minutes;
        if (hours < 10)
            hoursD = "0" + hours;
        output = hoursD+":"+minutesD+":"+secondsD;
        return output;
    }
    public void playAudio() {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String recordingDirectory = sdcardPath + "/audioDownload/";
        FileOutputStream fos = null;
        FileInputStream fis1 = null;
        FileInputStream fis2 = null;
        try {
            fos = new FileOutputStream(recordingDirectory + "FileDowloadCombine.mp3");
            fis1 = new FileInputStream(recordingDirectory + "sampleaudio.mp3");
            fis2 = new FileInputStream(recordingDirectory + "sample1.mp3");
            SequenceInputStream sis = new SequenceInputStream(fis1, fis2);
            byte[] buffer = new byte[(1024 * 2)];
            int x = 0;
            while ((x = sis.read(buffer)) != -1) {
                fos.write(buffer, 0, x);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
        public String convertDate(String datePass) {
        String converted_date = "";
        try {
            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(datePass);
            DateFormat currentTFormat = new SimpleDateFormat("d MMM, yyyy");
            currentTFormat.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));
            converted_date =  currentTFormat.format(date);
        }catch (Exception e){ e.printStackTrace();}
        return converted_date;
    }
    public String convertToCurrentTimeZone(String Date) {
        String converted_date = "";
        try {
            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            java.util.Date date = utcFormat.parse(Date);
            DateFormat currentTFormat = new SimpleDateFormat("d MMM, yyyy HH:mm aa");
            currentTFormat.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));
            converted_date =  currentTFormat.format(date);
        }catch (Exception e){ e.printStackTrace();}
        return converted_date;
    }
    private String getCurrentTimeZone(){
        TimeZone tz = Calendar.getInstance().getTimeZone();
        System.out.println(tz.getDisplayName());
        return tz.getID();
    }
    public int getDurationInSeconds(Long ms) {
        int value;
        int seconds;
        int minutes;
        String Sec;
        minutes = (int)(ms / 1000)  / 60;
        seconds = (int)((ms / 1000) % 60);
        Sec = seconds+"";
        return seconds;
    }
    public String getDurationInMinutes(Long ms) {
        int value;
        int seconds;
        int minutes;
        String Sec;
        minutes = (int)(ms / 1000)  / 60;
        seconds = (int)((ms / 1000) % 60);
        Sec = seconds+"";
        return minutes+":"+seconds;
    }

    public String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return  twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    public String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
    static String after(String value, String a) {
        // Returns a substring containing all characters after a string.
        int posA = value.lastIndexOf(a);
        if (posA == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= value.length()) {
            return "";
        }
        return value.substring(adjustedPosA);
    }
    public String getLastString(String value){
        String[] tokens = value.split("/");
        String[] fileNameTokens = tokens[tokens.length - 1].split("\\.");

        String fileNameStr = "";

        for(int i = 0; i < fileNameTokens.length - 1; i++) {
            fileNameStr += fileNameTokens[i] + ".";
        }

        fileNameStr = fileNameStr.substring(0, fileNameStr.length() - 1);
        return fileNameStr;
    }
}