package com.dev.aurora.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class DailyNewsBean implements Parcelable {
    private int postId;
    private List<NewsItem> newsItemList;

    protected DailyNewsBean(Parcel in) {
        postId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(postId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DailyNewsBean> CREATOR = new Creator<DailyNewsBean>() {
        @Override
        public DailyNewsBean createFromParcel(Parcel in) {
            return new DailyNewsBean(in);
        }

        @Override
        public DailyNewsBean[] newArray(int size) {
            return new DailyNewsBean[size];
        }
    };

    public int getPostId() {
        return postId;
    }

    public List<NewsItem> getNewsItemList() {
        return newsItemList;
    }

    private class NewsItem {
        private String time;
        private String title;
        private String imgUrl;
        private String Url;
        private int dayNum;

        public String getTime() {
            return time;
        }

        public String getTitle() {
            return title;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public String getUrl() {
            return Url;
        }

        public int getDayNum() {
            return dayNum;
        }
    }

}


