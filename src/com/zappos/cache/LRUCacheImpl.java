package com.zappos.cache;

import java.util.*;

public class LRUCacheImpl implements LRUCache {
    private Map<String, Entry> map;
    private final int CAPACITY;
    private final long TIME_TO_LIVE_IN_SECONDS;

    public LRUCacheImpl(long timeToLiveInSeconds, int capacity)
    {
        TIME_TO_LIVE_IN_SECONDS = timeToLiveInSeconds;
        CAPACITY = capacity;
        map =  Collections.synchronizedMap(new LinkedHashMap<String, Entry>(capacity, 1.0f, true){
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > CAPACITY;
            }
        });
    }

    public synchronized Entry get(String key)
    {
        Entry temp=map.get(key);
        if(temp!=null)
        {
            if(!isExpired(temp))
            {
                return temp;
            }
            map.remove(key);
        }
        return null;
    }

    public synchronized void put(String key, Date value)
    {
        map.put(key, new Entry(key,value));
    }

    private synchronized boolean isExpired(Entry item)
    {
        Date lastAccessTs = item.val;
        Date now = new Date(System.currentTimeMillis());
        long elapsedTime = now.getTime() - lastAccessTs.getTime();
        return elapsedTime > TIME_TO_LIVE_IN_SECONDS;
    }
}
