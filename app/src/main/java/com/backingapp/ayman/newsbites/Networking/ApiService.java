package com.backingapp.ayman.newsbites.Networking;

import com.backingapp.ayman.newsbites.Models.Result;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/v2/top-headlines")
    Single<Result> getHeadLinesNews(@Query("country") String country, @Query("apiKey") String apiKey);


    @GET("/v2/top-headlines")
    Single<Result> getHeadLinesNewsByCategory(@Query("category") String Category, @Query("country") String country, @Query("apiKey") String apiKey);

    @GET("/v2/everything")
    Single<Result> searchNews(@Query("q") String searchKeyword, @Query("apiKey") String apiKey);

}
