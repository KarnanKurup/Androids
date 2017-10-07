package online.euphoria2k17.gazahunt;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;

/**
 * Created by Hare Krishna on 8/3/2017.
 */

public class TypewriterText extends AppCompatTextView{

    private CharSequence mText;
    private int mIndex;
    private long mDelay = 200; //Default 500ms delay

    public TypewriterText(Context context) {
        super(context);
    }

    public TypewriterText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    private Handler mHandler=new Handler();
    private Runnable chaRunnable=new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0,mIndex++));
            if(mIndex <= mText.length()) {
                mHandler.postDelayed(chaRunnable, mDelay);
            }
        }
    };

    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;

        setText("");
        mHandler.removeCallbacks(chaRunnable);
        mHandler.postDelayed(chaRunnable, mDelay);
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }
}
