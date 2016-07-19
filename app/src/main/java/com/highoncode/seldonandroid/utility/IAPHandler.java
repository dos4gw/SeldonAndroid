package com.highoncode.seldonandroid.utility;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by callum on 28/04/16.
 */
public class IAPHandler implements ServiceConnection {

    private static IInAppBillingService mService;

    private static Context mContext;

    public IAPHandler(Context context) {
        mContext = context;
    }

    public void bindService() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        mContext.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        if (mService != null) {
            mContext.unbindService(this);
        }
    }


    public void getAvailablePurchases() {
        ArrayList<String> skuList = new ArrayList<>();
        skuList.add("seldon.message.send");
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);


        Bundle skuDetails = null;
        try {
            skuDetails = mService.getSkuDetails(3, mContext.getPackageName(), "inapp", querySkus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        ArrayList<String> responseList = null;
        if (skuDetails != null) {
            responseList = skuDetails.getStringArrayList("DETAILS_LIST");
        }
        if (responseList != null) {
            Log.i("Seldon", "getSkuDetails() - \"DETAILS_LIST\" return " + responseList.toString());
        }

        if (responseList.size() == 0) return;

        for (String thisResponse : responseList) {
            try {
                JSONObject object = new JSONObject(thisResponse);

                String sku = object.getString("productId");
                String title = object.getString("title");
                String description = object.getString("description");
                String price = object.getString("price");

                Log.i("Seldon", "getSkuDetails() - \"DETAILS_LIST\":\"productId\" return " + sku);
                Log.i("Seldon", "getSkuDetails() - \"DETAILS_LIST\":\"title\" return " + title);
                Log.i("Seldon", "getSkuDetails() - \"DETAILS_LIST\":\"description\" return " + description);
                Log.i("Seldon", "getSkuDetails() - \"DETAILS_LIST\":\"price\" return " + price);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }




    public static boolean purchaseItem() {

        if (mService != null) {
            try {
                Bundle buyIntentBundle;
                try {
                    buyIntentBundle = mService.getBuyIntent(3, mContext.getPackageName(), "seldon.message.send", "inapp", null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return false;
                }

                if (buyIntentBundle != null) {
                    PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                    if (pendingIntent != null) {
                        ((Activity)mContext).startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
                        return true;
                    } else {
                        return false;
                    }


                } else {
                    return false;
                }
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean consumePurchase() {
        String purchaseToken = "inapp:"+mContext.getPackageName()+":seldon.message.send";
        try {
            int response = mService.consumePurchase(3, mContext.getPackageName(),purchaseToken);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("In app billing", "Service binding success");
        mService = IInAppBillingService.Stub.asInterface(service);
        consumePurchase();
        getAvailablePurchases();
    }

}


