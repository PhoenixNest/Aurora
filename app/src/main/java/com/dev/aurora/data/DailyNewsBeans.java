package com.dev.aurora.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class DailyNewsBeans implements Parcelable {
    private int postId;
    private List<NewsItem> newsItemList;

    protected DailyNewsBeans(Parcel in) {
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

    public static final Creator<DailyNewsBeans> CREATOR = new Creator<DailyNewsBeans>() {
        @Override
        public DailyNewsBeans createFromParcel(Parcel in) {
            return new DailyNewsBeans(in);
        }

        @Override
        public DailyNewsBeans[] newArray(int size) {
            return new DailyNewsBeans[size];
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


