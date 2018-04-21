package com.zappos.cache;

import java.util.Date;

public interface LRUCache {
    public void put(String key, Date item);

    public Entry get(String key);

}
