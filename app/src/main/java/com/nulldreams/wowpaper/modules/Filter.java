package com.nulldreams.wowpaper.modules;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by boybe on 2017/3/17.
 */
@Table(name = "filters")
public class Filter implements Parcelable {

    public @Column(name = "id", isId = true, autoGen = false) int id;
    private @Column(name = "count") int count;
    public @Column(name = "name") String name;
    public @Column(name = "url") String url;

    public Filter () {

    }

    protected Filter(Parcel in) {
        id = in.readInt();
        count = in.readInt();
        name = in.readString();
        url = in.readString();
    }

    public Filter(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static final Creator<Filter> CREATOR = new Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel in) {
            return new Filter(in);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Filter) {
            return ((Filter) obj).id == id;
        }
        return false;
    }
}
