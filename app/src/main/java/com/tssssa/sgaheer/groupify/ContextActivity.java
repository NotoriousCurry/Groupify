package com.tssssa.sgaheer.groupify;

import android.app.Application;
import android.content.Context;

/**
 * Created by sgaheer on 12/05/2016.
 */
class ContextActivity extends Application{

    private static Context mContext;

    @Override
    public void onCreate(){
        mContext = this.getApplicationContext();
    }

    public static Context getAppContext() {
        return mContext;
    }
}
