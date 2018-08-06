package com.backingapp.ayman.newsbites.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.backingapp.ayman.newsbites.Models.Article;
import com.backingapp.ayman.newsbites.R;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class NewsWidgetProvider extends AppWidgetProvider {

    public static List<Article> articleList;
    public static int appWidgetId;

    static void updateAppWidget(Context context, List<Article> articles, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.news_widget);

        NewsWidgetProvider.appWidgetId = appWidgetId;
        NewsWidgetProvider.articleList = articles;


        Intent listIntent = new Intent(context, ListViewWidgetService.class);
        views.setRemoteAdapter(R.id.newsListView, listIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateWidgetRecipe(Context context, List<Article> articles, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, articles, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

