package com.example.abirshukla.yourday;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

public class pic extends Activity {

    ImageView imageView;
    boolean isImageFitToScreen;
    Intent emailIntent2;
    String imgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_pic);
        Bundle userData = getIntent().getExtras();
        imageView = (ImageView) findViewById(R.id.imageView);
        imgUrl = userData.getString("pic");

        new DownLoadImageTask(imageView).execute(imgUrl);
        //String picUrl = userData.getString("picUrl");
        //new DownLoadImageTask(imageView).execute(picUrl);



        String[] TO = {""};
        String[] CC = {""};
        emailIntent2 = new Intent(Intent.ACTION_SEND);

        emailIntent2.setData(Uri.parse("mailto:"));
        emailIntent2.setType("text/plain");
        emailIntent2.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent2.putExtra(Intent.EXTRA_CC, CC);
        Calendar c = Calendar.getInstance();
        int da = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        String monthString = "";
        switch (month) {
            case 0:  monthString = "January";
                break;
            case 1:  monthString = "February";
                break;
            case 2:  monthString = "March";
                break;
            case 3:  monthString = "April";
                break;
            case 4:  monthString = "May";
                break;
            case 5:  monthString = "June";
                break;
            case 6:  monthString = "July";
                break;
            case 7:  monthString = "August";
                break;
            case 8:  monthString = "September";
                break;
            case 9: monthString = "October";
                break;
            case 10: monthString = "November";
                break;
            case 11: monthString = "December";
                break;
            default: monthString = "Invalid month";
                break;
        }
        String date = monthString+" "+da+", "+year;
        emailIntent2.putExtra(Intent.EXTRA_SUBJECT, "Your Picture of the Day for "+ date);
        emailIntent2.putExtra(Intent.EXTRA_TEXT, imgUrl);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });


    }
    public void sendEmail() {
        try {
            startActivity(Intent.createChooser(emailIntent2, "Send mail..."));
            finish();
            System.out.println("Finished sending email...");
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
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
                        imgUrl = authorUrl;
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
