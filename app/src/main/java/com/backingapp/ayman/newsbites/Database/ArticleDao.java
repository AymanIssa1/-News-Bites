package com.backingapp.ayman.newsbites.Database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.backingapp.ayman.newsbites.Models.Article;

import java.util.List;

@Dao
public interface ArticleDao {

    @Query("SELECT * From article")
    LiveData<List<Article>> loadSavedArticle();

    @Insert
    void insertArticle(Article article);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateArtilr(Article article);

    @Query("DELETE FROM article WHERE articleId = :articleId")
    void deleteArticle(int articleId);

    @Query("SELECT * FROM article WHERE articleId = :articleId")
    Article loadArticleById(int articleId);

}
