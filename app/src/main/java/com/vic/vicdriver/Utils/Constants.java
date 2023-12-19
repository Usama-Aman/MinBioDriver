package com.vic.vicdriver.Utils;

import android.Manifest;

public class Constants {

    public static boolean isAppRunning = false;

    public static String socketUrl = "http://elxdrive.com:3000";


    public static String isFirstTime = "isFirstTime";

    public static String userId = "userId";
    public static String userEmail = "userEmail";
    public static String userPhone = "userPhone";
    public static String companyPhone= "companyPhone";
    public static String countryCode = "countryCode";
    public static String fingerPrintUser = "fingerPrintUser";
    public static String userpassword = "userPassword";
    public static String accessToken = "accessToken";
    public static String refreshToken = "refreshToken";
    public static String isLoggedIn = "isLoggedIn";
    public static String isBiometric = "isBiometric";
    public static String isFirstLogin = "isFirstLogin";
    public static String currency = "currency";
    public static String delivery_arrival_notify_circle = "delivery_arrival_notify_circle";
    public static String vat = "vat";
    public static String isMerchant = "isMerchant";
    public static String french = "fr";
    public static String english = "en";
    public static String language = "language";


    public static String channelName = "MINBIODRIVER";
    public static int notificationId = 786786;

    public static String deviceId = "deviceId";
    public static String fcmToken = "fcmToken";

    public static String WithInRange = "WithInRange";

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public static long mLastClickTime = 0;

//    keys ....

    public static String isNavigationStarted = "IsNavigationStarted";
    public static String navigatedOrderNumber = "NavigatedOrderNumber";
    public static String isAnotherOrder = "isAnotherOrder";


    public static String UserLat = "UserLng";
    public static String UserLng = "UserLat";
    public static String UserIdForNavigation = "UserId";
    public static String DriverIdForNavigation = "DriverId";
    public static String OrderNumber = "OrderNumber";

    /*Notification Types*/
    public static String MessageReceivedFromBuyer= "MessageReceivedFromBuyer";
    public static String MessageReceivedFromSeller= "MessageReceivedFromSeller";
    public static String NewOrderCreated= "NewOrderCreated";
    public static String AdminCommentOnSupport = "AdminCommentOnSupport";


    public static final int IMAGE_REQUEST_CODE = 2001;
    public static final int AUDIO_REQUEST_CODE = 2002;


    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

}
