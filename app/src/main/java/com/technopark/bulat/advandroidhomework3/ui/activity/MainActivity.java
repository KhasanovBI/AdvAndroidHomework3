package com.technopark.bulat.advandroidhomework3.ui.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.ui.fragment.ChangeContactInfoFragment;
import com.technopark.bulat.advandroidhomework3.ui.fragment.ContactListFragment;
import com.technopark.bulat.advandroidhomework3.ui.fragment.SettingsFragment;
import com.technopark.bulat.advandroidhomework3.ui.fragment.SplashScreenFragment;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDefaultDisplayHomeAsUpEnabled(true);
        mActionBar.hide();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragments_container, new SplashScreenFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_contact_list:
                Fragment channelListFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_contact_list);
                if (channelListFragment == null) {
                    channelListFragment = new ContactListFragment();
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragments_container, channelListFragment)
                        .commit();
                break;
            case R.id.nav_options:
                Fragment settingsFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_settings);
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragments_container, settingsFragment)
                        .commit();
                break;
            case R.id.nav_change_user_data:
                Fragment changeContactInfoFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_change_contact_info);
                if (changeContactInfoFragment == null) {
                    changeContactInfoFragment = new ChangeContactInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(ChangeContactInfoFragment.IS_FROM_DRAWER, true);
                    changeContactInfoFragment.setArguments(bundle);
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragments_container, changeContactInfoFragment)
                        .commit();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }


    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public void unsetFullScreenFlag() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
