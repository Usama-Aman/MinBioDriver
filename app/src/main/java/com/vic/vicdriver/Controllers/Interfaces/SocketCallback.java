package com.vic.vicdriver.Controllers.Interfaces;

import org.json.JSONObject;

public interface SocketCallback {

    void onSuccess(JSONObject jsonObject, String tag);
    void onFailure(String message, String tag);
}
