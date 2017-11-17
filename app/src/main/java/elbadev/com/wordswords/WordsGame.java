package elbadev.com.wordswords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by frome on 10/07/2017.
 */


public class WordsGame extends Activity {

    final AnimationSet button_as = new AnimationSet(true);


    @Override
    public void onBackPressed(){
        customToast(GlobalState.getRandomWisenessBackButton(),Toast.LENGTH_LONG);
        //super.onBackPressed();
    }


    ListView lv;
    List<String> fruits_list = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    Integer position_true = -99;


    //HANDLER

    //Handler Lista Utenti Management

    private Handler handler_fine_attesa = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {

            ProgressBar ld = (ProgressBar) findViewById(R.id.load);
            ld.setVisibility(View.GONE);

            TextView wt = (TextView) findViewById(R.id.wait_text);
            wt.setVisibility(View.GONE);

            TextView fin = (TextView) findViewById(R.id.text_finali);
            fin.setVisibility(View.VISIBLE);

            ListView ella = (ListView) findViewById(R.id.all_frasi);
            ella.setVisibility(View.VISIBLE);

            Button bv = (Button) findViewById(R.id.button_voto);
            bv.setVisibility(View.VISIBLE);



            final Bundle bobbo = message.getData();

            String users = bobbo.getString("utente");
            String finale = bobbo.getString("seconda");

            System.out.println("WORDSWORDS_LOG: ::::::::: " +  bobbo.getString("utente"));
            System.out.println("WORDSWORDS_LOG: ::::::::: " +  bobbo.getString("seconda"));

            lv.setAdapter(null);

            String[] fruits = new String[]{};
            System.out.println("WORDSWORDS_LOG: ::::::::: due");
            fruits_list = new ArrayList<String>(Arrays.asList(fruits));
            arrayAdapter = new ArrayAdapter<String>
                    (WordsGame.this, android.R.layout.simple_list_item_1, fruits_list);
            System.out.println("WORDSWORDS_LOG: ::::::::: tre");

            // Assign adapter to ListView
            lv.setAdapter(arrayAdapter);

            System.out.println("WORDSWORDS_LOG: ::::::::: quattro");
            String[] spirit = finale.split("___");
            String[] spirit_user = users.split("___");



            for(int i = 0; i < spirit.length; i++) {

                System.out.println("WORDSWORDS_LOG: ::::::::: " + spirit_user[i]);
                System.out.println("WORDSWORDS_LOG: ::::::::: " + spirit[i]);
                if(spirit_user[i] != null){
                    System.out.println("WORDSWORDS_LOG: -.-.-.-.-.-. " + spirit_user[i]);
                    if(spirit_user[i].equals("giusta"))
                    {
                        position_true = i;
                        System.out.println("WORDSWORDS_LOG: dentro giusta");
                    }
                    fruits_list.add(" ..." + spirit[i]);
                    if(spirit_user[i].equals(GlobalState.getMia_email()))
                    {
                        GlobalState.setPosizione_mia_frase(i);
                    }
                    arrayAdapter.notifyDataSetChanged();
                    System.out.println("WORDSWORDS_LOG: finale :::: " + spirit[i]);
                }else{
                    System.out.println("WORDSWORDS_LOG: spirit[i] è null");
                }

            }


            bv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.startAnimation(button_as);
                    GlobalState.getButtonSound().start();



                    System.out.println("WORDSWORDS_LOG: Posizione inviata " + GlobalState.getPosizione_lista());
                    if(GlobalState.getPosizione_lista() == position_true)
                    {
                        System.out.println("WORDSWORDS_LOG: dentro uguale");
                        GlobalState.getmSocket().emit("voto", -1);
                        Button bv2 = (Button) findViewById(R.id.button_voto);
                        bv2.setVisibility(View.GONE);

                        ProgressBar ld2 = (ProgressBar) findViewById(R.id.load_finale);
                        ld2.setVisibility(View.VISIBLE);
                    }
                    else if(GlobalState.getPosizione_lista() == -2)
                    {
                        customToast("devi scegliere un finale prima! ",Toast.LENGTH_LONG);
                    }
                    else
                    {
                        GlobalState.getmSocket().emit("voto", GlobalState.getPosizione_lista());
                        Button bv2 = (Button) findViewById(R.id.button_voto);
                        bv2.setVisibility(View.GONE);

                        ProgressBar ld2 = (ProgressBar) findViewById(R.id.load_finale);
                        ld2.setVisibility(View.VISIBLE);
                    }

                }
            });

        }
    };


    //Handler Gestione Nuovo Turno

    private Handler handler_nuovo_turno = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {

           final Bundle bobbo = message.getData();

            GlobalState.setTurno(GlobalState.getTurno()+1);
            System.out.println("WORDSWORDS_LOG: trasferimento partita " + bobbo.getString("prima_parte"));
            Intent i = GlobalState.getTurno_gioco();
            i.putExtra("prima_parte", bobbo.getString("prima_parte"));
            i.putExtra("seconda_parte", bobbo.getString("seconda_parte"));
            i.putExtra("titolo", bobbo.getString("titolo"));
            i.putExtra("autore", bobbo.getString("autore"));
            i.putExtra("tipo", bobbo.getString("tipo"));
            GlobalState.setTurno_gioco(i);
            System.out.println("WORDSWORDS_LOG: parametri: titolo " + i.getStringExtra("titolo") + "autore " + i.getStringExtra("autore")+ "tipo " + i.getStringExtra("tipo") );
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(i, 0);

        }
    };

    //FINE HANDLER

//   <<<<<<<<<   INIZIO LISTA EMETTITORI    >>>>>>>>>

    //Il server avverte che tutti hanno scritto la frase
    private Emitter.Listener on_tutti_pronti = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: Tutti pronti");


            JSONObject obj = (JSONObject) args[0];

            try
            {
                System.out.println("WORDSWORDS_LOG: -------->>>>>>> ");
                Bundle bibbo = new Bundle();
                bibbo.putString("utente" , obj.getString("utenti"));
                bibbo.putString("seconda" , obj.getString("seconde_parti"));
                Message message = handler_fine_attesa.obtainMessage();
                message.setData(bibbo);
                message.sendToTarget();
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }

        }
    };


    private Emitter.Listener on_voto_singolo = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: Ricezione del voto inviato");


        }
    };

    private Emitter.Listener on_classifica = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: Tutti i voti inviati e arriva la classifica");

            JSONObject obj = (JSONObject) args[0];
            GlobalState.setClassifica(obj);
            try {
                Iterator<String> temp = obj.keys();
                while (temp.hasNext()) {
                    String key = temp.next();
                    Object profilo = obj.get(key);
                    JSONObject pro = (JSONObject) profilo;
                    System.out.println("WORDSWORDS_LOG: <<<>>> Yes " + pro.getString("nome"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(GlobalState.getTurno() <= 1)
            {
                     Intent i = new Intent(WordsGame.this, ClassificaWordsGame.class);
                     GlobalState.setTurno_classifica(i);
                     startActivity(i);
            }
            else
            {
                Intent j = GlobalState.getTurno_classifica();
                if(j == null){
                    Intent i = new Intent(WordsGame.this, ClassificaWordsGame.class);
                    GlobalState.setTurno_classifica(i);
                    startActivity(i);
                }else{
                    j.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityIfNeeded(j, 0);
                }
            }

        }
    };

    //Avanzamento Turno
    private Emitter.Listener on_pronti_per_prossimo_turno = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            System.out.println("WORDSWORDS_LOG: si va avanti");

            JSONObject obj = (JSONObject) args[0];

            try
            {
                Bundle bibbo = new Bundle();
                bibbo.putString("titolo" , obj.getString("titolo"));
                System.out.println("WORDSWORDS_LOG: on_pronti_per_prossimo_turno " + obj.getString("prima_parte"));
                bibbo.putString("prima_parte" , obj.getString("prima_parte"));
                bibbo.putString("autore" , obj.getString("autore"));
                bibbo.putString("tipo" , obj.getString("tipo"));
                Message message = handler_nuovo_turno.obtainMessage();
                message.setData(bibbo);
                message.sendToTarget();

            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }

        }
    };

    //<<<<<<<<<   FINE LISTA EMETTITORI    >>>>>>>>>



    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_wordsgame);

        final Animation button_translate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation button_scale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        //GlobalState.getTypewriter_multi_sound().start();

        button_as.addAnimation(button_translate);
        button_as.addAnimation(button_scale);


        GlobalState.setPosizione_lista(-2);
        GlobalState.setPosizione_mia_frase(-2);
        TextView testo_turno = (TextView) findViewById(R.id.titolo_partita);
        testo_turno.setText("Turno " + GlobalState.getTurno());

        // Get ListView object from xml
        lv = (ListView) findViewById(R.id.all_frasi);

        String[] fruits = new String[]{};

        fruits_list = new ArrayList<String>(Arrays.asList(fruits));
        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, fruits_list);


        // Assign adapter to ListView
        lv.setAdapter(arrayAdapter);



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                System.out.println("WORDSWORDS_LOG: **************** " +  GlobalState.getPosizione_mia_frase());

                if(position == GlobalState.getPosizione_mia_frase())
                {
                    customToast("Non puoi Autovotarti Laidone!!! ",Toast.LENGTH_LONG);

                    GlobalState.setPosizione_lista(-2);
                }
                else
                {
//                    Toast.makeText(WordsGame.this, "Ho pigiato " + position, Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < lv.getChildCount(); i++) {
                        View listItem = lv.getChildAt(i);
                        listItem.setBackgroundColor(Color.WHITE);
                    }

                    view.setBackgroundColor(Color.argb(125,75,236,90));
                    GlobalState.setPosizione_lista(position);
                }

            }
        });



        //GESTIONE SOCKET

        GlobalState.getmSocket().on("tutti_pronti", on_tutti_pronti).on("voto_singolo", on_voto_singolo).on("classifica", on_classifica).on("pronti_per_prossimo_turno", on_pronti_per_prossimo_turno);




        Intent g = getIntent();

        TextView tit = (TextView) findViewById(R.id.titolo);
        TextView aut = (TextView) findViewById(R.id.autore);

        String tipo = g.getStringExtra("tipo");


        final TypeWriter tvg = (TypeWriter) findViewById(R.id.sentenza);
        tvg.setText("");
//        tvg.animateText(g.getStringExtra("prima_parte"));

        Random rand = new Random();
        int n = rand.nextInt(3); // Gives n such that 0 <= n < 3

        switch (tipo) {
            case "libro":
                tit.setText("Titolo del libro : " + g.getStringExtra("titolo"));
                aut.setText("Autore del libro : " + g.getStringExtra("autore"));
                break;
            case "film":
                tit.setText("Titolo del film : " + g.getStringExtra("titolo"));
                aut.setText("Regista del film : " + g.getStringExtra("autore"));
                break;
            case "canzone":
                tit.setText("Titolo della canzone : " + g.getStringExtra("titolo"));
                aut.setText("Autore della canzone : " + g.getStringExtra("autore"));
                break;
            case "pubblicità":
                tit.setText("Marca : " + g.getStringExtra("titolo"));
                aut.setText("Produttore : " + g.getStringExtra("autore"));
                break;
        }
        tvg.lowBound = 80;
        tvg.upBound = 150;
        //metto l'inizio frase in global state
        GlobalState.setFrase_giusta_inizio(g.getStringExtra("prima_parte"));
        GlobalState.setFrase_giusta_fine(g.getStringExtra("seconda_parte"));

        tvg.animateText(g.getStringExtra("prima_parte"));

        final Button scelta = (Button) findViewById(R.id.invia_scelta);
        final TextView wt = (TextView) findViewById(R.id.wait_text);
        final EditText edo = (EditText) findViewById(R.id.testo);
        scelta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                GlobalState.getmSocket().emit("invio_scelta", GlobalState.getId_stanza_attuale() + "," + edo.getText());
                ProgressBar ld = (ProgressBar) findViewById(R.id.load);
                ld.setVisibility(View.VISIBLE);
                scelta.setVisibility(View.GONE);
                wt.setVisibility(View.VISIBLE);
                edo.setVisibility(View.GONE);
                //nascondo la tastiera NUOVO_DA_TESTARE
                System.out.println("WORDSWORDS_LOG: nascondo tastiera ");

                View view = WordsGame.this.getCurrentFocus();
                if (view != null) {
                    hideKeyboard(view);
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_wordsgame);
//      multisound start
        final Animation button_translate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation button_scale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        final AnimationSet button_as = new AnimationSet(true);
        button_as.addAnimation(button_translate);
        button_as.addAnimation(button_scale);
        GlobalState.setPosizione_lista(-2);
        GlobalState.setPosizione_mia_frase(-2);
        TextView testo_turno = (TextView) findViewById(R.id.titolo_partita);
        testo_turno.setText("Turno " + GlobalState.getTurno());

        // Get ListView object from xml
        lv = (ListView) findViewById(R.id.all_frasi);

        String[] fruits = new String[]{};

        fruits_list = new ArrayList<String>(Arrays.asList(fruits));
        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, fruits_list);


        // Assign adapter to ListView
        lv.setAdapter(arrayAdapter);



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                System.out.println("WORDSWORDS_LOG: posizione frase " +  GlobalState.getPosizione_mia_frase());

                if(position == GlobalState.getPosizione_mia_frase())
                {
                    customToast("Non puoi Autovotarti Laidone!!! ",Toast.LENGTH_LONG);
                    GlobalState.setPosizione_lista(-2);
                }
                else
                {
                    //Toast.makeText(WordsGame.this, "Ho pigiato " + position, Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < lv.getChildCount(); i++) {
                        View listItem = lv.getChildAt(i);
                        listItem.setBackgroundColor(Color.argb(0,107,81,19));
                    }

                    view.setBackgroundColor(Color.argb(125,107,81,19));
                    GlobalState.setPosizione_lista(position);
                }

            }
        });
        Intent g = getIntent();
        Intent intento_nuovo = GlobalState.getTurno_gioco();
        if(GlobalState.getTurno_gioco() != null){
            g = intento_nuovo;
        }


        TextView tit = (TextView) findViewById(R.id.titolo);
        TextView aut = (TextView) findViewById(R.id.autore);
        final TypeWriter tvg = (TypeWriter) findViewById(R.id.sentenza);
        tvg.setText("");
        String tipo = g.getStringExtra("tipo");



        Random rand = new Random();
        int n = rand.nextInt(3); // Gives n such that 0 <= n < 3
        System.out.println("WORDSWORDS_LOG: onresumino" + g.getStringExtra("prima_parte") + tipo);
        switch (tipo) {
            case "libro":
                tit.setText("Titolo del libro : " + g.getStringExtra("titolo"));
                aut.setText("Autore del libro : " + g.getStringExtra("autore"));
                break;
            case "film":
                tit.setText("Titolo del film : " + g.getStringExtra("titolo"));
                aut.setText("Regista del film : " + g.getStringExtra("autore"));
                break;
            case "canzone":
                tit.setText("Titolo della canzone : " + g.getStringExtra("titolo"));
                aut.setText("Autore della canzone : " + g.getStringExtra("autore"));
                break;
            case "pubblicità":
                tit.setText("Marca : " + g.getStringExtra("titolo"));
                aut.setText("Produttore : " + g.getStringExtra("autore"));
                break;
        }
        tvg.lowBound = 80;
        tvg.upBound = 150;
        tvg.animateText(g.getStringExtra("prima_parte"));


        final Button scelta = (Button) findViewById(R.id.invia_scelta);
        final TextView wt = (TextView) findViewById(R.id.wait_text);
        final EditText edo = (EditText) findViewById(R.id.testo);



        scelta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GlobalState.getmSocket().emit("invio_scelta", edo.getText());
                ProgressBar ld = (ProgressBar) findViewById(R.id.load);
                ld.setVisibility(View.VISIBLE);
                scelta.setVisibility(View.GONE);
                wt.setVisibility(View.VISIBLE);
                edo.setVisibility(View.GONE);
                System.out.println("WORDSWORDS_LOG: nascondo tastiera ");
                View view = WordsGame.this.getCurrentFocus();
                if (view != null) {
                    hideKeyboard(view);
                }

            }
        });


    }
    public void customToast(String text, int duration){
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.toast_layout,null);

        TextView tv = (TextView) view.findViewById(R.id.custom_text);
        tv.setText(text);
        Toast custom_toast = new Toast(this);
        custom_toast.setView(view);
        custom_toast.makeText(WordsGame.this,text,duration);
        custom_toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,0 ,0);
        custom_toast.show();
    }
    private void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
