package com.example.calendarquickstart;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class set extends AppCompatActivity {
    Switch note;
    TimePicker tp;
    Button sub;
    PendingIntent pendingIntent;
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Calendar API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Your Quote of the Day!");
        setSupportActionBar(toolbar);
        note = (Switch) findViewById(R.id.switch1);
        note.setChecked(com.example.calendarquickstart.htmlCodeJ.notify);
        tp = (TimePicker) findViewById(R.id.timePicker);
        tp.setHour(com.example.calendarquickstart.htmlCodeJ.noteHour);
        tp.setMinute(com.example.calendarquickstart.htmlCodeJ.noteMin);
        sub = (Button) findViewById(R.id.button2);
        sub.setOnClickListener(new View.OnClickListener() {
                                   @TargetApi(Build.VERSION_CODES.M)
                                   @Override
                                   public void onClick(View v) {
                                       boolean noti = true;
                                       noti = note.isChecked();
                                       com.example.calendarquickstart.htmlCodeJ.notify = noti;
                                       int hour = tp.getHour();
                                       int min = tp.getMinute();
                                       com.example.calendarquickstart.htmlCodeJ.noteHour = hour;
                                       com.example.calendarquickstart.htmlCodeJ.noteMin = min;
                                       String message = "";
                                       if (noti) {
                                           message = "Notification on for "+hour+": "+min;
                                       }
                                       else {
                                           message = "Notification off for "+hour+": "+min;
                                       }
                                       alarmMethod();
                                       Toast.makeText(set.this,(String)message , Toast.LENGTH_LONG).show();

                                   }
                               }
        );



    }
    public void changeA (View view) {
        System.out.println("Choosing Account");
        chooseAccount();
        Toast.makeText(set.this,"Google account Changed" , Toast.LENGTH_LONG).show();
    }


    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
            System.out.println("Acount: " + mCredential.getSelectedAccountName());
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    private void alarmMethod() {
        Intent myIntent= new Intent(this, Note.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, com.example.calendarquickstart.htmlCodeJ.noteHour);
        calendar.set(Calendar.MINUTE, com.example.calendarquickstart.htmlCodeJ.noteMin);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);  //set repeating every 24 hours
        Toast.makeText(this, "Set Alarm!", Toast.LENGTH_SHORT).show();
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         *
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            List<String> resultStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s", event.getSummary()));
                String linkA = URLEncoder.encode(eventStrings.get(0), "utf-8");
                //linkA = linkA.replace(",","");
                String linkU = "http://www.brainyquote.com/search_results.html?q=" + linkA;
                //System.out.println(linkU);
                //String finalUrl = getFinalUrl(ht);
                //finalUrl = finalUrl.replace(" ","+");
                //System.out.println("FInal UrL: "+finalUrl);
                // String quote = getQuote(getHTML(finalUrl));
                //resultStrings.add(quote);
                //resultStrings.add(event.getSummary());
                //resultStrings.add(linkU);
                // System.out.println("Quote for the User is: " + quote);
                return resultStrings;
            }
            return resultStrings;
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<String> output) {
            String userQuote = "";
            if (output == null || output.size() == 0) {
                userQuote = "No results returned.";
            } else {
                userQuote = TextUtils.join("\n", output);
            }
            //System.out.println("Beiging Image show");
            //Intent i = new Intent(MainActivity.this,result.class);
            //System.out.println("Image Shown");
            //i.putExtra("userQuote", userQuote);
            //System.out.println("Trying to get pic");
            //String picUrl = getPicUrl(getAuthor(userQuote));
            //System.out.println(picUrl);
            //i.putExtra("picUrl",picUrl);
            //startActivity(i);
        }

        public void fullScreen() {

            // BEGIN_INCLUDE (get_current_ui_flags)
            // The UI options currently enabled are represented by a bitfield.
            // getSystemUiVisibility() gives us that bitfield.
            int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
            int newUiOptions = uiOptions;
            // END_INCLUDE (get_current_ui_flags)
            // BEGIN_INCLUDE (toggle_ui_flags)
            boolean isImmersiveModeEnabled =
                    ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);


            // Navigation bar hiding:  Backwards compatible to ICS.
            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            // Status bar hiding: Backwards compatible to Jellybean
            if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }

            // Immersive mode: Backward compatible to KitKat.
            // Note that this flag doesn't do anything by itself, it only augments the behavior
            // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
            // all three flags are being toggled together.
            // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
            // Sticky immersive mode differs in that it makes the navigation and status bars
            // semi-transparent, and the UI flag does not get cleared when the user interacts with
            // the screen.
            if (Build.VERSION.SDK_INT >= 18) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
            //END_INCLUDE (set_ui_flags)
        }

        @Override
        protected void onCancelled() {
            String userQuote = "";
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            com.example.calendarquickstart.MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    userQuote = "The following error occurred:\n"
                            + mLastError.getMessage();
                }
            } else {
                userQuote = "Request cancelled.";
            }
            userQuote = "Canceled";
            //System.out.println("Beiging Image show");
            //Intent i = new Intent(MainActivity.this,result.class);
            //System.out.println("Image Shown");
            //i.putExtra("userQuote", userQuote);
            //System.out.println("Trying to get pic");
            //String picUrl = getPicUrl(getAuthor(userQuote));
            //System.out.println(picUrl);
            //i.putExtra("picUrl",picUrl);
            //startActivity(i);


        }

        /*
        public String getAuthor (String quote) {
            if (quote.endsWith(")")) {
                int begin = quote.lastIndexOf("(");
                int end = quote.lastIndexOf(")");
                return quote.substring(begin+1,end-1);
            }
            else {
                int begin = quote.lastIndexOf("-");
                return quote.substring(begin+1);
            }
        }
        public String getFinalUrl(String ht) {
            int index = ht.indexOf("/quotes/keywords/");
            //System.out.println("Index for the quote: " + index);
            if (index == -1) {
                int index2 = ht.indexOf("<a href=\"/quotes/topics/");
                if (index2 == -1)
                    return "http://www.brainyquote.com/quotes/topics/topic_motivational.html";
                String quote = ht.substring(index2+9,ht.indexOf(">",index2+1));
                return  "http://www.brainyquote.com/"+quote;
            }
            String quote = ht.substring(index+9,ht.indexOf(">",index+1));
            return  "http://www.brainyquote.com/"+quote;
        }
        public String getPicUrl(String author) {
            String ac = author.replace(" ", "%20");
            String url = "http://abirshukla.pythonanywhere.com/searchImage/"+ac+"/";
            String ht = getHTML(url);
            int acBegin = ht.indexOf("Url is: ");
            //System.out.print("Index of image url: "+begin);
            System.out.println("New Image Url: " + ht.substring(acBegin, ht.indexOf("<",acBegin)));
            return ht.substring(acBegin, ht.indexOf("<",acBegin));

        }
        */

        /*
        public String getQuote (String url) {
            //System.out.println("Begin QUote");
            String quote = "";
            String author = "";
            String result = "";
            //System.out.println("1");
            int indexQ = url.indexOf("<a href=\"/quotes/quotes/");
            int indexA = url.indexOf("<a href=\"/quotes/authors/");
            //System.out.println("2, indexes:"+indexQ+", "+indexA);
            quote = url.substring(url.indexOf(">",indexQ+1)+1,url.indexOf("</a>",indexQ));
            author = url.substring(url.indexOf(">",indexA+1)+1,url.indexOf("</a>",indexA));
            //System.out.println("3");
            result = quote+" -"+author;
            //System.out.println("End QUote");
            return result;
        }
    }
    */
    }

}
