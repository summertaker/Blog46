package com.summertaker.blog46;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.blog46.blog.BlogAdapterInterface;
import com.summertaker.blog46.common.DataManager;
import com.summertaker.blog46.data.Blog;
import com.summertaker.blog46.data.Article;
import com.summertaker.blog46.blog.BlogListAdapter;
import com.summertaker.blog46.common.BaseApplication;
import com.summertaker.blog46.common.BaseFragment;
import com.summertaker.blog46.common.Config;
import com.summertaker.blog46.parser.Nogizaka46Parser;
import com.summertaker.blog46.util.EndlessScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.summertaker.blog46.common.BaseApplication.tag;

public class MainFragment extends BaseFragment implements BlogAdapterInterface {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private Context mContext;
    private DataManager mDataManager;

    private Blog mBlog;

    private ProgressBar mPbLoading;

    private ListView mListView;
    private BlogListAdapter mAdapter;
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

        mDataManager = new DataManager();
        mBlog = mDataManager.getBlogData(getArguments().getInt(ARG_SECTION_NUMBER));

        mPbLoading = (ProgressBar) rootView.findViewById(R.id.pbLoading);
        mPbLoading.getIndeterminateDrawable().setColorFilter(Config.PROGRESS_BAR_COLOR, PorterDuff.Mode.MULTIPLY);

        mArticles = new ArrayList<>();

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mAdapter = new BlogListAdapter(mContext, mArticles, this);
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
                //Log.e(mTag, "onLoadMore().page: " + page + " / " + mMaxPage);
                if (page > mMaxPage) {
                    //showLastDataMessage();
                } else {
                    loadData();
                }
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        loadData();

        return rootView;
    }

    private void loadData() {
        if (mIsLoading) {
            return;
        }

        if (mCurrentPage > mMaxPage) {
            return;
        }

        mIsLoading = true;

        String url = mBlog.getUrl();
        if (url.contains("keyakizaka46.com")) {
            return;
        }

        if (mCurrentPage > 1) {
            url += mBlog.getPageParam() + mCurrentPage;
            //mLoLoadingMore.setVisibility(View.VISIBLE);
        }

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d(tag, response);
                        parseData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag, "Error: " + error.getMessage());
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

    private void parseData(String response) {
        Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();

        ArrayList<Article> articles = new ArrayList<>();
        nogizaka46Parser.parseBlogList(response, articles);

        mArticles.addAll(articles);

        renderData();
    }

    private void renderData() {
        if (mCurrentPage == 1) {
            mPbLoading.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            //mLoLoadingMore.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();

        //mBaseToolbar.setTitle(mTitle + " ( " + mCurrentPage + " / " + mMaxPage + " )");

        mIsLoading = false;
        mCurrentPage++;
    }

    @Override
    public void onImageClick(int position, String imageUrl) {

    }

    @Override
    public void onContentClick(int position) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}