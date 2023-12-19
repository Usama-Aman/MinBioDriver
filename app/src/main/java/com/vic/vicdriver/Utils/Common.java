package com.vic.vicdriver.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.vic.vicdriver.Controllers.Interfaces.Api;
import com.vic.vicdriver.Models.CartModel;
import com.vic.vicdriver.Models.Response.Login.Data;
import com.vic.vicdriver.Models.Response.bank_detail.BankDetailModel;
import com.vic.vicdriver.Models.Response.orders_pack.Datum;
import com.vic.vicdriver.Network.RetrofitClient;
import com.vic.vicdriver.R;
import com.google.gson.Gson;
import com.irozon.sneaker.Sneaker;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Common {

    private static ProgressDialog dialog;
    public static boolean isNegoLoaded = false;

    //Function to hide the keyboard if open
    public static void hideKeyboard(Activity activity) {

        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }

            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void hideKeyboardFromDialog(Activity activity, EditText editText) {

        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }

            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Function to check whether the email is valid or not
    public static boolean isValidEmailId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    // Static function to show the loading dialog
    public static void showDialog(Context context) {
        try {
            hideKeyboard(((AppCompatActivity) context));
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle(context.getResources().getString(R.string.loading));
            dialog.setMessage(context.getResources().getString(R.string.loading_please_wait));
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DIssmisses the dialog
    public static void dissmissDialog() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // round the double value to 2 digits
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    // Function to check whether the keyboard is shown or not
    public static boolean keyboardShown(View rootView) {

        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    //Call the api to register the device on the server for notifications
    public static void fcmDevices(Context context, String deviceId, String fcmToken, String userType) {

        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> call = api.sendDeviceToken("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), fcmToken, "Android", "development", deviceId, userType);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        Log.d(TAG, "onResponse: " + jsonObject.getString("message"));
                    } else {

                        jsonObject = new JSONObject(response.errorBody().string());
                        Log.d(TAG, "onResponse: " + jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    public static double getTotal(ArrayList<CartModel> cartList) {
        double priceTotal = 0;

        for (int i = 0; i < cartList.size(); i++) {
            priceTotal += cartList.get(i).getQuantity() * Double.parseDouble(cartList.get(i).getPrice());
        }

        return priceTotal;
    }


    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String convertInKFormat(String price) {

        long value = (long) Double.parseDouble(price);

        try {
            //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
            if (value == Long.MIN_VALUE)
                return convertInKFormat(String.valueOf(Long.MIN_VALUE + 1));

            if (value < 0) return "-" + convertInKFormat(String.valueOf(-value));

//            if (value < 1000) return Long.toString(value); //deal with easy case

            if (value < 1000)
                return String.valueOf(round(Double.parseDouble(price), 2));

            Map.Entry<Long, String> e = suffixes.floorEntry(value);
            Long divideBy = e.getKey();
            String suffix = e.getValue();

            long truncated = value / (divideBy / 10); //the number part of the output times 10
            boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
//            return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
            return (truncated / 10d) + suffix;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void showToast(Activity activity, String message, boolean isSuccess) {

        if (isSuccess)
            Sneaker.with(activity)
                    .setTitle(activity.getApplicationContext().getResources().getString(R.string.success))
                    .setDuration(2000)
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setMessage(message)
                    .sneakSuccess();
        else
            Sneaker.with(activity)
                    .setTitle(activity.getApplicationContext().getResources().getString(R.string.error))
                    .setDuration(2000)
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setMessage(message)
                    .sneakError();


    }


    public static Data fromJson(Context context) {
        Gson gson = new Gson();
        String json = SharedPreference.getSimpleString(context, "MyObject");
        Data obj = gson.fromJson(json, Data.class);
        return obj;
    }

    public static void toJson(Context context, Data data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        SharedPreference.saveSimpleString(context, "MyObject", json);

    }


    public static BankDetailModel bankDetailfromJson(Context context) {
        Gson gson = new Gson();
        String json = SharedPreference.getSimpleString(context, "bankdetail");
        BankDetailModel obj = gson.fromJson(json, BankDetailModel.class);
        return obj;
    }

    public static void bankDetailtoJson(Context context, BankDetailModel data) {
//        Data myObject = new Data();

        Gson gson = new Gson();
        String json = gson.toJson(data);
        SharedPreference.saveSimpleString(context, "bankdetail", json);

    }


    public static Datum orderModelfromJson(Context context) {
        Gson gson = new Gson();
        String json = SharedPreference.getSimpleString(context, "ordermodel");
        Datum obj = gson.fromJson(json, Datum.class);
        return obj;
    }

    public static void orderModeltoJson(Context context, Datum data) {
//        Data myObject = new Data();

        Gson gson = new Gson();
        String json = gson.toJson(data);
        SharedPreference.saveSimpleString(context, "ordermodel", json);

    }

    public static Datum orderModelfromJsonSpecific(Context context) {
        Gson gson = new Gson();
        String json = SharedPreference.getSimpleString(context, "orderModelSpecific");
        Datum obj = gson.fromJson(json, Datum.class);
        return obj;
    }

    public static void orderModeltoJsonSpecific(Context context, Datum data) {
//        Data myObject = new Data();

        Gson gson = new Gson();
        String json = gson.toJson(data);
        SharedPreference.saveSimpleString(context, "orderModelSpecific", json);

    }

    /*******************************
     * Method for network is in working state or not.
     ******************************/
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }

    /****************************************
     * Get "String" to check is it null or not
     ****************************************/
    public static boolean isSet(String string) {
        if (string != null && string.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Calendar convertStrDateToCalendar(String strDate) {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            cal.setTime(sdf.parse(strDate));
            return cal;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String formatDateFromDate(String dt) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d", Locale.ENGLISH);
            Date date = simpleDateFormat.parse(dt);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String msgDate = sdf.format(date);
            return msgDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public static String formatDateFromDateTime(String dt) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            Date date = simpleDateFormat.parse(dt);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
            String msgDate = sdf.format(date);
            return msgDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }
}
