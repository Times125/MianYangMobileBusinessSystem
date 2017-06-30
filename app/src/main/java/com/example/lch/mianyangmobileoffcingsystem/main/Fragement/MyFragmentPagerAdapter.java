package com.example.lch.mianyangmobileoffcingsystem.main.Fragement;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.List;

/**
 * Created by lch on 2017/3/7.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;

    public MyFragmentPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList) {
        super(fragmentManager);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
