package com.backingapp.ayman.newsbites.Widget;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.backingapp.ayman.newsbites.DataController.NewsController;
import com.backingapp.ayman.newsbites.Interfaces.NewsInterface;
import com.backingapp.ayman.newsbites.Models.Article;
import com.backingapp.ayman.newsbites.Models.Category;
import com.backingapp.ayman.newsbites.R;
import com.backingapp.ayman.newsbites.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WidgetConfigurationActivity extends AppCompatActivity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView(R.id.categoryListView) ListView categoryListView;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_configuration);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        List<String> categories = new ArrayList<>();
        for (int i = 1; i < Category.values().length; i++) {
            categories.add(Category.values()[i].name());
        }
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories);
        categoryListView.setAdapter(categoriesAdapter);
        categoryListView.setOnItemClickListener((parent, view, position, id) -> {
            progressDialog.show();
            NewsController.getNews(getResources().getString(R.string.apiKey), Utils.getUserCountry(this), Category.values()[position + 1], new NewsInterface() {
                @Override
                public void done(List<Article> articles) {
                    WidgetIntentService.startWidgetService(getApplicationContext(), articles);
                    progressDialog.dismiss();

                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);

                    finish();
                }

                @Override
                public void error(String errorMessage) {

                }
            });
        });
    }
}
