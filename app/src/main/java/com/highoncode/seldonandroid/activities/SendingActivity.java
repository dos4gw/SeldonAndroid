package com.highoncode.seldonandroid.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;


import com.highoncode.seldonandroid.R;
import com.highoncode.seldonandroid.fragments.SendingFragment;
import com.highoncode.seldonandroid.fragments.SuccessFragment;

public class SendingActivity extends AppCompatActivity implements SendingFragment.OnFragmentInteractionListener, SuccessFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private String mSendDateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending);


        Bundle b = getIntent().getExtras();
        mSendDateString = b.getString("sendDateString");

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


    }


    public void goToNextPage() {
        if (mViewPager.getCurrentItem() + 1 == mSectionsPagerAdapter.getCount()) {
            System.out.println("At the end - can't go further.");
        } else {
            System.out.println("Going to next page.");
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {

    }

    public void closeSeldon(View view) {
        moveTaskToBack(true);
        finish();
    }


    public void startAgain(View view) {
        Intent intent = new Intent(this, SeldonActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("didStartAgain", true);
        startActivity(intent);
        finish();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SendingFragment.newInstance();
                case 1:
                    return SuccessFragment.newInstance(mSendDateString);
                default:
                    return SuccessFragment.newInstance(mSendDateString);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
