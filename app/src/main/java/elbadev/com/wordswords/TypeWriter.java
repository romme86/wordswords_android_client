package elbadev.com.wordswords;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import java.util.Random;


/**
 * Created by frome on 12/09/2017.
 */

public class TypeWriter extends AppCompatTextView {

    private CharSequence mText;
    private int mIndex;
    public int lowBound = 150; // in ms
    public int upBound = 300; // in ms

    public TypeWriter(Context context) {
        super(context);
    }

    public TypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler mHandler = new Handler();

    private Runnable characterAdder = new Runnable() {

        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
         //   GlobalState.getTypingSound().start();
            Random r = new Random();
            int i1 = r.nextInt(upBound - lowBound) + lowBound;
            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, i1);
            }
        }
    };

    public void animateText(CharSequence txt) {
        mText = txt;
        mIndex = 0;
        Random r = new Random();
        int i1 = r.nextInt(upBound - lowBound) + lowBound;
        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, i1);
    }


}