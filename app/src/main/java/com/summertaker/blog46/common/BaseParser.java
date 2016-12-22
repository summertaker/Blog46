package com.summertaker.blog46.common;

public class BaseParser {

    public String tag;

    public BaseParser() {
        tag = "== " + this.getClass().getSimpleName();
    }
}