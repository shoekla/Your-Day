package com.example.abirshukla.yourday;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
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

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Categorizing...");
        mProgress.show();
        textView = (TextView) findViewById(R.id.textView);
        Bundle info = getIntent().getExtras();
        String code = info.getString("htmlCode");
        String what = info.getString("what");
        //String codeA = code.substring(code.indexOf("<title")+1,code.indexOf("</title")+1);
        //textView.setText(codeA);

        //this is the line ----------------------------------------------------
        Intent p = new Intent(this, ana2.class);
        int be = code.indexOf("<a href=\"/quotes/keywords/");
        int begin = code.indexOf("\"", be);
        if (begin == -1) {
            if (what.equals("quote")) {
                getHTML("http://www.brainyquote.com/quotes/topics/topic_life.html");
                //textView.setText("Could not find it");
                System.out.println("LinkA link1: http://www.brainyquote.com/quotes/topics/topic_life.html");
                mProgress.dismiss();
            }
            else if (what.equals("video")) {
                //t.substring(t.lastIndexOf("/")+1,t.indexOf(".html"))

                getHTML("http://abirshukla.pythonanywhere.com/yot/life/");

            }
            else {
                getHTML("http://abirshukla.pythonanywhere.com/picQ/life/");
            }
        } else {
            if (what.equals("quote")) {
                int end = code.indexOf("\"", begin + 1);
                String part = code.substring(begin + 1, end);
                System.out.println("LinkA Part: " + part);
                String link = "http://www.brainyquote.com" + part;
                System.out.println("LinkA link1: " + link);
                getHTML(link);
                mProgress.dismiss();
                //textView.setText("Link is: "+link);
            }
            else if (what.equals("video")) {
                int end = code.indexOf("\"", begin + 1);
                String part = code.substring(begin + 1, end);
                System.out.println("LinkA Part: " + part);
                part = part.substring(part.lastIndexOf("/")+1,part.indexOf(".html"));
                part=part.replace(" ","%20");
                getHTML("http://abirshukla.pythonanywhere.com/yot/"+part+"/");
            }
            else {
                int end = code.indexOf("\"", begin + 1);
                String part = code.substring(begin + 1, end);
                System.out.println("LinkA Part: " + part);
                part = part.substring(part.lastIndexOf("/")+1,part.indexOf(".html"));
                part=part.replace(" ","%20");
                getHTML("http://abirshukla.pythonanywhere.com/picQ/"+part+"/");
            }
        }
    }
    public void getHTML(final String url) {
        if (url.contains("abirshukla")) {
            System.out.println("Begin HTML");
            final String[] d = new String[1];
            Ion.with(getApplicationContext())
                    .load(url)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            result = result.substring(result.indexOf("<p>")+3,result.indexOf("</p>"));
                            Intent intent =new Intent(Intent.ACTION_VIEW);
                            intent.setPackage("com.google.android.youtube");
                            intent.setData(Uri.parse(result));
                            startActivity(intent);
                        }
                    });
        }
        else {
            System.out.println("Begin HTML");
            final String[] d = new String[1];
            Ion.with(getApplicationContext())
                    .load(url)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            Intent a = new Intent(ana1.this, ana2.class);
                            System.out.println("LinkA link: " + result);
                            a.putExtra("link", result);
                            mProgress.dismiss();
                            startActivity(a);
                        }
                    });
        }
    }












}

