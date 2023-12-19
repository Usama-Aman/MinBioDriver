package com.vic.vicdriver.Controllers.Helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vic.vicdriver.Views.Fragments.AvailableOrders;
import com.vic.vicdriver.Views.Fragments.DeliveredOrders;
import com.vic.vicdriver.Views.Fragments.OngoingOrders;

public class MyPagerAdapter extends FragmentPagerAdapter {

    //View pager adapter for the order screen

    private String[] titles;

    public MyPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AvailableOrders();
            case 1:
                return new OngoingOrders();
            case 2:
                return new DeliveredOrders();
            default:
                break;
        }
        return null;
    }
}