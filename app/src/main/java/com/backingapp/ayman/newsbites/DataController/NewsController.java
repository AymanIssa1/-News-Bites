package com.backingapp.ayman.newsbites.DataController;

import com.backingapp.ayman.newsbites.Interfaces.NewsInterface;
import com.backingapp.ayman.newsbites.Models.Category;
import com.backingapp.ayman.newsbites.Models.Result;
import com.backingapp.ayman.newsbites.Networking.ApiClient;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsController {

    public static void getNews(String apiKey, String country, Category category, NewsInterface newsInterface) {
        if (category == Category.Headlines)
            getHeadLinesNews(apiKey, country, newsInterface);
        else
            getNewsByCategory(apiKey, country, category, newsInterface);
    }

    public static void searchNews(String apiKey, String searchKeyword, NewsInterface newsInterface) {
        ApiClient.getInstance()
                .searchNews(apiKey, searchKeyword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsResultSingle(newsInterface));
    }

    private static void getHeadLinesNews(String apiKey, String country, NewsInterface newsInterface) {
        ApiClient.getInstance()
                .getNews(apiKey, country)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsResultSingle(newsInterface));
    }

    private static void getNewsByCategory(String apiKey, String country, Category category, NewsInterface newsInterface) {
        ApiClient.getInstance()
                .getNews(apiKey, country, category.name())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsResultSingle(newsInterface));
    }

    private static SingleObserver<Result> newsResultSingle(NewsInterface newsInterface) {
        return new SingleObserver<Result>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onSuccess(Result result) {
                newsInterface.done(result.getArticles());
                disposable.dispose();
            }

            @Override
            public void onError(Throwable e) {
                newsInterface.error(e.getMessage());
                disposable.dispose();
            }
        };
    }

}
