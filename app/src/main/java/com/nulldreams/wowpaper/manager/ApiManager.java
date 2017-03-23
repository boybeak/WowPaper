package com.nulldreams.wowpaper.manager;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.nulldreams.base.manager.AbsManager;
import com.nulldreams.wowpaper.modules.CategoryResult;
import com.nulldreams.wowpaper.modules.CollectionResult;
import com.nulldreams.wowpaper.modules.CountResult;
import com.nulldreams.wowpaper.modules.GroupResult;
import com.nulldreams.wowpaper.modules.Paper;
import com.nulldreams.wowpaper.modules.PaperInfoResult;
import com.nulldreams.wowpaper.modules.PaperResult;
import com.nulldreams.wowpaper.modules.SubCategoryResult;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gaoyunfei on 2017/3/16.
 */

public class ApiManager extends AbsManager {

    public static final String WOW_TYPE_NEWEST = "newest",
            WOW_TYPE_HIGHEST_RATED = "highest_rated";

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

    public void getWallpapers(int page, String type, Callback<PaperResult> callback) {
        mApi.getPapers(mApiKey, type, page).enqueue(callback);
    }

    public void getWallpapersWithId (String type, int id, int page, Callback<PaperResult> callback) {
        mApi.getPapersWithId(mApiKey, type, id, page).enqueue(callback);
    }

    public void getPaperInfo (int id, Callback<PaperInfoResult> callback) {
        mApi.getPaperInfo(mApiKey, id).enqueue(callback);
    }

    public void getCategories(Callback<CategoryResult> callback) {
        mApi.getCategories(mApiKey).enqueue(callback);
    }

    public void getSubCategories(int id, Callback<SubCategoryResult> callback) {
        mApi.getSubCategories(mApiKey, id).enqueue(callback);
    }

    public void getCollections(Callback<CollectionResult> callback) {
        mApi.getCollections(mApiKey).enqueue(callback);
    }

    public void getGroups(Callback<GroupResult> callback) {
        mApi.getGroups(mApiKey).enqueue(callback);
    }

    public void getQueryCount (Callback<CountResult> callback) {
        mApi.getQueryCount(mApiKey).enqueue(callback);
    }

    public void downloadPaper (Paper paper, String outputPath) {
        URL website;
        try {
            website = new URL(paper.url_image);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(outputPath);
            fos.getChannel().transferFrom(rbc, 0, paper.file_size);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
