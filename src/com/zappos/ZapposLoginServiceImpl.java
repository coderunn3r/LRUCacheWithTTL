package com.zappos;

import com.zappos.cache.LRUCacheImpl2;
import com.zappos.cache.Entry;
import com.zappos.database.DBEx;
import com.zappos.database.FakeDBAccess;

import java.util.Date;

public class ZapposLoginServiceImpl implements LoginService {

    public static int CAPACITY=2;
    public static long TIME_TO_LIVE_IN_SECONDS=10000;
    private LRUCacheImpl2 cache;

    public ZapposLoginServiceImpl()
    {
        cache = new LRUCacheImpl2( TIME_TO_LIVE_IN_SECONDS,CAPACITY );
    }

    public boolean hasUserLoggedInWithin24(String userId)
    {
        Entry userDateFromCache=cache.get(userId);
        if(userDateFromCache!=null)
        {
            System.out.println("Getting for user id:" + userId + " from cache..");
            if(userDateFromCache.val.getTime() > System.currentTimeMillis() - 24 * 60 * 60 * 1000)
            {
                return true;
            }
        }
        else
        {
            Date userDateFromFakeDB= FakeDBAccess.getLastLoginForUser(userId);
            System.out.println("Getting for user id:" + userId + " from fake db..");
            if(userDateFromFakeDB.getTime() > System.currentTimeMillis() - 24 * 60 * 60 * 1000)
            {
                return true;
            }
        }
        return false;
    }

    public void userJustLoggedIn(String userId)
    {
        Date updatedTime=new Date(System.currentTimeMillis());
        try
        {
            FakeDBAccess.setLastLoginForUser(userId,updatedTime);
            cache.put(userId,updatedTime);
        } catch (DBEx dbEx)
        {
            dbEx.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        ZapposLoginServiceImpl obj=new ZapposLoginServiceImpl();

        obj.userJustLoggedIn("Kaushik");
        obj.userJustLoggedIn("Tom");
        System.out.println(obj.hasUserLoggedInWithin24("Kaushik"));
        obj.userJustLoggedIn("Samantha");    // evicts key Tom
        System.out.println(obj.hasUserLoggedInWithin24("Kathy"));
        obj.userJustLoggedIn("Taylor");    // evicts key Kaushik
        System.out.println(obj.hasUserLoggedInWithin24("Kaushik"));
        System.out.println(obj.hasUserLoggedInWithin24("Samantha"));
        Thread.sleep(5000);
        System.out.println("After 5 secs...");
        System.out.println(obj.hasUserLoggedInWithin24("Taylor"));
        Thread.sleep(7000);
        System.out.println("After another 7 secs...");
        System.out.println(obj.hasUserLoggedInWithin24("Samantha"));
        System.out.println(obj.hasUserLoggedInWithin24("Taylor"));
        Thread.sleep(2000);
        System.out.println("After another 2 secs...");
        obj.userJustLoggedIn("Taylor");
        System.out.println(obj.hasUserLoggedInWithin24("Taylor"));
    }
}

// c f f c c f f c