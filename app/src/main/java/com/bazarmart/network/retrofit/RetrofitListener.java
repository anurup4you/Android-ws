package com.bazarmart.network.retrofit;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Partha Chatterjee on 21/08/2017.
 */

public interface RetrofitListener {
    void onSuccess(Call call, Response response, String method_name, String method);
    void onFailure(String errorMessage);
//    void onError(String errorMessage);
}
