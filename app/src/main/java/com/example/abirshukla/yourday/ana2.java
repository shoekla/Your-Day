package com.example.abirshukla.yourday;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringEscapeUtils;

public class ana2 extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ana2);
        textView = (TextView) findViewById(R.id.textView);
        Bundle userLink = getIntent().getExtras();
        String code = userLink.getString("link");
        String quoteForUser = "";
        int next = 0;
        String author;
        //this is the line ----------------------------------------------------

            int be = code.indexOf("title=\"view quote\"",next);
            if (be == -1) {
                quoteForUser = "Erro Quote";
                author = "Abir";

            }
        else {
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
                next = end + 1;
            }







        Intent f = new Intent(this,result.class);
        f.putExtra("author",author);
        System.out.println("LinkA Quote: "+quoteForUser);
        f.putExtra("quoteForUser",quoteForUser);
        startActivity(f);
        //textView.setText("\""+quote+"\" -"+author);

    }
    public void backHome(View view) {
        Intent b = new Intent(this, MainActivity.class);
        startActivity(b);
    }


}
