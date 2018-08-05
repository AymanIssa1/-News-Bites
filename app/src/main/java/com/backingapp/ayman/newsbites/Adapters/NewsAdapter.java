package com.backingapp.ayman.newsbites.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.backingapp.ayman.newsbites.Interfaces.DeleteArticleInterface;
import com.backingapp.ayman.newsbites.Interfaces.NewsAdapterInterface;
import com.backingapp.ayman.newsbites.Interfaces.SaveArticleInterface;
import com.backingapp.ayman.newsbites.Models.Article;
import com.backingapp.ayman.newsbites.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<Article> articles;
    private NewsAdapterInterface newsAdapterInterface;
    private SaveArticleInterface saveArticleInterface;
    private DeleteArticleInterface deleteArticleInterface;

    public NewsAdapter(List<Article> articles, NewsAdapterInterface newsAdapterInterface, SaveArticleInterface saveArticleInterface, DeleteArticleInterface deleteArticleInterface) {
        this.articles = articles;
        this.newsAdapterInterface = newsAdapterInterface;
        this.saveArticleInterface = saveArticleInterface;
        this.deleteArticleInterface = deleteArticleInterface;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, null);
        return new NewsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article article = articles.get(position);

        if (article.getUrlToImage() != null)
            Picasso.get().load(article.getUrlToImage()).placeholder(R.drawable.placeholder).into(holder.newsImageView);

        holder.sourceTextView.setText(article.getSource().getName());
        holder.titleTextView.setText(article.getTitle());

        holder.newsImageView.setOnClickListener(v -> newsAdapterInterface.onArticleClickListener(article));

        if (saveArticleInterface != null) {
            holder.deleteButton.setVisibility(View.GONE);
            holder.downloadButton.setOnClickListener(v -> saveArticleInterface.saveArticle(article));
        } else if (deleteArticleInterface != null) {
            holder.downloadButton.setVisibility(View.GONE);
            holder.deleteButton.setOnClickListener(v -> deleteArticleInterface.deleteArticle(article));
        }

    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public int getItemCount() {
        if (articles == null)
            return 0;
        return articles.size();
    }

    protected static class NewsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.newsImageView) ImageView newsImageView;
        @BindView(R.id.sourceTextView) TextView sourceTextView;
        @BindView(R.id.titleTextView) TextView titleTextView;
        @BindView(R.id.downloadButton) ImageView downloadButton;
        @BindView(R.id.deleteButton) ImageView deleteButton;


        public NewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
