package com.dev.aurora.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SearchDao {
    @Insert
    void insertSearchItem(SearchItem... searchHistories);

    @Delete
    void deleteSearchItem(SearchItem... searchHistories);

    @Query("Delete From searchitem")
    void deleteAllSearchHistory();

    @Query("Select Distinct * From searchitem Group By searchName Order By id DESC")
    LiveData<List<SearchItem>> getAllSearchItem();
}
