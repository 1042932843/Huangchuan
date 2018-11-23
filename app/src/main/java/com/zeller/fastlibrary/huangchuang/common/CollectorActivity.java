package com.zeller.fastlibrary.huangchuang.common;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/18 0018.
 */
public class CollectorActivity {

    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity){

        activities.add(activity);
    }

    public static void removeActivity(Activity activity){

        activities.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity : activities){
            if (!activity.isFinishing()){

                activity.finish();
            }
        }
    }
}
