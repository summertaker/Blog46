package com.summertaker.blog46.common;

import com.summertaker.blog46.data.Blog;

import java.util.ArrayList;

public class DataManager {

    private ArrayList<Blog> mBlogs;

    public DataManager() {
        mBlogs = new ArrayList<>();
        mBlogs.add(new Blog(
                "乃木坂46",
                "http://blog.nogizaka46.com/",
                "?p=",
                15));
        mBlogs.add(new Blog(
                "欅坂46",
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
