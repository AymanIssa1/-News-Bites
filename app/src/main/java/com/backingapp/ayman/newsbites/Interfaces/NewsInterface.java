package com.backingapp.ayman.newsbites.Interfaces;

import com.backingapp.ayman.newsbites.Models.Article;

import java.util.List;

public interface NewsInterface {

    void done(List<Article> articles);

    void error(String errorMessage);

}
