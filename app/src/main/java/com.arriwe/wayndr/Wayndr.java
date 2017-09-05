package com.arriwe.wayndr;

import android.content.Context;

/**
 * Created by Abhi1 on 20/08/15.
 */
public class Wayndr extends android.app.Application {

    private static Wayndr instance;

    public Wayndr() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

}