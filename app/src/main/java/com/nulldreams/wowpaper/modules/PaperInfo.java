package com.nulldreams.wowpaper.modules;

import android.os.Parcel;

/**
 * Created by boybe on 2017/3/17.
 */

public class PaperInfo extends Paper{

    public int category_id, sub_category_id, user_id, collection_id, group_id;
    public String name, category, sub_category, user_name, collection, group;

    protected PaperInfo(Parcel in) {
        super(in);
    }
}
