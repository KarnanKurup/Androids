package com.madhouse.vpesa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.madhouse.vpesa.core.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PasswordScreen extends AppCompatActivity {

    String stat;
    boolean newscreen=false;
    EditText et,etotp;
    Button bnext;
    JSONParser jsonParser;


    boolean workeron=false;
    boolean otpover=false;
    String id,name,pho;
    int cheking=0;

    final String urlOTP="http://192.168.43.232:85/vpesa/otpvalues_1.php";
    final String urlReg="http://192.168.43.232:85/vpesa/regvalues_1.php";
    final String urlLog="http://192.168.43.232:85/vpesa/logvalues_1.php";
    final String urlPin="http://192.168.43.232:85/vpesa/pinvalues_1.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i =getIntent();
        jsonParser=new JSONParser();

        stat=i.getStringExtra("status");
        if(stat.equals("login")){
            pho=i.getStringExtra("ph");
            setContentView(R.layout.conf_pass_screen);
            et = (EditText) findViewById(R.id.passtxt);
            bnext = (Button) findViewById(R.id.bnext);
            stat="OLD";
            newscreen=true;
        }else {
            id = i.getStringExtra("id");
            name = i.getStringExtra("name");
            pho = i.getStringExtra("ph");
            if (stat != null && stat.equals("OLD")) {
                newscreen = true;
                setContentView(R.layout.conf_pass_screen);
                et = (EditText) findViewById(R.id.passtxt);
                bnext = (Button) findViewById(R.id.bnext);
                saveNumber();

            } else {
                setContentView(R.layout.otpenter);
                etotp = (EditText) findViewById(R.id.etOtp);
            }
            et = (EditText) findViewById(R.id.passtxt);
            bnext = (Button) findViewById(R.id.bnext);
            if (workeron==false && (stat==null || stat.equals("OLD")==false)) {
                cheking = 1;
                new NetWorker().execute();
            }
        }
    }

    private void saveNumber(){
        SharedPreferences pref=getSharedPreferences("vpesa",MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("mob",pho);
        editor.commit();
    }
    public void btnClear(View v){
        et.setText("");
        bnext.setEnabled(false);
    }
    public void btnOtp(View v){
        //OTP Call
        otpover=true;
        otpcode=etotp.getText().toString();
        cheking=2;
        new NetWorker().execute();
        if(suc.equals("ok")) {
            setContentView(R.layout.new_pass_screen);
            et = (EditText) findViewById(R.id.passtxt);
            bnext = (Button) findViewById(R.id.bnext);
        }else{
            Toast.makeText(this,"Wrong OTP!",Toast.LENGTH_SHORT).show();
            etotp.setText("");
        }
    }

    String otpcode="";
    String passcode1="";
    String passcode2="";
    public void btnNext(View v){
        if(newscreen==false){
            passcode1=et.getText().toString();
            setContentView(R.layout.conf_pass_screen);
            et = (EditText) findViewById(R.id.passtxt);
            bnext = (Button) findViewById(R.id.bnext);
            newscreen=true;
        }else{
            if(stat!=null && stat.equals("OLD")){
                //Login CAll
                passcode2=et.getText().toString();
                cheking=4;
                new NetWorker().execute();
                if(suc.equals("ok")){
                    Intent i =new Intent(this,ClientActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }else{
                //Register Call
                passcode2=et.getText().toString();
                if(passcode1.equals(passcode2)){
                    cheking=3;
                    new NetWorker().execute();
                    if(suc.equals("ok")){
                        Toast.makeText(this,"Please Login with the Passcode",Toast.LENGTH_SHORT).show();

                        //storing phone no for future login
                        saveNumber();

                        et.setText("");
                        stat="OLD";
                    }
                }else{
                    Toast.makeText(this,"Passcode is wrong",Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.new_pass_screen);
                    newscreen=false;
                    et = (EditText) findViewById(R.id.passtxt);
                    bnext = (Button) findViewById(R.id.bnext);
                }
            }
        }

    }

    String suc="";
    public class NetWorker extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            workeron=true;
            Looper.prepare();
            List<NameValuePair> params=new ArrayList<>();
            if(cheking==1) {
                System.out.println(".......... ?"+id+" "+pho+" "+name);
                params.add(new BasicNameValuePair("uid",id));
                params.add(new BasicNameValuePair("ph",pho));
                params.add(new BasicNameValuePair("Cname",name));
                JSONObject json = jsonParser.makeHttpRequest(urlReg, "POST", params);
                try {
                    suc = json.getString("status");
                } catch (Exception e) {
                    System.err.println("+++++ 1 " + e);
                    Toast.makeText(PasswordScreen.this, "Ooohh.. Server Not Available :(", Toast.LENGTH_LONG).show();
                }
            }else if(cheking==2){
                System.out.println(".......... ??"+otpcode);
                params.add(new BasicNameValuePair("otp",otpcode));
                params.add(new BasicNameValuePair("ph",pho));
                JSONObject json = jsonParser.makeHttpRequest(urlOTP, "POST", params);
                try {
                    suc = json.getString("status");
                } catch (Exception e) {
                    System.err.println("+++++ 2 " + e);
                    Toast.makeText(PasswordScreen.this, "Ooohh.. Server Not Available :(", Toast.LENGTH_LONG).show();
                }
            }
            else if(cheking==3){
                System.out.println(".......... ???"+passcode2);
                params.add(new BasicNameValuePair("pin",passcode2));
                params.add(new BasicNameValuePair("Cpin",passcode2));
                params.add(new BasicNameValuePair("uid",id));
                params.add(new BasicNameValuePair("ph",pho));
                params.add(new BasicNameValuePair("name",name));
                JSONObject json = jsonParser.makeHttpRequest(urlPin, "POST", params);
                try {
                    suc = json.getString("status");
                } catch (Exception e) {
                    System.err.println("+++++ 2 " + e);
                    Toast.makeText(PasswordScreen.this, "Ooohh.. Server Not Available :(", Toast.LENGTH_LONG).show();
                }
            }else if(cheking==4){

                System.out.println(".......... ????"+pho+" "+passcode2);
                params.add(new BasicNameValuePair("ph",pho));
                params.add(new BasicNameValuePair("pin",passcode2));
                JSONObject json = jsonParser.makeHttpRequest(urlLog, "POST", params);
                try {
                    suc = json.getString("status");
                } catch (Exception e) {
                    System.err.println("+++++ 3 " + e);
                    Toast.makeText(PasswordScreen.this, "Ooohh.. Server Not Available :(", Toast.LENGTH_LONG).show();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(PasswordScreen.this,suc,Toast.LENGTH_SHORT).show();
            if(suc.equals("ok") && cheking==4){
                Intent i =new Intent(PasswordScreen.this,ClientActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                PasswordScreen.this.finish();
            }
            super.onPostExecute(s);
        }
    }

    public void btn1Click(View v){
        String s= et.getText().toString();
        if(s.length()<6){
            et.setText(s+"1");
            if(et.getText().length()==6)
                bnext.setEnabled(true);
        }
    }
    public void btn2Click(View v){
        String s= et.getText().toString();
        if(s.length()<6){
            et.setText(s+"2");
            if(et.getText().length()==6)
                bnext.setEnabled(true);
        }
    }
    public void btn3Click(View v){
        String s= et.getText().toString();
        if(s.length()<6){
            et.setText(s+"3");
            if(et.getText().length()==6)
                bnext.setEnabled(true);
        }
    }
    public void btn4Click(View v){
        String s= et.getText().toString();
        if(s.length()<6){
            et.setText(s+"4");
            if(et.getText().length()==6)
                bnext.setEnabled(true);
        }
    }
    public void btn5Click(View v){
        String s= et.getText().toString();
        if(s.length()<6){
            et.setText(s+"5");
            if(et.getText().length()==6)
                bnext.setEnabled(true);
        }
    }
    public void btn6Click(View v){
        String s= et.getText().toString();
        if(s.length()<6){
            et.setText(s+"6");
            if(et.getText().length()==6)
                bnext.setEnabled(true);
        }
    }
    public void btn7Click(View v){
        String s= et.getText().toString();
        if(s.length()<6){
            et.setText(s+"7");
            if(et.getText().length()==6)
                bnext.setEnabled(true);
        }
    }
    public void btn8Click(View v){
        String s= et.getText().toString();
        if(s.length()<6){
            et.setText(s+"8");
            if(et.getText().length()==6)
                bnext.setEnabled(true);
        }
    }
    public void btn9Click(View v){
        String s= et.getText().toString();
        if(s.length()<6){
            et.setText(s+"9");
            if(et.getText().length()==6)
                bnext.setEnabled(true);
        }
    }
    public void btn0Click(View v){
        String s= et.getText().toString();
        if(s.length()<6){
            et.setText(s+"0");
            if(et.getText().length()==6)
                bnext.setEnabled(true);
        }
    }

}
