package com.summertaker.blog46.common;

import android.content.Context;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    protected String TAG;
    protected Context mContext;

    public BaseFragment() {
        this.TAG = "===== " + this.getClass().getSimpleName();
    }
}
