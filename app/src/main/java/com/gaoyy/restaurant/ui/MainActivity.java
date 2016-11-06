package com.gaoyy.restaurant.ui;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.base.BaseActivity;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
    private DrawerLayout mainDrawerLayout;
    private NavigationView mainNavView;
    private Toolbar mainToolbar;
    private RelativeLayout mainContent;
    private Button mainDeliveryBtn;
    private Button mainCheckBtn;


    @Override
    protected void initContentView()
    {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initToolbar()
    {
        super.initToolbar(mainToolbar, R.string.app_name, false, null);
    }

    @Override
    protected void assignViews()
    {
        super.assignViews();
        mainDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mainNavView = (NavigationView) findViewById(R.id.main_nav_view);
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainContent = (RelativeLayout) findViewById(R.id.main_content);
        mainDeliveryBtn = (Button) findViewById(R.id.main_delivery_btn);
        mainCheckBtn = (Button) findViewById(R.id.main_check_btn);
    }

    @Override
    protected void configViews()
    {
        super.configViews();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mainDrawerLayout, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    protected void setListener()
    {
        super.setListener();
        mainNavView.setNavigationItemSelectedListener(this);
        mainDeliveryBtn.setOnClickListener(this);
        mainCheckBtn.setOnClickListener(this);

    }

    @Override
    public void onBackPressed()
    {
        if (mainDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mainDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        super.setStatusBarColor(-1);
        super.setNavigationBarColor(-1);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera)
        {
            // Handle the camera action
        }
        else if (id == R.id.nav_gallery)
        {

        }
        else if (id == R.id.nav_slideshow)
        {

        }
        else if (id == R.id.nav_manage)
        {

        }
        else if (id == R.id.nav_share)
        {

        }
        else if (id == R.id.nav_send)
        {

        }

        mainDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
//        Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants
//                (MainActivity.this, false, new Pair<>(view, "toolbar"));
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, pairs);
        switch (id)
        {
            case R.id.main_delivery_btn:
                redirect(DeliveryActivity.class);
//                redirectWithShareViews(DeliveryActivity.class,options);
                break;
            case R.id.main_check_btn:
                redirect(CheckActivity.class);
                break;
        }
    }
}
