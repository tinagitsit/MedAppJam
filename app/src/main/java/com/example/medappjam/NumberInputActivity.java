package com.example.medappjam;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static java.security.AccessController.getContext;

/**
 * Created by Kristen on 11/16/2016.
 */
public class NumberInputActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    EditText date, weight, hr, bp;
    String strDate, strWeight, strHR, strBP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numberinput);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Intent intent = getIntent();
        date = (EditText)findViewById(R.id.etInputDate);
        weight = (EditText)findViewById(R.id.etInputWeight);
        hr = (EditText)findViewById(R.id.etInputHR);
        bp = (EditText)findViewById(R.id.etInputBP);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("myNumbers") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void writeNumbers(View view) throws IOException {
        strWeight = weight.getText().toString();
        System.out.println(strWeight);
        FileOutputStream fout = openFileOutput("myNumbers.txt", MODE_PRIVATE);
        fout.write(strWeight.getBytes());
        fout.close();
        /*OutputStreamWriter osw = new OutputStreamWriter(fout);
        osw.write(strWeight);
        osw.flush();
        osw.close();*/
        Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();
    }

    public String readNumbers() throws IOException {
        String data;
        FileInputStream fin = null;
        try {
            fin = openFileInput("myNumbers.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader insr = new InputStreamReader(fin);
        BufferedReader bufferedReader = new BufferedReader(insr);
        StringBuffer strbuff = new StringBuffer();
        try {
            while((data = bufferedReader.readLine())!=null){
                strbuff.append(data +"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return strbuff.toString();
    }


}

