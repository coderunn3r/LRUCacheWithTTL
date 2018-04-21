package com.zappos.cache;

import java.util.Date;

// Doubly linked list
public class Entry {
    public String key;
    public Date val;
    public Entry pre;
    public Entry next;

    Entry(String key, Date val) {
        this.key = key;
        this.val = val;
    }

    Entry(){
        this("", null);
    }
}
