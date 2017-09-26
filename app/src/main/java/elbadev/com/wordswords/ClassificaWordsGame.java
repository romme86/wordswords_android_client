package elbadev.com.wordswords;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by frome on 26/08/2017.
 */

public class ClassificaWordsGame extends Activity {

    final AnimationSet button_as = new AnimationSet(true);


    @Override
    public void onBackPressed(){
        Toast.makeText(this,  GlobalState.getRandomWisenessBackButton(), Toast.LENGTH_SHORT).show();
        //super.onBackPressed();
    }


    //   <<<<<<<<<   INIZIO LISTA EMETTITORI    >>>>>>>>>

    //Il server avverte che tutti hanno scritto la frase
    private Emitter.Listener on_1 = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            System.out.println("WORDSWORDS_LOG: uuuuuu");


            JSONObject obj = (JSONObject) args[0];



        }
    };

    //Gestione ultimo turno
    private Emitter.Listener on_ultimo_turno = new Emitter.Listener() {
        @Override
        public void call(Object... args) {


                System.out.println("WORDSWORDS_LOG: arrivato ultimo turno");
                GlobalState.setUltimo_turno(1);

        }
    };

    //Lista vincitori
    private Emitter.Listener on_who_win = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject obj = (JSONObject) args[0];

            try
            {
//                Iterator<String> temp = obj.keys();
//                while (temp.hasNext()) {
//                    String key = temp.next();
//                    Object profilo = obj.get(key);
//
//                    JSONObject pro = (JSONObject) profilo;
//
//
//
//                    System.out.println("WORDSWORDS_LOG: <<<>>> Yes " + pro.getString("nome"));
//                }

                String b1 = obj.getString("nome");

                System.out.println("WORDSWORDS_LOG: +++++++ " + b1);

                String[] spirit = b1.split(",");

                if(spirit.length > 1)
                {
                    GlobalState.setVincitore("I vincitori sono " + b1);
                }
                else
                {
                    GlobalState.setVincitore("Il vincitore è " + b1);
                }

                GlobalState.setPunti_vincitore(obj.getInt("punti"));

            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }

        }
    };



    //<<<<<<<<<   FINE LISTA EMETTITORI    >>>>>>>>>

    //INIZIO HANDLER


    //FINE HANDLER



    List<String> lista_voti = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Animation button_translate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation button_scale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.layout_classifica);
//        GlobalState.getTypewriter_multi_sound().start();
        final TypeWriter tw = (TypeWriter) findViewById(R.id.id_classifica);
        tw.setText("");
        tw.animateText("Classifica");

        button_as.addAnimation(button_translate);
        button_as.addAnimation(button_scale);
        GlobalState.setUltimo_turno(0);

        final ProgressBar pb = (ProgressBar) findViewById(R.id.load_classifica);
        pb.setVisibility(View.GONE);

        try {
            Iterator<String> temp = GlobalState.getClassifica().keys();



            while (temp.hasNext()) {




                String key = temp.next();
                Object profilo = GlobalState.getClassifica().get(key);
                JSONObject pro = (JSONObject) profilo;

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                int larg = (60*width)/100;

                if(GlobalState.getMia_email().equals(pro.getString("nome")))
                {
                    GlobalState.setMiei_punti(pro.getInt("points"));
                }


                TableLayout tabella = (TableLayout) findViewById(R.id.tab);


                TableRow riga = new TableRow(this);

                TableRow.LayoutParams aParams = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                riga.setLayoutParams(aParams);
                riga.setGravity(Gravity.CENTER);

                TextView nome = new TextView(this);
                nome.setText(pro.getString("nome"));
                nome.setGravity(Gravity.CENTER);
                nome.setPadding(2,10,2,10);
                riga.addView(nome);

                TextView punti = new TextView(this);
                punti.setText(pro.getString("points"));
                punti.setGravity(Gravity.CENTER);
                punti.setPadding(2,10,2,10);
                riga.addView(punti);

                TextView voto = new TextView(this);
                voto.setWidth(larg);
                voto.setLines(1);
                voto.setPadding(2,10,2,10);
                voto.setHorizontallyScrolling(true);
                voto.setMovementMethod(new ScrollingMovementMethod());
                voto.setText(pro.getString("voto").replace("[","").replace("]","").replace("\"",""));
                System.out.println("WORDSWORDS_LOG: .......... " + pro.getString("voto"));
                riga.addView(voto);


                tabella.addView(riga, new TableLayout.LayoutParams());


                TableRow riga2 = new TableRow(this);

                TableRow.LayoutParams aParams2 = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                aParams2.span = 3;
                riga2.setLayoutParams(aParams2);
                riga2.setGravity(Gravity.CENTER);



                TextView frase = new TextView(this);
                frase.setText(pro.getString("frase"));
                frase.setGravity(Gravity.CENTER);
                frase.setTextColor(Color.BLACK);
                riga2.addView(frase);

                tabella.addView(riga2, new TableLayout.LayoutParams());

                TableRow riga_vuota = new TableRow(this);
                TextView frase_vuota = new TextView(this);
                frase_vuota.setText("");
                frase_vuota.setGravity(Gravity.CENTER);

                riga_vuota.addView(frase_vuota);

                tabella.addView(riga_vuota, new TableLayout.LayoutParams());


               System.out.println("WORDSWORDS_LOG: ........ fine ciclo");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //GESTIONE SOCKET

        GlobalState.getmSocket().on("1", on_1).on("ultimo_turno", on_ultimo_turno).on("who_win", on_who_win);

        //Bottone Avanzamento o Fine
        final Button foward = (Button) findViewById(R.id.button_classifica);


        foward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                GlobalState.getmSocket().emit("prossimo_turno", "prossimo_turno");
                foward.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
            }
        });


        final Button end = (Button) findViewById(R.id.button_fine);
        end.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                GlobalState.getmSocket().emit("fine_partita", "fine_partita");

                Intent i = new Intent(ClassificaWordsGame.this, Ending.class);
                startActivity(i);

            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.layout_classifica);
        final Animation button_translate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation button_scale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
//        GlobalState.getTypewriter_multi_sound().start();

        button_as.addAnimation(button_translate);
        button_as.addAnimation(button_scale);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        final ProgressBar pb = (ProgressBar) findViewById(R.id.load_classifica);
        pb.setVisibility(View.GONE);

        try {
            Iterator<String> temp = GlobalState.getClassifica().keys();



            while (temp.hasNext()) {



                String key = temp.next();
                Object profilo = GlobalState.getClassifica().get(key);
                JSONObject pro = (JSONObject) profilo;

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                int larg = (60*width)/100;

                TableLayout tabella = (TableLayout) findViewById(R.id.tab);


                TableRow riga = new TableRow(this);

                TableRow.LayoutParams aParams = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                riga.setLayoutParams(aParams);
                riga.setGravity(Gravity.CENTER);

                TextView nome = new TextView(this);
                nome.setText(pro.getString("nome"));
                nome.setGravity(Gravity.CENTER);
                nome.setPadding(2,10,2,10);
                riga.addView(nome);

                TextView punti = new TextView(this);
                punti.setText(pro.getString("points"));
                punti.setGravity(Gravity.CENTER);
                punti.setPadding(2,10,2,10);
                riga.addView(punti);

                TextView voto = new TextView(this);
                voto.setWidth(larg);
                voto.setLines(1);
                voto.setPadding(2,10,2,10);
                voto.setHorizontallyScrolling(true);
                voto.setMovementMethod(new ScrollingMovementMethod());
                voto.setText(pro.getString("voto").replace("[","").replace("]","").replace("\"",""));
                System.out.println("WORDSWORDS_LOG: .......... " + pro.getString("voto"));
                riga.addView(voto);


                tabella.addView(riga, new TableLayout.LayoutParams());

                TableRow riga2 = new TableRow(this);
                TableRow.LayoutParams aParams2 = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                aParams2.span = 3;
                riga2.setLayoutParams(aParams2);
                riga2.setGravity(Gravity.CENTER);


                TextView frase = new TextView(this);
                frase.setText(pro.getString("frase"));
                frase.setGravity(Gravity.CENTER);
                frase.setTextColor(Color.BLACK);
                frase.setGravity(Gravity.CENTER);

                riga2.addView(frase);

                tabella.addView(riga2, new TableLayout.LayoutParams());


                TableRow riga_vuota = new TableRow(this);
                TextView frase_vuota = new TextView(this);
                frase_vuota.setText("");
                frase_vuota.setGravity(Gravity.CENTER);

                riga_vuota.addView(frase_vuota);

                tabella.addView(riga_vuota, new TableLayout.LayoutParams());


                System.out.println("WORDSWORDS_LOG: ........ fine ciclo");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Bottone Avanzamento o Fine
        final Button foward = (Button) findViewById(R.id.button_classifica);


        foward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                GlobalState.getmSocket().emit("prossimo_turno", "prossimo_turno");
                foward.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
            }
        });


        if(GlobalState.getUltimo_turno() == 1)
        {
            Button bfin1 = (Button) findViewById(R.id.button_classifica);
            bfin1.setVisibility(View.GONE);

            Button bfin2= (Button) findViewById(R.id.button_fine);
            bfin2.setVisibility(View.VISIBLE);
        }

        final Button end = (Button) findViewById(R.id.button_fine);
        end.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                GlobalState.getmSocket().emit("fine_partita", "fine_partita");

                Intent i = new Intent(ClassificaWordsGame.this, Ending.class);
                startActivity(i);

            }
        });

    }


}




