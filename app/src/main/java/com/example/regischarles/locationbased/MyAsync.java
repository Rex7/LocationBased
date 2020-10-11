package com.example.regischarles.locationbased;


import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class MyAsync extends AsyncTask<Void, Integer, Integer> {
    TransferData transferData;
    private int res=-1;
    public MyAsync(TransferData transferData){
        this.transferData=transferData;
    }
    @Override
    protected Integer doInBackground(Void... params) {
        try {
            URL url = new URL("http://hostinger.in/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            int res = urlConnection.getResponseCode();
            Log.v("Response code ", "" + res);
            Log.v("cartoon", "response" + res);
            return res;
        } catch (Exception ex) {
            Log.v("Error Stream", ex.getMessage());
        }
        return res;


    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        transferData.setData(integer.intValue());
    }
}