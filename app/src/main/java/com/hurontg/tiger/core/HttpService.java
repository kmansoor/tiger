package com.hurontg.tiger.core;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hurontg.tiger.domain.ErrorResponse;
import com.hurontg.tiger.domain.Parent;
import com.hurontg.tiger.domain.ParentStatusResponse;
import com.hurontg.tiger.domain.RealtimeLocation;
import com.hurontg.tiger.domain.TokenResponse;
import com.hurontg.tiger.util.Constants;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpService {
  public static final String TAG = "HttpUtil";

  private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  private OkHttpClient client = new OkHttpClient();

  public HttpService() {
    RxJavaPlugins.setErrorHandler(throwable -> {
      Log.e(TAG, throwable.getClass().getName());             // io.reactivex.exceptions.OnErrorNotImplementedException
      Log.e(TAG, throwable.getCause().getClass().getName());  // java.lang.Exception
      Log.e(TAG, throwable.getMessage());                     // "Test"
      throwable.printStackTrace();
    });
  }

  public Observable<Boolean> postLocationData(JSONObject postData) {
    return Observable.fromCallable(() -> {
      RequestBody body = RequestBody.create(JSON, postData.toString());
      Request request = new Request.Builder()
          .url(Constants.PARENT_LOCATION_URL)
          .post(body)
          .build();

      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) return true;
        throw new RuntimeException(response.message());
      }
    });
  }

  public Observable<List<RealtimeLocation>> getLocationData(String token) {
    return Observable.fromCallable(() -> {
      Request request = new Request.Builder()
          .url(Constants.PARENT_LOCATION_URL)
          .addHeader("x-access-token", token)
          .build();

      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
          String jsonData = response.body().string();

          Type listType = new TypeToken<ArrayList<RealtimeLocation>>(){}.getType();
          List<RealtimeLocation> location = new Gson().fromJson(jsonData, listType);
          return location;
        } else {
          throw new RuntimeException(response.message());
        }
      }
    });
  }

  public Observable<TokenResponse> postParent(JSONObject postData) {
    return Observable.fromCallable(() -> {
      RequestBody body = RequestBody.create(JSON, postData.toString());
      Request request = new Request.Builder()
          .url(Constants.PARENT_SERVER_URL)
          .post(body)
          .build();

      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
          String jsonData = response.body().string();
          TokenResponse parentResponse = new Gson().fromJson(jsonData, TokenResponse.class);
          return parentResponse;
        }
        throw new RuntimeException(response.message());
      }
    });
  }

  public Observable<ParentStatusResponse> getParentStatus(String token) {
    return Observable.fromCallable(() -> {
      Request request = new Request.Builder()
          .url(Constants.PARENT_STATUS_URL + token)
          .build();

      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
          String jsonData = response.body().string();
          return new Gson().fromJson(jsonData, ParentStatusResponse.class);
        } else {
          throw new RuntimeException(response.message());
        }
      }
    });
  }

  public Observable<TokenResponse> postStaff(JSONObject postData) {
    return Observable.fromCallable(() -> {
      RequestBody body = RequestBody.create(JSON, postData.toString());
      Request request = new Request.Builder()
          .url(Constants.STAFF_SERVER_URL)
          .post(body)
          .build();

      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
          String jsonData = response.body().string();
          TokenResponse staffResponse = new Gson().fromJson(jsonData, TokenResponse.class);
          return staffResponse;
        }
        String errorJson = response.body().string();
        ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);
        throw new RuntimeException(errorResponse.getErrors());
      }
    });
  }

  public Observable<LinkedList<Parent>> loadPendingActivationClients() {
    return Observable.fromCallable(() -> {
      Request request = new Request.Builder()
          .url(Constants.ADMIN_PENDING_ACTIVATION)
          .build();

      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
          String jsonData = response.body().string();

          Type listType = new TypeToken<LinkedList<Parent>>(){}.getType();
          LinkedList<Parent> parents = new Gson().fromJson(jsonData, listType);
          return parents;
        } else {
          throw new RuntimeException(response.message());
        }
      }
    });
  }

  public Observable<Boolean> setParentApplictaionStatus(String status, String uuid) {
    return Observable.fromCallable(() -> {
      JSONObject postData = new JSONObject();
      postData.put("status", status);
      postData.put("uuid", uuid);

      RequestBody body = RequestBody.create(JSON, postData.toString());
      Request request = new Request.Builder()
          .url(Constants.ADMIN_APPLICATION_STATUS)
          .post(body)
          .build();

      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
          return true;
        }
        String errorJson = response.body().string();
        ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);
        throw new RuntimeException(errorResponse.getErrors());
      }
    });
  }
}