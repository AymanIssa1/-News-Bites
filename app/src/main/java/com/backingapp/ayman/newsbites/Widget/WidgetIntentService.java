package com.backingapp.ayman.newsbites.Widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.backingapp.ayman.newsbites.Constants;
import com.backingapp.ayman.newsbites.Models.Article;

import java.util.ArrayList;
import java.util.List;

public class WidgetIntentService extends IntentService {

    public static final String OPEN_NEWS_ACTION = "open-news-action";

    public WidgetIntentService() {
        super("NewsWidgetIntentService");
    }

    public static void startWidgetService(Context context, List<Article> articles) {
        Intent intent = new Intent(context, WidgetIntentService.class);
        intent.setAction(OPEN_NEWS_ACTION);
        intent.putParcelableArrayListExtra(Constants.ARTICLES_LIST_EXTRA, (ArrayList<? extends Parcelable>) articles);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(OPEN_NEWS_ACTION))
                handleOpenNewsAction(intent);
        }
    }

    private void handleOpenNewsAction(Intent intent) {
        List<Article> articles = intent.getParcelableArrayListExtra(Constants.ARTICLES_LIST_EXTRA);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NewsWidgetProvider.class));
        NewsWidgetProvider.updateWidgetRecipe(this, articles, appWidgetManager, appWidgetIds);

    }
}
