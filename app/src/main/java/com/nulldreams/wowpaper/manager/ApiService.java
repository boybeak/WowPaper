package com.nulldreams.wowpaper.manager;

import com.nulldreams.wowpaper.modules.CategoryResult;
import com.nulldreams.wowpaper.modules.CollectionResult;
import com.nulldreams.wowpaper.modules.GroupResult;
import com.nulldreams.wowpaper.modules.PaperInfoResult;
import com.nulldreams.wowpaper.modules.PaperResult;
import com.nulldreams.wowpaper.modules.SubCategoryResult;

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
    Call<PaperResult> getPapersWithId (@Query("auth") String auth, @Query("method") String method, @Query("id") int id, @Query("page") int page);

    @GET("get.php?method=category_list")
    Call<CategoryResult> getCategories(@Query("auth") String auth);

    @GET("get.php?method=collection_list")
    Call<CollectionResult> getCollections(@Query("auth") String auth);

    @GET("get.php?method=group_list")
    Call<GroupResult> getGroups(@Query("auth") String auth);

    @GET("get.php?method=sub_category_list")
    Call<SubCategoryResult> getSubCategories(@Query("auth") String auth, @Query("id") int id);

    @GET("get.php?method=wallpaper_info")
    Call<PaperInfoResult> getPaperInfo (@Query("auth") String auth, @Query("id") int id);
}
