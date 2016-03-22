package com.example.sundar.droidkey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class LogDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_detail);

        Intent intent = this.getIntent();
        String filename= intent.getStringExtra("name");

        TextView disfile = (TextView) findViewById(R.id.textView_dislog);


        while(true){
            URI url;
            String[] params = new String[2];
            try {
                url = new URI("http://" + "192.168.4.1" + "/?Log");
                getLog mgetLog = new getLog(this);
                params[0]=url.toString();
                params[1]="Could not reach the lock. Check if the lock is in your network and turned on";
                String hi = mgetLog.execute(params).get();
                if(!(hi.equals("NIL"))){

                    FileOutputStream fOut = openFileOutput(filename,MODE_PRIVATE);
                    fOut.write(hi.getBytes());
                    fOut.close();
                }else if(hi.equals("NIL")){
                    break;
                }
                Log.e("Hi", url.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        String temp="";

        FileInputStream fin = null;
        try {
            int c;

            fin = openFileInput(filename);
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        disfile.setText(temp);



    }
}

class getLog extends AsyncTask<String, Void, String> {

    private AlertDialog alertDialog;
    String serverResponse="";
    String line;
    String responsemsg;
    Long flag;

    public getLog(Context context)
    {


        alertDialog = new AlertDialog.Builder(context)
                .setTitle("Please Wait")
                .setCancelable(true)
                .create();
    }



    @Override
    protected void onPreExecute() {




        alertDialog.setMessage("Retrieving log from device");
        if(!alertDialog.isShowing())
        {
            alertDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        URI url = URI.create(params[0]);
        responsemsg = params[1];


        // HttpURLConnection urlConnection = null;
        alertDialog.setMessage("Retrieving log from device");
        if(!alertDialog.isShowing())
        {
            alertDialog.show();
        }

        try {
            HttpParams httpParameters = new BasicHttpParams();

            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//
            int timeoutSocket = 15000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);

            HttpGet getRequest = new HttpGet();
            getRequest.setURI(url);
            HttpResponse response = httpclient.execute(getRequest);
            Log.v("Hi", url.toString());



            InputStream content = null;
            content = response.getEntity().getContent();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    content
            ));
            line = in.readLine();
            while (!(line==null)) {
                if (line.isEmpty()) {
                    break;
                }
                serverResponse += line;
                line = in.readLine();
            }

            content.close();
            flag=new Long(1);
        } catch (ClientProtocolException e) {
            // HTTP error
            serverResponse = e.getMessage();
            e.printStackTrace();
            Log.v("Hi", e.toString());
            flag=new Long(1);
        } catch (IOException e) {
            // IO error
            serverResponse = e.getMessage();
            e.printStackTrace();
            Log.v("Hi", e.toString());
            flag=new Long(1);
        }

        return serverResponse;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        MainActivity.flagerror = flag.intValue();

        if(flag==0) {
            alertDialog.setMessage(responsemsg + serverResponse);
        }else if(flag==1)
            alertDialog.setMessage(serverResponse);

        if(!alertDialog.isShowing())
        {
            alertDialog.show(); // show dialog
        }
    }



}
