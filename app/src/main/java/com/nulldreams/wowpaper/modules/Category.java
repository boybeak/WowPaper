package com.nulldreams.wowpaper.modules;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by boybe on 2017/3/17.
 */

public class Category implements Parcelable {

    public int id, count;
    public String name, url;

    protected Category(Parcel in) {
        id = in.readInt();
        count = in.readInt();
        name = in.readString();
        url = in.readString();
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(count);
        dest.writeString(name);
        dest.writeString(url);
    }
}
