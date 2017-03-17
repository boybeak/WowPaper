package com.nulldreams.wowpaper.modules;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gaoyunfei on 2017/3/17.
 */

public class Paper implements Parcelable{

    public int id, width, height;
    public String file_type, url_image, url_thumb, url_page;
    public long file_size;

    protected Paper(Parcel in) {
        id = in.readInt();
        width = in.readInt();
        height = in.readInt();
        file_type = in.readString();
        url_image = in.readString();
        url_thumb = in.readString();
        url_page = in.readString();
        file_size = in.readLong();
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
    }

    public String getThumb350 () {
        StringBuilder builder = new StringBuilder(url_thumb);
        int index = builder.indexOf("-");
        builder.insert(index, "-350");
        return builder.toString();
    }
}
