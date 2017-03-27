package com.nulldreams.wowpaper.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Formatter;

/**
 * Created by gaoyunfei on 2017/3/17.
 */

public class Paper implements Parcelable{

    public int id, width, height;
    public String file_type, url_image, url_thumb, url_page;
    public long file_size;

    public int category_id, sub_category_id, user_id, collection_id, group_id;
    public String name, category, sub_category, user_name, collection, group;


    protected Paper(Parcel in) {
        id = in.readInt();
        width = in.readInt();
        height = in.readInt();
        file_type = in.readString();
        url_image = in.readString();
        url_thumb = in.readString();
        url_page = in.readString();
        file_size = in.readLong();
        category_id = in.readInt();
        sub_category_id = in.readInt();
        user_id = in.readInt();
        collection_id = in.readInt();
        group_id = in.readInt();
        name = in.readString();
        category = in.readString();
        sub_category = in.readString();
        user_name = in.readString();
        collection = in.readString();
        group = in.readString();
    }

    public static final Creator<Paper> CREATOR = new Creator<Paper>() {
        @Override
        public Paper createFromParcel(Parcel in) {
            return new Paper(in);
        }

        @Override
        public Paper[] newArray(int size) {
            return new Paper[size];
        }
    };

    public String getThumb350 () {
        StringBuilder builder = new StringBuilder(url_thumb);
        int index = builder.indexOf("-");
        builder.insert(index, "-350");
        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(file_type);
        dest.writeString(url_image);
        dest.writeString(url_thumb);
        dest.writeString(url_page);
        dest.writeLong(file_size);
        dest.writeInt(category_id);
        dest.writeInt(sub_category_id);
        dest.writeInt(user_id);
        dest.writeInt(collection_id);
        dest.writeInt(group_id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(sub_category);
        dest.writeString(user_name);
        dest.writeString(collection);
        dest.writeString(group);
    }

    public String getInfo (Context context) {
        return width + "×" + height + "  " + Formatter.formatFileSize(context, file_size) + "  " + file_type;
    }
}
