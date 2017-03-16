package com.nulldreams.wowpaper.manager;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.nulldreams.base.manager.AbsManager;
import com.nulldreams.wowpaper.modules.PaperResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gaoyunfei on 2017/3/16.
 */

public class ApiManager extends AbsManager {

    private static ApiManager sManager = null;

    public synchronized static ApiManager getInstance (Context context) {
        if (sManager == null) {
            sManager = new ApiManager(context.getApplicationContext());
        }
        return sManager;
    }

    private ApiService mApi;

    private String mApiKey;

    private ApiManager(Context context) {
        super(context);
        try {
            Properties properties = new Properties();
            InputStream is = context.getAssets().open("app.properties");
            properties.load(is);
            mApiKey = properties.getProperty("api_key");
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.addInterceptor(logging);

        GsonBuilder gsonBuilder = new GsonBuilder();

        /*HttpUrl baseUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("wall.alphacoders.com")
                .addPathSegment("api2.0")
                .addPathSegment("get.php")
                .addQueryParameter("auth", api_key)
                .build();*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://wall.alphacoders.com/api2.0/")
                .client(okBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();

        mApi = retrofit.create(ApiService.class);
    }

    public void getNewest (Callback<PaperResult> callback) {
        mApi.getPapers(mApiKey, "newest").enqueue(callback);
    }

}
