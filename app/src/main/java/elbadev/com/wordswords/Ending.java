package elbadev.com.wordswords;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Ending extends Activity {



    @Override
    public void onBackPressed(){
        Toast.makeText(this,  GlobalState.getRandomWisenessBackButton(), Toast.LENGTH_SHORT).show();
        //super.onBackPressed();
    }


    //   <<<<<<<<<   INIZIO LISTA EMETTITORI    >>>>>>>>>




    //<<<<<<<<<   FINE LISTA EMETTITORI    >>>>>>>>>

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ending);
//        GlobalState.getTypewriter_multi_sound().start();
        final Animation button_translate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation button_scale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        final AnimationSet button_as = new AnimationSet(true);
        button_as.addAnimation(button_translate);
        button_as.addAnimation(button_scale);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Button vai_menu = (Button) findViewById(R.id.button_home);
        vai_menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                GlobalState.getmSocket().emit("distruggi_partita", GlobalState.getId_stanza_attuale());
            }
        });


        TextView tf = (TextView) findViewById(R.id.testo_fine);
        tf.setText(GlobalState.getVincitore()  + " con " + GlobalState.getPunti_vincitore() + " punti!!!");

    }



}
