package com.historygram.api.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.historygram.api.exceptions.ApiCodes;
import com.historygram.api.exceptions.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ApiErrorUtils {

    public static boolean hasSimpleError(String response) {
        try {
            return JsonUtils.containsElement(response, "error");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @NonNull
    public static ApiException toSimpleError(String errorJson, String method) throws ApiException {
        try {
            return parseSimpleError(new JSONObject(errorJson), method);
        } catch (JSONException e) {
            throw new ApiException("JSONException: " + e.getMessage());
        }
    }

    private static ApiException parseSimpleError(JSONObject errorJson, String method) throws JSONException {
        return parse(errorJson, method);
    }

    private static ApiException parse(JSONObject json, String method) {
        int code = json.optInt("status");
        String errorMsg = json.optString("message");
        return new ApiException("Error (" + code + "): " + errorMsg + " in method " + method);
    }

}
