package africa.collect.android.Utils;

import android.content.Context;

import com.segment.analytics.Properties;

public class Analytics {

    public void Track(Context context,  String msg, String key, String val){
        com.segment.analytics.Analytics.with(context).track(msg, new Properties().putValue(key, val ).putValue("source", "android"));
    }
}
