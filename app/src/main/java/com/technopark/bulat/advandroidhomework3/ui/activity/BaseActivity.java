package com.technopark.bulat.advandroidhomework3.ui.activity;

import android.support.v7.app.AppCompatActivity;

import com.technopark.bulat.advandroidhomework3.application.MyApplication;

/**
 * Created by bulat on 28.01.16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected MyApplication getMyApplication() {
        return (MyApplication) getApplication();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMyApplication().incrementVisibleActivityCount();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getMyApplication().decrementVisibleActivityCount();
    }
}
