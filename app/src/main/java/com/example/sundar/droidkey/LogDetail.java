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
         final AlertDialog alertDialog;

        Intent intent = this.getIntent();
        final String filename= intent.getStringExtra("name");

        final TextView disfile = (TextView) findViewById(R.id.textView_dislog);

        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Please Wait")
                .setCancelable(true)
                .create();
        alertDialog.setMessage("Retrieving log from device");

            alertDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    URI url;
                    String[] params = new String[2];
                    try {
                        url = new URI("http://" + "192.168.4.1" + "/?Log");
                        getLog mgetLog = new getLog(getApplicationContext());
                        params[0] = url.toString();
                        params[1] = "Could not reach the lock. Check if the lock is in your network and turned on";
                        String hi = mgetLog.execute(params).get();
                        if (!(hi.equals("NIL"))||hi==null) {

                            FileOutputStream fOut = new FileOutputStream(filename, true);
                            fOut.write(hi.getBytes());
                            fOut.close();
                        } else if (hi.equals("NIL")) {
                            break;
                        }
                        Log.e("Hi", url.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                String temp = "";

                FileInputStream fin = null;
                try {
                    int c;

                    fin = new FileInputStream(filename);
                    InputStreamReader inputStreamReader = new InputStreamReader(fin);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        //sb.append(line);
                        temp.concat(line);
                    }
//                    while ((c = fin.read()) != -1) {
//                        temp = temp + Character.toString((char) c);
//                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final String temp1 = temp;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disfile.setText(temp1);


                    }
                });

                alertDialog.dismiss();


            }
        }).start();








    }
}

class getLog extends AsyncTask<String, Void, String> {


    String serverResponse="";
    String line;
    String responsemsg;
    Long flag;

    public getLog(Context context)
    {



    }



    @Override
    protected void onPreExecute() {





    }

    @Override
    protected String doInBackground(String... params) {
        URI url = URI.create(params[0]);
        responsemsg = params[1];


        // HttpURLConnection urlConnection = null;


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




    }



}
