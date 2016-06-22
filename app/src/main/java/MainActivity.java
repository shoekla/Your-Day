package com.example.calendarquickstart;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
public class MainActivity extends Activity
        implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    private TextView quoteText;
    ProgressDialog mProgress;
    ImageView imageView;
    String monthString;
    int da;
    int year;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    SharedPreferences sharedPref;
    private static final String BUTTON_TEXT = "Call Google Calendar API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    LinearLayout activityLayout;
    LinearLayout imAc;
    Intent a;
    PendingIntent pendingIntent;

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref != null) {
/*
            String quotes = sharedPref.getString("quotes","");
            String arrQ[] = quotes.split("abir");
            String dates = sharedPref.getString("dates","");
            String arrD[] = quotes.split("abir");
            com.example.calendarquickstart.htmlCodeJ.quotes.clear();
            com.example.calendarquickstart.htmlCodeJ.dates.clear();
            for (int i = 0; i < arrD.length; i++) {
                com.example.calendarquickstart.htmlCodeJ.quotes.add(arrQ[i].trim());
                com.example.calendarquickstart.htmlCodeJ.dates.add(arrD[i].trim());
            }
            int noteH = sharedPref.getInt("noteHour",10);
            int noteM = sharedPref.getInt("noteMin",0);
            int coun  = sharedPref.getInt("count",0);
            boolean noti = sharedPref.getBoolean("notify",true);
            com.example.calendarquickstart.htmlCodeJ.notify = noti;
            com.example.calendarquickstart.htmlCodeJ.noteHour = noteH;
            com.example.calendarquickstart.htmlCodeJ.noteMin = noteM;
            com.example.calendarquickstart.htmlCodeJ.count = coun;


            //ArrayList<String> names = savedInstanceState.getStringArrayList("names");
        */
        }
        super.onCreate(savedInstanceState);
        activityLayout = new LinearLayout(this);

        imAc = new LinearLayout(this);
        LinearLayout.LayoutParams im = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        imAc.setLayoutParams(im);
        imAc.setOrientation(LinearLayout.VERTICAL);
        imAc.setPadding(16, 16, 16, 16);

        a = new Intent(MainActivity.this, ana1.class);
        com.example.calendarquickstart.htmlCodeJ.count = 0;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);
        quoteText = new TextView(this);
        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        new TextView(this);
        imageView = new ImageView(this);
        imAc.addView(imageView);
        quoteText.setPadding(16, 16, 16, 16);
        quoteText.setVerticalScrollBarEnabled(true);
        quoteText.setTextSize(40);
        quoteText.setMovementMethod(new ScrollingMovementMethod());
        activityLayout.addView(quoteText);
        mCallApiButton = (Button) findViewById(R.id.button);
        mOutputText = (TextView) findViewById(R.id.textView);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Analyzing your current schedule");

        setContentView(R.layout.content_home);
        NotificationManager can = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        can.cancel(2247);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        int i = preferences.getInt("numLaunch",1);
        if (i <2) {
            i++;
            editor.putInt("numLaunch",i);
            editor.commit();
            alarmMethod();
        }

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    public void slide(View view) {
        Intent s = new Intent(MainActivity.this, we.class);
        startActivity(s);
    }
    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        System.out.println("Back button was pressed");
        startActivity(i);
    }
    */

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
            a.putExtra("account",mCredential.getSelectedAccountName());
            System.out.println("Acount: " + mCredential.getSelectedAccountName());
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
            a.putExtra("account", mCredential.getSelectedAccountName());
        } else if (!isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
            a.putExtra("account",mCredential.getSelectedAccountName());
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */



/*

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //String names = listTime.getArrNames().toString();
        ArrayList<String> quo = com.example.calendarquickstart.htmlCodeJ.quotes;
        String quotes = "";
        for (int i = 0; i<quo.size();i++) {
            if (i == quo.size()-1) {
                quotes = quotes + quo.get(i);
            }
            else {
                quotes = quotes + quo.get(i) + "abir";
            }
        }
        ArrayList<String> dat = com.example.calendarquickstart.htmlCodeJ.dates;
        String dates = "";
        for (int i = 0; i<dat.size();i++) {
            if (i == quo.size()-1) {
                dates = dates + dat.get(i);
            }
            else {
                dates = dates + dat.get(i) + "abir";
            }
        }
        editor.putString("quotes",quotes);
        editor.putString("dates",dates);
        editor.putInt("count", com.example.calendarquickstart.htmlCodeJ.count);
        editor.putInt("noteHour", com.example.calendarquickstart.htmlCodeJ.noteHour);
        editor.putInt("noteMin", com.example.calendarquickstart.htmlCodeJ.noteMin);
        editor.putBoolean("notify", com.example.calendarquickstart.htmlCodeJ.notify);
        editor.commit();
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //String names = listTime.getArrNames().toString();
        ArrayList<String> quo = com.example.calendarquickstart.htmlCodeJ.quotes;
        String quotes = "";
        for (int i = 0; i<quo.size();i++) {
            if (i == quo.size()-1) {
                quotes = quotes + quo.get(i);
            }
            else {
                quotes = quotes + quo.get(i) + "abir";
            }
        }
        ArrayList<String> dat = com.example.calendarquickstart.htmlCodeJ.dates;
        String dates = "";
        for (int i = 0; i<dat.size();i++) {
            if (i == quo.size()-1) {
                dates = dates + dat.get(i);
            }
            else {
                dates = dates + dat.get(i) + "abir";
            }
        }
        editor.putString("quotes",quotes);
        editor.putString("dates",dates);
        editor.putInt("count", com.example.calendarquickstart.htmlCodeJ.count);
        editor.putInt("noteHour", com.example.calendarquickstart.htmlCodeJ.noteHour);
        editor.putInt("noteMin", com.example.calendarquickstart.htmlCodeJ.noteMin);
        editor.putBoolean("notify", com.example.calendarquickstart.htmlCodeJ.notify);
        editor.commit();
        super.onDestroy();
    }

*/










    public void getQuoteForUser(View v) {

        mProgress.show();
        getResultsFromApi();
        getResultsFromApi();
    }
    public void setting(View view) {
        Intent s = new Intent(MainActivity.this,set.class);
        startActivity(s);
    }
    public void changeA(View view) {
        startActivityForResult(
                mCredential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
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

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    private void alarmMethod() {
        Intent myIntent= new Intent(MainActivity.this, Note.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, com.example.calendarquickstart.htmlCodeJ.noteHour);
        calendar.set(Calendar.MINUTE, com.example.calendarquickstart.htmlCodeJ.noteMin);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);  //set repeating every 24 hours
    }
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
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
                getHTML(linkU);
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
                            MainActivity.REQUEST_AUTHORIZATION);
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
        public void getHTML(String url) {
            System.out.println("Begin HTML");
            final String[] d = new String[1];
            Ion.with(getApplicationContext())
                    .load(url)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            Intent a = new Intent(MainActivity.this, ana1.class);
                            a.putExtra("htmlCode", result);
                            mProgress.dismiss();
                            startActivity(a);
                        }
                    });
        }


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
        private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView imageView;

            public DownLoadImageTask(ImageView imageView) {
                this.imageView = imageView;
            }

            /*
                doInBackground(Params... params)
                    Override this method to perform a computation on a background thread.
             */
            protected Bitmap doInBackground(String... urls) {
                String urlOfImage = urls[0];
                Bitmap logo = null;
                try {
                    InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                    logo = BitmapFactory.decodeStream(is);
                } catch (Exception e) { // Catch the download exception
                    e.printStackTrace();
                }
                return logo;
            }

            /*
                onPostExecute(Result result)
                    Runs on the UI thread after doInBackground(Params...).
             */
            protected void onPostExecute(Bitmap result) {
                imageView.setImageBitmap(result);
            }
        }
    }
}