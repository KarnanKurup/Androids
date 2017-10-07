package com.madhouse.vpesa;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.madhouse.vpesa.core.CommonValues;
import com.madhouse.vpesa.core.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Runnable {

    JSONParser jsonParser=new JSONParser();
    final String url="http://192.168.43.232:85/vpesa/testing.php";

    EditText etvuid,etvname,etvphone;

    String uid,uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!CommonValues.afterScan) {
            Thread tr = new Thread(this);
            tr.start();
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        if(CommonValues.afterScan){
            setContentView(R.layout.activity_main);
            etvuid=(EditText)findViewById(R.id.etvuid);
            etvname=(EditText)findViewById(R.id.etvname);
            etvphone=(EditText)findViewById(R.id.etvphone);
            etvuid.setText(uid);
            etvname.setText(uname);
        }else {
            setContentView(R.layout.splash);
        }

    }

    public void showHelp(View v){
        Snackbar.make(v,"Click the Camera button to scan Aadhar Card",4000).show();
    }


    private String getSavedPhone(){
        SharedPreferences pref=getSharedPreferences("vpesa",MODE_PRIVATE);
        String mobile=pref.getString("mob","");
        return mobile;
    }

    private void setUi(){
        //check not a new user

        String passcode=getSavedPhone();

        System.out.println(passcode+"||||||||||||||||||");
        if(passcode.equals("")) {
            CommonValues.afterScan = true;
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

        }else{
            Intent i= new Intent(this,PasswordScreen.class);
            i.putExtra("status","login");
            i.putExtra("ph",passcode);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    public void floatingClick(View v){
        Intent qr=new Intent("la.droid.qr.scan");
        qr.putExtra("la.droid.qr.complete",true);
        try {
            startActivityForResult(qr, 10010);
        }catch (ActivityNotFoundException ex){
            Snackbar make = Snackbar.make(v,"QR Droid is not installed!!",4000);
            make.setAction("Click Here to install it", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://qrdroid.com/get.php")));
                }
            });
            make.show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==10010 && resultCode==RESULT_OK){

            String result=data.getExtras().getString("la.droid.qr.result");
            System.out.println(">>>>>>>>>>>>>"+result);
            uid=result.substring(result.indexOf("uid=")+5,result.indexOf(" name")-1);
            uname=result.substring(result.indexOf("name=")+6,result.indexOf(" gender")-1);
            System.out.println(">>>>>>>>>>>>>111111111"+uid);
        }else{
            System.out.println("ffffffffffffffffff" +requestCode+" "+resultCode+" "+RESULT_CANCELED);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void btnBack(View v){
        etvuid=(EditText)findViewById(R.id.etvuid);
        etvname=(EditText)findViewById(R.id.etvname);
        etvphone=(EditText)findViewById(R.id.etvphone);
        String uid=etvuid.getText().toString();
        String ename=etvname.getText().toString();
        String ph=etvphone.getText().toString();

        if(uid.equals("") || ename.equals("")||ph.equals("")){
            Snackbar.make(v,"Please fill all the fields..",4000).show();
        }else{
            Intent i = new Intent(this,PasswordScreen.class);
            i.putExtra("status","OLD");
            i.putExtra("ph",ph);
            i.putExtra("id",uid);
            i.putExtra("name",ename);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    public void btnNew(View v){
        etvuid=(EditText)findViewById(R.id.etvuid);
        etvname=(EditText)findViewById(R.id.etvname);
        etvphone=(EditText)findViewById(R.id.etvphone);
        String uid=etvuid.getText().toString();
        String ename=etvname.getText().toString();
        String ph=etvphone.getText().toString();

        if(uid.equals("") || ename.equals("")||ph.equals("")){
            Snackbar.make(v,"Please fill all the fields..",4000).show();
        }else{
            Intent i = new Intent(this,PasswordScreen.class);
            i.putExtra("status","NEW");
            i.putExtra("ph",ph);
            i.putExtra("id",uid);
            i.putExtra("name",ename);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            setUi();
        }
    };


    @Override
    public void run() {
        Looper.prepare();
        initaializNetwork();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mHandler.sendEmptyMessage(0);
    }

    private void initaializNetwork() {
        new NetWorker().execute();
    }

    public class NetWorker extends AsyncTask<String,String,String>{

        ProgressDialog pd;
        String suc="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pd=new ProgressDialog(MainActivity.this);
            //pd.setIndeterminate(false);
            //pd.setCancelable(false);
            //pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            //pd.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            Looper.prepare();
            String uid="dasda";
            List<NameValuePair> params=new ArrayList<>();

            params.add(new BasicNameValuePair("newuser",uid));

            JSONObject json=jsonParser.makeHttpRequest(url,"POST",params);
            try{
                suc=json.getString("msg");
            }catch (Exception e){
                System.err.println(e+" >>>>>>>>>>>>");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //pd.dismiss();

            if(suc==null || suc.equals("")) {
                Toast.makeText(MainActivity.this,"Ooohh.. Server Not Available :(",Toast.LENGTH_LONG).show();
                MainActivity.this.finish();
            }else{
                Toast.makeText(MainActivity.this,suc,Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}
