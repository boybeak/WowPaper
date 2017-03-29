package com.nulldreams.wowpaper.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Formatter;

import com.google.gson.Gson;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by gaoyunfei on 2017/3/17.
 */
@Table(name = "paper")
public class Paper implements Parcelable{

    @Column(name = "id", isId = true, autoGen = false) public int id;
    @Column(name = "width") public int width;
    @Column(name = "height") public int height;
    @Column(name = "file_type") public String file_type;
    @Column(name = "url_image") public String url_image;
    @Column(name = "url_thumb") public String url_thumb;
    @Column(name = "url_page") public String url_page;
    @Column(name = "file_size") public long file_size;

    @Column(name = "category_id") public int category_id;
    @Column(name = "sub_category_id") public int sub_category_id;
    @Column(name = "user_id") public int user_id;
    @Column(name = "collection_id") public int collection_id;
    @Column(name = "group_id") public int group_id;
    @Column(name = "name") public String name;
    @Column(name = "category") public String category;
    @Column(name = "sub_category") public String sub_category;
    @Column(name = "user_name") public String user_name;
    @Column(name = "collection") public String collection;
    @Column(name = "group") public String group;

    public Paper () {

    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public String getUrl_thumb() {
        return url_thumb;
    }

    public void setUrl_thumb(String url_thumb) {
        this.url_thumb = url_thumb;
    }

    public String getUrl_page() {
        return url_page;
    }

    public void setUrl_page(String url_page) {
        this.url_page = url_page;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(int sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(int collection_id) {
        this.collection_id = collection_id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Paper) {
            return ((Paper) obj).id == id;
        }
        return false;
    }
}
