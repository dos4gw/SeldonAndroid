package com.highoncode.seldonandroid.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.highoncode.seldonandroid.fragments.*;
import com.highoncode.seldonandroid.R;
import com.highoncode.seldonandroid.utility.IAPHandler;
import com.highoncode.seldonandroid.views.SeldonViewPager;

public class SeldonActivity extends AppCompatActivity implements EmailFragment.OnFragmentInteractionListener, IntroFragment.OnFragmentInteractionListener, DateFragment.OnFragmentInteractionListener, RecordingFragment.OnFragmentInteractionListener, PreviewFragment.OnFragmentInteractionListener {


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
    private SeldonViewPager mViewPager;

    private IAPHandler mIAPHandler;

    private String mRecipientEmail;
    private Date mSendDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seldon);



        mIAPHandler = new IAPHandler(this);
        mIAPHandler.bindService();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (SeldonViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setPagingEnabled(false);
            disableViewPagerSwipe();
        }

        Bundle b = getIntent().getExtras();
        if (b != null) {
            Boolean didStartAgain = b.getBoolean("didStartAgain");
            if (didStartAgain) {
                mViewPager.setCurrentItem(1);
            }
        }
   }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIAPHandler.unbindService();

    }

    // overrides the viewpager so that  the user cannot swipe to change the view
    private void disableViewPagerSwipe() {
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)  {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem());
                return true;
            }
        });
    }


    @Override
    public void onBackPressed() {
        System.out.println("Back pressed");

        if (mViewPager.getCurrentItem() > 0) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, false);
        } else {
            System.out.println("Already at intro - can't go back further.");
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return IntroFragment.newInstance();
                case 1:
                    return EmailFragment.newInstance(mRecipientEmail);
                case 2:
                    return DateFragment.newInstance(mSendDate);
                case 3:
                    return RecordingFragment.newInstance();
                case 4:
                    return PreviewFragment.newInstance(mRecipientEmail, mSendDate);
                default:
                    return IntroFragment.newInstance();
            }
         }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

    }


    // custom methods required for the activity.
    public void goToNextPage() {
        if (mViewPager.getCurrentItem() + 1 == mSectionsPagerAdapter.getCount()) {
            System.out.println("At the end - can't go further.");
        } else {
            System.out.println("Going to next page.");
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, false);
        }
    }

    public void setmRecipientEmail(String newEmail) {
        mRecipientEmail = newEmail;
    }

    public void setmSendDate(Date sendDate) {
        mSendDate = sendDate;
    }


    // IN APP PURCHASE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    IAPHandler.consumePurchase();
                    startSending();
                }
                catch (JSONException e) {

                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("PERMISSION GRANTED");
                } else {
                    System.out.println("PERMISSION NOT GRANTED");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void startSending() {
        DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        String sendDateAsString = df.format(mSendDate);
        Intent intent = new Intent(this, SendingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("sendDateString", sendDateAsString);
        startActivity(intent);
        finish();
    }

}
