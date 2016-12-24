package com.summertaker.blog46;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.blog46.blog.BlogAdapter;
import com.summertaker.blog46.blog.BlogInterface;
import com.summertaker.blog46.common.BaseApplication;
import com.summertaker.blog46.common.BaseFragment;
import com.summertaker.blog46.common.Config;
import com.summertaker.blog46.common.DataManager;
import com.summertaker.blog46.data.Article;
import com.summertaker.blog46.data.Blog;
import com.summertaker.blog46.parser.Keyakizaka46Parser;
import com.summertaker.blog46.parser.Nogizaka46Parser;
import com.summertaker.blog46.util.EndlessScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends BaseFragment implements BlogInterface {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private Context mContext;
    private Activity mActivity;
    private DataManager mDataManager;

    private Blog mBlog;

    private ProgressBar mPbLoading;
    private LinearLayout mLoLoadMore;

    private ListView mListView;
    private BlogAdapter mAdapter;
    private ArrayList<Article> mArticles;

    private int mCurrentPage = 1;
    private int mMaxPage = 15;
    private boolean mIsLoading = false;

    public MainFragment() {
    }

    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mContext = container.getContext();
        mActivity = (Activity) mContext;

        mDataManager = new DataManager(mContext);
        mBlog = mDataManager.getBlogData(getArguments().getInt(ARG_SECTION_NUMBER));
        mMaxPage = mBlog.getMaxPage();

        mPbLoading = (ProgressBar) rootView.findViewById(R.id.pbLoading);
        mPbLoading.getIndeterminateDrawable().setColorFilter(Config.PROGRESS_BAR_COLOR, PorterDuff.Mode.MULTIPLY);

        mLoLoadMore = (LinearLayout) rootView.findViewById(R.id.loLoadMore);
        ProgressBar pbLoadMore = (ProgressBar) rootView.findViewById(R.id.pbLoadMore);
        pbLoadMore.getIndeterminateDrawable().setColorFilter(Config.PROGRESS_BAR_COLOR_MORE, PorterDuff.Mode.MULTIPLY);

        mArticles = new ArrayList<>();

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mAdapter = new BlogAdapter(mContext, mArticles, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onContentClick(position);
            }
        });
        mListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                //Log.e(TAG, "onLoadMore().page: " + page + " / " + mMaxPage);
                if (mMaxPage == 0 || mCurrentPage <= mMaxPage) {
                    loadData();
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                } else {
                    return false;
                }
            }
        });

        loadData();

        return rootView;
    }

    private void loadData() {
        if (mIsLoading) {
            return;
        }

        mIsLoading = true;

        String url = mBlog.getUrl();
        if (mCurrentPage > 1) {
            url += mBlog.getPageParam() + mCurrentPage;
            mLoLoadMore.setVisibility(View.VISIBLE);
        }

        final String blogUrl = url;

        StringRequest request = new StringRequest(Request.Method.GET, blogUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d(TAG, response);
                        parseData(blogUrl, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", Config.USER_AGENT_WEB);
                return headers;
            }
        };

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(request);
    }

    private void parseData(String blogUrl, String response) {
        ArrayList<Article> articles = new ArrayList<>();

        if (blogUrl.contains("blog.nogizaka46.com")) {
            Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
            nogizaka46Parser.parseBlogList(response, articles);
        } else {
            Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
            keyakizaka46Parser.parseBlogList(response, articles);
        }

        mArticles.addAll(articles);

        renderData();
    }

    private void renderData() {
        if (mCurrentPage == 1) {
            mPbLoading.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mLoLoadMore.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();

        //mBaseToolbar.setTitle(mTitle + " ( " + mCurrentPage + " / " + mMaxPage + " )");

        mIsLoading = false;
        mCurrentPage++;
    }

    @Override
    public void onImageClick(int position, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
            startActivityForResult(intent, 100);
            mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onContentClick(int position) {
        Article article = (Article) mAdapter.getItem(position);
        String url = article.getUrl();
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivityForResult(intent, 100);
            mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}