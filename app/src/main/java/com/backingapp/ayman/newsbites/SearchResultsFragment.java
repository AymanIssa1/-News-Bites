package com.backingapp.ayman.newsbites;

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
import com.backingapp.ayman.newsbites.DataController.NewsController;
import com.backingapp.ayman.newsbites.Database.AppDatabase;
import com.backingapp.ayman.newsbites.Interfaces.NewsAdapterInterface;
import com.backingapp.ayman.newsbites.Interfaces.NewsInterface;
import com.backingapp.ayman.newsbites.Interfaces.SaveArticleInterface;
import com.backingapp.ayman.newsbites.Models.Article;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.thefinestartist.finestwebview.FinestWebView;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

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
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsFragment extends Fragment implements NewsAdapterInterface, ConnectivityChangeListener, SaveArticleInterface {

    private static final String SEARCH_QUERY_EXTRA = "param1";
    @BindView(R.id.noResultFoundTextView) TextView noResultFoundTextView;
    RecyclerViewSkeletonScreen skeletonScreen;
    @BindView(R.id.newsRecyclerView) RecyclerView newsRecyclerView;
    private String searchQuery;
    private NewsAdapter newsAdapter;
    private GridLayoutManager gridLayoutManager;

    private List<Article> articleList;

    // to store recyclerview position
    private Parcelable mListState;

    private Unbinder unbinder;

    private OnFragmentInteractionListener mListener;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    public static SearchResultsFragment newInstance(String searchQuery) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY_EXTRA, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchQuery = getArguments().getString(SEARCH_QUERY_EXTRA);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        initRecyclerView();

        // for first fragment launch
        if (articleList == null)
            getNews();
        else // on rotate
            getActivity().runOnUiThread(() -> setRecyclerView());

        return rootView;
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

    void getNews() {
        AppExecutors.getInstance().networkIO().execute(() -> NewsController.searchNews(getResources().getString(R.string.apiKey), searchQuery, new NewsInterface() {
            @Override
            public void done(List<Article> articles) {
                articleList = articles;
                getActivity().runOnUiThread(() -> {
                    setRecyclerView();
                    noResultFoundTextView.setVisibility(articles.size() != 0 ? View.INVISIBLE : View.VISIBLE);
                });
            }

            @Override
            public void error(String errorMessage) {

            }
        }));
    }

    void initRecyclerView() {
        newsAdapter = new NewsAdapter(articleList, this, this, null);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onArticleClickListener(Article article) {
        new FinestWebView.Builder(getActivity()).show(article.getUrl());
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        if (event.getState() == ConnectivityState.CONNECTED) {
            // device has active internet connection
            if (articleList == null)
                getNews();
        } else {
            // there is no active internet connection on this device
        }
    }

    @Override
    public void saveArticle(Article article) {
        Toast.makeText(getContext(), "Article Saved.", Toast.LENGTH_SHORT).show();
        AppDatabase.getInstance(getContext()).articlesDao().insertArticle(article);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
