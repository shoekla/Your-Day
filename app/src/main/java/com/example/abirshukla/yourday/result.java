package com.example.abirshukla.yourday;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.InputStream;
import java.net.URL;

public class result extends Activity {
    TextView quote;
    ImageView imageView;
    boolean isImageFitToScreen;
    Intent emailIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_result);
        quote = (TextView) findViewById(R.id.quote);
        Bundle userData = getIntent().getExtras();
        String userQuote = userData.getString("quoteForUser");
        quote.setText(userQuote);
        String author = userData.getString("author");
        isImageFitToScreen = false;
        imageView = (ImageView) findViewById(R.id.imageView);

        //String picUrl = userData.getString("picUrl");
        //new DownLoadImageTask(imageView).execute(picUrl);
        getHTML(author);





    }

    public void getHTML(final String author) {
        System.out.println("Begin HTML");
        String au = author.replace(" ","%20");
        String url ="http://abirshukla.pythonanywhere.com/searchImage/"+au+"/";
        final String[] d = new String[1];
        Ion.with(getApplicationContext())
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        int be = result.indexOf(":");
                        int begin = result.indexOf(" ",be);
                        int end = result.indexOf("<",begin);
                        String authorUrl = result.substring(begin+1,end);
                        System.out.println("Author Url: "+authorUrl);
                        new DownLoadImageTask(imageView).execute(authorUrl);
                    }
                });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    public void onDestroy() {

        super.onDestroy();


    }








    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

}
