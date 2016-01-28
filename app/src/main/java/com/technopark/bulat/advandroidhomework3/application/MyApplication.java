package com.technopark.bulat.advandroidhomework3.application;

import android.app.Application;

/**
 * Created by bulat on 28.01.16.
 */
public class MyApplication extends Application {
    private static volatile int visibleActivityCount = 0;
    private static boolean isFirstConnect = true;

    public void incrementVisibleActivityCount() {
        ++visibleActivityCount;
    }

    public void decrementVisibleActivityCount() {
        --visibleActivityCount;
    }

    public static int getVisibleActivityCount() {
        return visibleActivityCount;
    }

    public static boolean isFirstConnect() {
        return isFirstConnect;
    }

    public static void setIsFirstConnect(boolean isFirstConnect) {
        MyApplication.isFirstConnect = isFirstConnect;
    }
}
