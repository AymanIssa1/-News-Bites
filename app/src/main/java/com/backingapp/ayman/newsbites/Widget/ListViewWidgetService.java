package com.backingapp.ayman.newsbites.Widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.backingapp.ayman.newsbites.Models.Article;
import com.backingapp.ayman.newsbites.R;

import java.util.List;

public class ListViewWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Article> articles;
    private int appWidgetId;

    public ListViewRemoteViewsFactory(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        appWidgetId = NewsWidgetProvider.appWidgetId;
        articles = NewsWidgetProvider.articleList;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (articles == null)
            return 0;
        return articles.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.news_list_widget_item);

        Article article = articles.get(position);

        remoteViews.setTextViewText(R.id.newsTitleTextView, article.getTitle());
        remoteViews.setTextViewText(R.id.newsSourceTextView, article.getSource().getName());

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
