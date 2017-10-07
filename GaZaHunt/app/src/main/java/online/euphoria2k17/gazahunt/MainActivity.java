package online.euphoria2k17.gazahunt;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {

    ViewFlipper vf;
    TypewriterText tc;
    ProgressBar pb;
    int val=0;
    Params pp;
    String cr="0",finished="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        final ImageView img= (ImageView)findViewById(R.id.imageView);
        final Animation animation1= AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotate);
        final Animation animation2= AnimationUtils.loadAnimation(getBaseContext(),R.anim.antirotate);
        final Animation animation3= AnimationUtils.loadAnimation(getBaseContext(),android.R.anim.fade_out);

        img.startAnimation(animation2);
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                img.startAnimation(animation1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                img.startAnimation(animation3);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.setContentView(R.layout.activity_main);
                        vf=(ViewFlipper)MainActivity.this.findViewById(R.id.vflip);
                        vf.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this.getBaseContext(),android.R.anim.fade_in));
                        vf.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this.getBaseContext(),android.R.anim.fade_out));
                        tc=(TypewriterText)findViewById(R.id.clueView);
                        tc.setCharacterDelay(100);
                        pb=(ProgressBar)findViewById(R.id.pb1);

                        SharedPreferences sp = getSharedPreferences("gazahunt",MODE_PRIVATE);
                        val=sp.getInt("checkPoint",0);
                        System.err.println(">>>>>>>>>>>>>>>"+val);
                        finished=sp.getString("finish","-");
                        System.err.println(">>>>>>>>>>>>>>>"+finished);
                        if(pp==null) {
                            pp = new Params(finished);
                        }
                        cr=sp.getString("cur","0");
                        System.err.println(">>>>>>>>>>>>>>>"+cr);

                        if(val>0){
                            vf.setDisplayedChild(1);
                            pb.setProgress(val);
                            if(finished.length()==9){
                                tc.animateText(pp.getMsgs(0));
                                ImageButton ib=(ImageButton)findViewById(R.id.gocl);
                                ib.setVisibility(View.GONE);
                            }else{
                                tc.animateText(pp.getMsgs(Integer.parseInt(cr)));
                            }

                        }

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if((finished.length()==1) && (!finished.equals("-"))){
            SharedPreferences sp = getSharedPreferences("gazahunt",MODE_PRIVATE);
            val=sp.getInt("checkPoint",0);
            System.err.println(">>>>>>>>>>>>>>>"+val);
            finished=sp.getString("finish","-");
            System.err.println(">>>>>>>>>>>>>>>"+finished);
            if(pp==null) {
                pp = new Params(finished);
            }
            cr=sp.getString("cur","0");
            System.err.println(">>>>>>>>>>>>>>>"+cr);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    public void btnClick(View v){
        vf.setDisplayedChild(0);
    }
    public void btn1Click(View v){
        EditText et= (EditText)findViewById(R.id.aswr);
        if(val==0){
            if(et.getText().toString().equalsIgnoreCase("ILLUSION")){
                Toast.makeText(this,"Good Work",Toast.LENGTH_LONG).show();
                ClueProvider();
            }else{
                Toast.makeText(this,"Sorry You are Wrong",Toast.LENGTH_SHORT).show();
            }
        }else {
            if(et.getText().toString().equalsIgnoreCase(pp.getCls(Integer.parseInt(cr)))){
                Toast.makeText(this,"Good Work",Toast.LENGTH_LONG).show();
                ClueProvider();
            }else{
                Toast.makeText(this,"Sorry You are Wrong",Toast.LENGTH_SHORT).show();
            }
        }
        if(val==6){
            ImageButton ib=(ImageButton)findViewById(R.id.gocl);
            ib.setVisibility(View.GONE);
        }
        et.setText("");
    }

    public void ClueProvider(){
        val++;
        pb.setProgress(val);

        System.err.println("??"+finished.length());
        if(finished.length()==9){
            vf.setDisplayedChild(1);
            tc.animateText(pp.getMsgs(0));
        }else {
            if (finished.length() == 1 && finished.equals("-")) {
                finished = cr;
            } else {
                finished += "-" + cr;
            }

            pp.Rem(Integer.parseInt(cr));
            cr = String.valueOf(pp.getRand());
            System.err.println("????????/ "+cr);
            SharedPreferences sp = getSharedPreferences("gazahunt", MODE_PRIVATE);
            SharedPreferences.Editor etr = sp.edit();
            etr.putInt("checkPoint", val);

            etr.putString("finish", finished);
            etr.putString("cur", String.valueOf(cr));
            etr.commit();

            vf.setDisplayedChild(1);
            tc.animateText(pp.getMsgs(Integer.parseInt(cr)));
        }
        System.err.println(finished);
        System.err.println(cr);
        System.err.println(val);
        System.err.println(pp.getLen());
    }
}
