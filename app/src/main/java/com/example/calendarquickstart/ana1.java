package com.example.calendarquickstart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class ana1 extends Activity {
    TextView textView;
    ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Categorizing...");
        mProgress.show();
        textView = (TextView) findViewById(R.id.textView);
        Bundle info = getIntent().getExtras();
        String code = info.getString("htmlCode");
        //String codeA = code.substring(code.indexOf("<title")+1,code.indexOf("</title")+1);
        //textView.setText(codeA);

        //this is the line ----------------------------------------------------
        Intent p = new Intent(this, ana2.class);
        int be = code.indexOf("<a href=\"/quotes/keywords/");
        int begin = code.indexOf("\"", be);
        if (begin == -1) {
            getHTML("http://www.brainyquote.com/quotes/topics/topic_life.html");
            //textView.setText("Could not find it");
            mProgress.dismiss();
            startActivity(p);
        } else {
            int end = code.indexOf("\"", begin + 1);
            String part = code.substring(begin + 1, end);
            String link = "http://www.brainyquote.com" + part;
            getHTML(link);
            mProgress.dismiss();
            //textView.setText("Link is: "+link);
        }
    }
    public void getHTML(final String url) {
        System.out.println("Begin HTML");
        final String[] d = new String[1];
        Ion.with(getApplicationContext())
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Intent a = new Intent(ana1.this, ana2.class);
                        a.putExtra("link", result);
                        mProgress.dismiss();
                        startActivity(a);
                    }
                });
    }












}


