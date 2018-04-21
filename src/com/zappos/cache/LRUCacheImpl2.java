package com.zappos.cache;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LRUCacheImpl2 implements LRUCache {
    private int capacity;
    private final long timeToLiveInSeconds;
    private Map<String, Entry> map = new ConcurrentHashMap<>();
    private Entry head;
    private Entry tail;
    private int count;

    public LRUCacheImpl2(long timeToLiveInSeconds, int capacity) {
        this.timeToLiveInSeconds = timeToLiveInSeconds;
        this.capacity = capacity;
        count = 0;
        head = new Entry();
        tail = new Entry();
        head.next = tail;
        tail.pre = head;
    }

    public synchronized Entry get(String key) {
        Entry e = map.get(key);

        if(e!=null)
        {
            if(!isExpired(e))
            {
                update(e);
                return e;
            }
            map.remove(key);
        }
        return null;
    }

    public synchronized void put(String key, Date value) {
        Entry n = map.get(key);
        if(null==n){
            n = new Entry(key, value);
            map.put(key, n);
            add(n);
            ++count;
        }
        else{
            n.val = value;
            update(n);
        }
        if(count>capacity){
            Entry toDel = tail.pre;
            remove(toDel);
            map.remove(toDel.key);
            --count;
        }
    }

    private synchronized boolean isExpired(Entry item)
    {
        Date lastAccessTs = item.val;
        Date now = new Date(System.currentTimeMillis());
        long elapsedTime = now.getTime() - lastAccessTs.getTime();
        return elapsedTime > timeToLiveInSeconds;
    }

    private void update(Entry e){
        remove(e);
        add(e);
    }
    private void add(Entry e){
        Entry after = head.next;
        head.next = e;
        e.pre = head;
        e.next = after;
        after.pre = e;
    }

    private void remove(Entry e){
        Entry before = e.pre, after = e.next;
        before.next = after;
        after.pre = before;
    }

}