package com.summertaker.blog46.common;

public class BaseParser {

    public String TAG;

    public BaseParser() {
        TAG = "== " + this.getClass().getSimpleName();
    }
}