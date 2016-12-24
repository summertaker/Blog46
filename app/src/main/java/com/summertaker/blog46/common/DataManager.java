package com.summertaker.blog46.common;

import android.content.Context;
import android.content.res.Resources;

import com.summertaker.blog46.R;
import com.summertaker.blog46.data.Blog;

import java.util.ArrayList;

public class DataManager {

    private String TAG;
    private Context mContext;
    private Resources mResources;

    private ArrayList<Blog> mBlogs;

    public DataManager(Context context) {
        this.TAG = this.getClass().getSimpleName();
        this.mContext = context;
        this.mResources = context.getResources();
        //mSharedPreferences = context.getSharedPreferences(Config.USER_PREFERENCE_KEY, 0);

        mBlogs = new ArrayList<>();
        mBlogs.add(new Blog(
                mResources.getString(R.string.nogizaka46),
                "http://blog.nogizaka46.com/",
                "?p=",
                15));
        mBlogs.add(new Blog(
                mResources.getString(R.string.keyakizaka46),
                "http://www.keyakizaka46.com/mob/news/diarKiji.php?site=k46o&ima=0000&cd=member",
                "&rw=20&page=",
                0));
    }

    public Blog getBlogData(int i) {
        return mBlogs.get(i);
    }

    public int getBlogDataSize() {
        return mBlogs.size();
    }
}
