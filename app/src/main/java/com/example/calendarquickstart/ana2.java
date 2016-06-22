package com.example.calendarquickstart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Calendar;

public class ana2 extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana2);
        textView = (TextView) findViewById(R.id.textView);
        Bundle userLink = getIntent().getExtras();
        String code = userLink.getString("link");
        String quoteForUser = "";
        int next = 0;
        String author;
        //this is the line ----------------------------------------------------
        do {
            int be = code.indexOf("title=\"view quote\"",next);
            if (be == -1) {
                int dex = com.example.calendarquickstart.htmlCodeJ.getRandomQuote();
                quoteForUser = com.example.calendarquickstart.htmlCodeJ.preQ[dex];
                author = com.example.calendarquickstart.htmlCodeJ.preA[dex];
                break;
            }
            int begin = code.indexOf(">", be);
            int end = code.indexOf("</a>", begin);
            String quoteB = code.substring(begin + 1, end);
            String quote = StringEscapeUtils.unescapeHtml4(quoteB);
            int beAuthor = code.indexOf("title=\"view author\"");
            int beginAuthor = code.indexOf(">", beAuthor);
            int endAuthor = code.indexOf("</a>", beginAuthor);
            String authorB = code.substring(beginAuthor + 1, endAuthor);
            author = StringEscapeUtils.unescapeHtml4(authorB);
            quoteForUser = "\"" + quote + "\" -" + author;
            next = end +1;
            if (com.example.calendarquickstart.htmlCodeJ.quotes.contains(quoteForUser)) {
                int indexQuote = com.example.calendarquickstart.htmlCodeJ.quotes.indexOf(quoteForUser);
                String dateInArray = com.example.calendarquickstart.htmlCodeJ.dates.get(indexQuote);
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
                String currentDate = monthString+" "+da+", "+year;
                if (currentDate.equals(dateInArray)) {
                    break;
                }

            }
        }while (com.example.calendarquickstart.htmlCodeJ.quotes.contains(quoteForUser));






        Intent f = new Intent(this,result.class);
        f.putExtra("author",author);
        f.putExtra("quoteForUser",quoteForUser);
        startActivity(f);
        //textView.setText("\""+quote+"\" -"+author);

    }
    public void backHome(View view) {
        Intent b = new Intent(this, com.example.calendarquickstart.MainActivity.class);
        startActivity(b);
    }


}
