package com.native_code.bp_project02;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TextViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }


    public TextViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) { return mFragmentList.get(position); }

    @Override
    public int getCount() {
        return mFragmentList.size() ;
    }
}