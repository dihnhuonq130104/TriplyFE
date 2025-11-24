package com.example.triply.data.remote;

import android.content.Context;
import android.util.Log;

import com.example.triply.util.TokenManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "http://192.168.0.13:8080";
    private static Retrofit retrofitInstance;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getInstance() {
        if (retrofitInstance == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitInstance == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, message));
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    Interceptor authInterceptor = new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request originalRequest = chain.request();
                            
                            if (appContext != null) {
                                TokenManager tokenManager = new TokenManager(appContext);
                                String token = tokenManager.getAccessToken();
                                
                                if (token != null) {
                                    Request newRequest = originalRequest.newBuilder()
                                            .header("Authorization", "Bearer " + token)
                                            .build();
                                    return chain.proceed(newRequest);
                                }
                            }
                            
                            return chain.proceed(originalRequest);
                        }
                    };

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                            .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                            .addInterceptor(authInterceptor)
                            .addInterceptor(logging)
                            .build();

                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    retrofitInstance = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                }
            }
        }
        return retrofitInstance;
    }

    public static ApiService api() {
        return getInstance().create(ApiService.class);
    }
}


