package com.nulldreams.wowpaper.modules;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by boybe on 2017/3/17.
 */

public class SubCategoryResult extends Result {
    @SerializedName("sub-categories")
    public ArrayList<Filter> sub_categories;
}
