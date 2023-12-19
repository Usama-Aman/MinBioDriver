package com.vic.vicdriver.Network;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vic.vicdriver.Models.Response.Login.Login;
import com.vic.vicdriver.R;
import com.vic.vicdriver.Utils.Common;
import com.vic.vicdriver.Utils.Constants;
import com.vic.vicdriver.Utils.SharedPreference;
import com.vic.vicdriver.Views.Activities.Driver2Buyer;
import com.vic.vicdriver.Views.Activities.Driver2Seller;
import com.vic.vicdriver.Views.Activities.MyOrdersActivity;
import com.vic.vicdriver.Views.Activities.Splash;
import com.vic.vicdriver.Views.Activities.Support.SupportChat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

import static androidx.constraintlayout.widget.Constraints.TAG;


@SuppressLint("Registered")
public class FCMService extends FirebaseMessagingService {

    String GROUP_KEY = "com.elementary.minbio";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);


        Common.fcmDevices(getApplicationContext(),
                SharedPreference.getSimpleString(getApplicationContext(), Constants.deviceId), s.toString(), "driver");

    }

    //Function called when new message is received
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = "", body = "", orderId = "", type = "", price = "", quantity = "";

        String messageData = "";


        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body:" + remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {


            try {

                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(remoteMessage.getData());
                try {
                    if (object.has("body")) {
                        body = object.getString("body");
                    }
                    if (object.has("title")) {
                        title = object.getString("title");
                    }
                    if (object.has("type")) {
                        type = object.getString("type");
                    }
                    if (object.has("order_id")) {
                        orderId = object.getString("order_id");
                    }
                    if (object.has("message")) {
                        messageData = object.getString("message");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        showMessage(type, orderId, body, title, messageData);

    }

    //"Showing the notification
    private void showMessage(String type, String orderId, String body, String title, String messageData) {
        boolean toShowNotification;

        Intent resultIntent;

        if (Constants.isAppRunning && SharedPreference.getSimpleString(this, Constants.isLoggedIn).equals("true")) {

            resultIntent = new Intent(this, MyOrdersActivity.class);
            toShowNotification = true;

            if (type.equals(Constants.MessageReceivedFromBuyer) || type.equals(Constants.MessageReceivedFromSeller) || type.equals(Constants.AdminCommentOnSupport)) {

                if (Driver2Buyer.isd2bChatActive) {
                    toShowNotification = Integer.parseInt(orderId) != Driver2Buyer.driverBuyerOrderId;

                    Intent chatIntent = new Intent("Driver2BuyerBroadCast");
                    chatIntent.putExtra("messageData", messageData);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(chatIntent);
                } else if (Driver2Seller.isd2sChatActive) {
                    toShowNotification = Integer.parseInt(orderId) != Driver2Seller.driverSellerOrderId;

                    Intent chatIntent = new Intent("Driver2SellerBroadCast");
                    chatIntent.putExtra("messageData", messageData);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(chatIntent);
                } else if (SupportChat.isSupportChatActive) {

                    toShowNotification = Integer.parseInt(orderId) != SupportChat.supportId;

                    Intent chatIntent = new Intent("SupportChatBroadCast");
                    chatIntent.putExtra("messageData", messageData);
                    chatIntent.putExtra("supportId", orderId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(chatIntent);

                } else if (type.equals(Constants.AdminCommentOnSupport) && !SupportChat.isSupportChatActive) {

                    resultIntent = new Intent(this, SupportChat.class);
                    toShowNotification = true;
                }
            }
        } else if (SharedPreference.getSimpleString(this, Constants.isLoggedIn).equals("false")) {
            resultIntent = new Intent(this, Login.class);
            toShowNotification = true;
        } else {
            resultIntent = new Intent(this, Splash.class);
            toShowNotification = true;
        }

        resultIntent.putExtra("supportId", orderId);
        resultIntent.putExtra("isComingFromNotification", true);
        resultIntent.putExtra("message", messageData);
        resultIntent.putExtra("type", type);
        resultIntent.putExtra("orderId", orderId);
        resultIntent.putExtra("body", body);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), new Random().nextInt(), resultIntent, 0);

        if (toShowNotification)
            sendNotification(title, body, resultPendingIntent);

    }

    private void sendNotification(String title, String body, PendingIntent resultPendingIntent) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(Constants.channelName, Constants.channelName,
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        // to display notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(Constants.channelName);
            channel.canBypassDnd();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.channelName);

        notificationBuilder
                .setContentTitle(title)
                .setContentText(body)
                .setGroup(Constants.channelName)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.noti)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setContentIntent(resultPendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (notificationChannel != null) {
                notificationBuilder.setChannelId(Constants.channelName);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
        mNotificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }

}
