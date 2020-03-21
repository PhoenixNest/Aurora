package com.dev.aurora.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.dev.aurora.db.CoffeeDataBase;
import com.dev.aurora.db.SearchDao;
import com.dev.aurora.db.SearchItem;

import java.util.List;

public class CoffeeRepository {
    private SearchDao searchDao;
    private LiveData<List<SearchItem>> liveSearchItemList;

    public CoffeeRepository(Context context) {
        CoffeeDataBase coffeeDataBase = CoffeeDataBase.getInstance(context);
        searchDao = coffeeDataBase.getSearchDao();
        liveSearchItemList = searchDao.getAllSearchItem();
    }

    // get 所有搜索条目列表
    public LiveData<List<SearchItem>> getLiveSearchItemList() {
        return liveSearchItemList;
    }

    // insert 搜索条目
    public void insertSearchItem(SearchItem... searchItems) {
        new InsertSearchItemAsyncTask(searchDao).execute(searchItems);
    }

    // delete Single 搜索条目
    public void deleteSingleSearchItem(SearchItem... searchItems) {
        new DeleteSingleSearchItemAsyncTask(searchDao).execute(searchItems);
    }

    // delete All 搜索条目
    public void deleteAllSearchItem() {
        new DeleteAllSearchItemAsyncTask(searchDao).execute();
    }

    // insert 搜索历史条目
    public static class InsertSearchItemAsyncTask extends AsyncTask<SearchItem, Void, Void> {

        private SearchDao searchDao;

        InsertSearchItemAsyncTask(SearchDao searchDao) {
            this.searchDao = searchDao;
        }

        @Override
        protected Void doInBackground(SearchItem... searchItems) {
            searchDao.insertSearchItem(searchItems);
            System.out.println("插入成功");
            return null;
        }
    }

    // delete Single 删除单条搜索历史记录
    public static class DeleteSingleSearchItemAsyncTask extends AsyncTask<SearchItem, Void, Void> {

        private SearchDao searchDao;

        DeleteSingleSearchItemAsyncTask(SearchDao searchDao) {
            this.searchDao = searchDao;
        }

        @Override
        protected Void doInBackground(SearchItem... searchItems) {
            searchDao.deleteSearchItem(searchItems);
            return null;
        }
    }

    // delete All 删除全部历史条目
    public static class DeleteAllSearchItemAsyncTask extends AsyncTask<Void, Void, Void> {

        private SearchDao searchDao;

        DeleteAllSearchItemAsyncTask(SearchDao searchDao) {
            this.searchDao = searchDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            searchDao.getAllSearchItem();
            return null;
        }
    }
}


