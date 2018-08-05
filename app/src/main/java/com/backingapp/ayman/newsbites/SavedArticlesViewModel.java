package com.backingapp.ayman.newsbites;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.backingapp.ayman.newsbites.Database.AppDatabase;
import com.backingapp.ayman.newsbites.Models.Article;

import java.util.List;

public class SavedArticlesViewModel extends AndroidViewModel {

    private static final String TAG = SavedArticlesViewModel.class.getSimpleName();

    private LiveData<List<Article>> articles;

    public SavedArticlesViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the movies from the DataBase");
        articles = database.articlesDao().loadSavedArticle();
    }

    public LiveData<List<Article>> getArticles() {
        return articles;
    }
}
