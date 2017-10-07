package online.euphoria2k17.gazahunt;

import java.util.Random;

/**
 * Created by Hare Krishna on 8/4/2017.
 */

public class Params {
    private  String[] msgs= new String[6];
    private  String[] cls= new String[6];

    public Params(String s) {
        msgs[0]="EUPHORIA2K17 - Double your Happiness";
        cls[0]="";
        msgs[1]="My uses are changing, but I still remain the same. My interior is quiet, " +
                "and stories are my game and it has a million stories" +
                " but can't tell them, what am I? 123456";
        cls[1]="346512";

        msgs[2]="Whenever the waiter comes with the check, shouts of \"You pay, it's your turn\" " +
                "rings in. It consists of 9 letters, with 4 consonants." ;
        cls[2]="SUJATA";

        msgs[3]="To The Greater Glory of God";
        cls[3]="Mother of Mount Carmel";

        msgs[4]="A lion tries to attack a sheep and 4 persons, A white bird rescued them. Two witnesses...";
        cls[4]="MANOJ";

        msgs[5]="You take shots at me, but I never die; a man named Jordan used me to fly.\nRun....Dribble....Jump....";
        cls[5]="FR. MOSES TROPHY";


        if(!s.equals("-")) {
            String[] ids = s.split("-");
            System.err.println(ids.length);
            for (int i = 1; i < ids.length; i++) {
                msgs[Integer.parseInt(ids[i])]="-";
                cls[Integer.parseInt(ids[i])]="-";
            }
        }

    }
    public int getLen(){
        return msgs.length;
    }
    public int getRand(){
        if(msgs.length>1) {
            Random r = new Random();
            int c=r.nextInt(msgs.length);
            while (msgs[c].equals("-") || c==0){
                c=r.nextInt(msgs.length);
            }
            return c;
        }
        return -1;
    }
    public String getMsgs(int i){
        return msgs[i];
    }
    public String getCls(int i){
        return cls[i];
    }
    public void Rem(int indx){
        msgs[indx]="-";
        cls[indx]="-";
    }

}
