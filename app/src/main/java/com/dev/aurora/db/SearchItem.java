package com.dev.aurora.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "searchItem")
public class SearchItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "searchName")
    private String searchName;

    @ColumnInfo(name = "searchInfo")
    private String searchInfo;

    public SearchItem(String searchName, String searchInfo) {
        this.searchName = searchName;
        this.searchInfo = searchInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearchName() {
        return searchName;
    }

    public String getSearchInfo() {
        return searchInfo;
    }
}
