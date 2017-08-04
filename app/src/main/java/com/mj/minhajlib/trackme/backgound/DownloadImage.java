package com.mj.minhajlib.trackme.backgound;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Minhaj lib on 8/3/2017.
 */

public class DownloadImage extends AsyncTask<String,Void,Bitmap> {

    private Context mContext;
    private ActionBar mToolbar;

    public DownloadImage(Context context,ActionBar toolbar){
        mContext = context;
        mToolbar = toolbar;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {

        Bitmap bitmap = null;
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(),bitmap);
        mToolbar.setIcon(drawable);
        Log.d("tracker","downloaded");
    }
}
