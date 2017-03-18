package com.nulldreams.wowpaper.manager;

import com.nulldreams.wowpaper.modules.CategoryResult;
import com.nulldreams.wowpaper.modules.PaperInfoResult;
import com.nulldreams.wowpaper.modules.PaperResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by gaoyunfei on 2017/3/16.
 */

public interface ApiService {

    @GET("get.php")
    Call<PaperResult> getPapers (@Query("auth") String auth, @Query("method") String method, @Query("page") int page);

    @GET("get.php")
    Call<CategoryResult> getCategories (@Query("auth") String auth, @Query("method") String method);

    @GET("get.php?method=wallpaper_info")
    Call<PaperInfoResult> getPaperInfo (@Query("auth") String auth, @Query("id") int id);
}
