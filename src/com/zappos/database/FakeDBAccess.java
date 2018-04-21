package com.zappos.database;

import java.util.Date;

public class FakeDBAccess {

    public static Date getLastLoginForUser(String userId) {
        if(Math.random() < .5)
        {
            return new Date(System.currentTimeMillis());
        }
        return new Date(System.currentTimeMillis()-42*60*60*1000);
    }

    public static void setLastLoginForUser(String userId, Date date) throws DBEx {
        // do nothing
    }
}

