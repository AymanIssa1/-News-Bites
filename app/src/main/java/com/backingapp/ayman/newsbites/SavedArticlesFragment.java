package com.backingapp.ayman.newsbites;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.backingapp.ayman.newsbites.Adapters.NewsAdapter;
import com.backingapp.ayman.newsbites.Database.AppDatabase;
import com.backingapp.ayman.newsbites.Interfaces.DeleteArticleInterface;
import com.backingapp.ayman.newsbites.Interfaces.NewsAdapterInterface;
import com.backingapp.ayman.newsbites.Models.Article;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SavedArticlesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedArticlesFragment extends Fragment implements Observer<List<Article>>, NewsAdapterInterface, DeleteArticleInterface {

    @BindView(R.id.noSavedArticlesFoundTextView) TextView noSavedArticlesFoundTextView;
    RecyclerViewSkeletonScreen skeletonScreen;
    @BindView(R.id.newsRecyclerView) RecyclerView newsRecyclerView;
    private OnFragmentInteractionListener mListener;
    private NewsAdapter newsAdapter;
    private GridLayoutManager gridLayoutManager;

    // to store recyclerview position
    private Parcelable mListState;

    private List<Article> articleList;

    private SavedArticlesViewModel savedArticlesViewModel;

    private Unbinder unbinder;


    public SavedArticlesFragment() {
        // Required empty public constructor
    }

    public static SavedArticlesFragment newInstance() {
        return new SavedArticlesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_saved_articles, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        initRecyclerView();

        // for first fragment launch
        if (savedArticlesViewModel == null)
            getNews();
        else // on rotate
            getActivity().runOnUiThread(() -> setRecyclerView());

        return rootView;
    }

    void getNews() {
        savedArticlesViewModel = ViewModelProviders.of(this).get(SavedArticlesViewModel.class);
        savedArticlesViewModel.getArticles().observe(this, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListState != null) {
            gridLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle outState) {
        super.onSaveInstanceState(outState);

        mListState = gridLayoutManager.onSaveInstanceState();
        outState.putParcelable(Constants.LIST_STATE_EXTRA, mListState);
        outState.putParcelableArrayList(Constants.ARTICLES_LIST_EXTRA, (ArrayList<? extends Parcelable>) articleList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(Constants.LIST_STATE_EXTRA);
            articleList = savedInstanceState.getParcelableArrayList(Constants.ARTICLES_LIST_EXTRA);
        }
    }

    void initRecyclerView() {
        newsAdapter = new NewsAdapter(articleList, this, null, this);
        gridLayoutManager = new GridLayoutManager(getActivity(), Utils.getSpanCount(getContext()));
        newsRecyclerView.setLayoutManager(gridLayoutManager);
        skeletonScreen = Skeleton.bind(newsRecyclerView)
                .adapter(newsAdapter)
                .shimmer(true)
                .frozen(false)
                .duration(1200)
                .count(10)
                .angle(20)
                .load(R.layout.news_demo_item)
                .show();
    }

    void setRecyclerView() {
        skeletonScreen.hide();
        newsRecyclerView.setAdapter(newsAdapter);
        newsAdapter.setArticles(articleList);
        newsAdapter.notifyDataSetChanged();
        if (mListState != null)
            gridLayoutManager.onRestoreInstanceState(mListState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onChanged(@Nullable List<Article> articles) {
        articleList = articles;
        getActivity().runOnUiThread(() -> setRecyclerView());
        noSavedArticlesFoundTextView.setVisibility(articles.size() != 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onArticleClickListener(Article article) {
        new FinestWebView.Builder(getActivity()).show(article.getUrl());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void deleteArticle(Article article) {
        Toast.makeText(getContext(), "Article Deleted.", Toast.LENGTH_SHORT).show();
        AppExecutors.getInstance().diskIO().execute(() -> AppDatabase.getInstance(getContext()).articlesDao().deleteArticle(article.articleId));
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
