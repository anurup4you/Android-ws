package com.bazarmart.network.retrofit;


import android.content.Context;
import android.content.Intent;

import com.bazarmart.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public class RetroClient {

    public Retrofit retrofit;
    RetrofitListener retroListener;
    String method_name;
    Context mContext;
    Call call;

    public RetroClient(Context mContext, RetrofitListener retroListener) {
        this.mContext = mContext;
        this.retroListener = retroListener;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // Search logcat with OkHttp: for raw response : addInterceptor
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                /*.addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", Paper.book().read(Constants.USER_TOKEN, mContext.getString(R.string.authorise_token))).build();
                    return chain.proceed(request);
                })*/
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public interface RestInterface {

    }

    /**
     * Network Request
     *
     * @param call
     * @param method
     * @param fromMethod
     */
    public void makeHttpRequest(Call call, final String method, final String fromMethod) {
        this.method_name = method;

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
               if (response.isSuccessful()){
                    if (response.code()==200){
                        try {
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            if (jsonObject.optInt("resCode")==401){
                               /* Intent intent=new Intent(AppController.getInstance(), SelectLanguageActivity.class);
                                intent.putExtra("session","expired");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                AppController.applicationContext.startActivity(intent);*/
                            }
                            else {
                                retroListener.onSuccess(call, response, method_name, fromMethod);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

                if (t instanceof NoRouteToHostException) {
                    retroListener.onFailure("Server Unreachable");
                } else if (t instanceof SocketTimeoutException) {
                    retroListener.onFailure("Server Unreachable");
                } else if (t instanceof IOException) {
                    retroListener.onFailure("No Internet");
                } else {
                    retroListener.onFailure(t.getMessage());
                }
            }
        });
    }



}
