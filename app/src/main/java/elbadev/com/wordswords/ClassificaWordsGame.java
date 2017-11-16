package elbadev.com.wordswords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
        customToast(GlobalState.getRandomWisenessBackButton(),Toast.LENGTH_LONG);

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
            if(GlobalState.getClassifica() != null){
                Iterator<String> temp = GlobalState.getClassifica().keys();
                System.out.println("WORDSWORDS_LOG: count: ");

                while (temp.hasNext()) {
                    System.out.println("WORDSWORDS_LOG: start cycle");
                    String key = temp.next();
                    Object profilo = GlobalState.getClassifica().get(key);
                    JSONObject pro = (JSONObject) profilo;

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;

                    int larg = (60 * width) / 100;

                    if (GlobalState.getMia_email().equals(pro.getString("nome"))) {
                        GlobalState.setMiei_punti(pro.getInt("points"));
                    }


                    TableLayout tabella = (TableLayout) findViewById(R.id.tab);

                    TableRow riga = new TableRow(this);
    //                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //                riga = (TableRow)inflater.inflate(R.layout.custom_list_row, null);
                    TableRow.LayoutParams aParams = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                    riga.setLayoutParams(aParams);
                    riga.setGravity(Gravity.CENTER);

                    TextView nome = new TextView(this);
                    nome.setBackgroundColor(Color.parseColor("#ebca79"));
                    nome.setText(pro.getString("nome"));
                    nome.setGravity(Gravity.CENTER);
                    nome.setPadding(2, 10, 2, 10);
                    riga.addView(nome);

                    TextView punti = new TextView(this);
                    punti.setBackgroundColor(Color.parseColor("#ebca79"));
                    punti.setText(pro.getString("points"));
                    punti.setGravity(Gravity.CENTER);
                    punti.setPadding(2, 10, 2, 10);
                    riga.addView(punti);

                    TextView voto = new TextView(this);
                    voto.setWidth(larg);
                    voto.setBackgroundColor(Color.parseColor("#ebca79"));
                    voto.setLines(1);
                    voto.setPadding(2, 10, 2, 10);
                    voto.setHorizontallyScrolling(true);
                    voto.setMovementMethod(new ScrollingMovementMethod());
                    voto.setText(pro.getString("voto").replace("[", "").replace("]", "").replace("\"", ""));
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
            TableLayout tabella = (TableLayout) findViewById(R.id.tab);
            //aggiungo frase giusta
            TableRow riga_giusta = new TableRow(this);
            TableRow.LayoutParams aParams_g = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            aParams_g.span = 2;
            riga_giusta.setLayoutParams(aParams_g);
            riga_giusta.setPadding(5,5,5,5);
            riga_giusta.setBackgroundColor(Color.parseColor("#000000"));
            riga_giusta.setGravity(Gravity.LEFT);
            TextView frase_giusta = new TextView(this);
            frase_giusta.setText(GlobalState.getFrase_giusta_inizio().replace("...","") + " " + GlobalState.getFrase_giusta_fine().replace("...",""));
            frase_giusta.setBackgroundColor(Color.parseColor("#ebca79"));
            frase_giusta.setGravity(Gravity.CENTER);
            frase_giusta.setTextColor(Color.BLACK);
            frase_giusta.setLayoutParams(aParams_g);
            frase_giusta.setPadding(2,10,2,10);
            riga_giusta.addView(frase_giusta);

            TableRow riga_vuota = new TableRow(this);
            riga_vuota.setLayoutParams(aParams_g);
            riga_vuota.setGravity(Gravity.LEFT);
            riga_vuota.setPadding(5,5,5,5);
            TextView vuota = new TextView(this);
            vuota.setText(" ");
            vuota.setGravity(Gravity.CENTER);
            vuota.setPadding(2,10,2,10);
            riga_vuota.addView(vuota);
            tabella.addView(riga_giusta, new TableLayout.LayoutParams());
            tabella.addView(riga_vuota, new TableLayout.LayoutParams());
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




                TableRow riga_nome = new TableRow(this);
                TableRow.LayoutParams aParams = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                riga_nome.setLayoutParams(aParams);
                riga_nome.setPadding(5,5,5,0);
                riga_nome.setBackgroundColor(Color.parseColor("#000000"));
                riga_nome.setGravity(Gravity.LEFT);

                TextView nome_label = new TextView(this);
                nome_label.setBackgroundColor(Color.parseColor("#ebca79"));
                nome_label.setText("Scrittore:");
                nome_label.setLayoutParams(aParams);
                nome_label.setGravity(Gravity.LEFT);
                nome_label.setPadding(2,10,2,10);
                riga_nome.addView(nome_label);
                TextView nome = new TextView(this);
                nome.setBackgroundColor(Color.parseColor("#ebca79"));
                nome.setText(pro.getString("nome"));
                nome.setLayoutParams(aParams);
                nome.setGravity(Gravity.RIGHT);
                nome.setPadding(2,10,2,10);
                riga_nome.addView(nome);

                TableRow riga_Punti = new TableRow(this);
                riga_Punti.setLayoutParams(aParams);
                riga_Punti.setPadding(5,5,5,0);
                riga_Punti.setBackgroundColor(Color.parseColor("#000000"));
                riga_Punti.setGravity(Gravity.LEFT);

                TextView punti_label = new TextView(this);
                punti_label.setBackgroundColor(Color.parseColor("#ebca79"));
                punti_label.setText("Punti:");
                punti_label.setLayoutParams(aParams);
                punti_label.setGravity(Gravity.LEFT);
                punti_label.setPadding(2,10,2,10);
                riga_Punti.addView(punti_label);
                TextView punti = new TextView(this);
                punti.setBackgroundColor(Color.parseColor("#ebca79"));
                punti.setText(pro.getString("points"));
                punti.setLayoutParams(aParams);
                punti.setGravity(Gravity.RIGHT);
                punti.setPadding(2,10,2,10);
                riga_Punti.addView(punti);

                TableRow riga_voto = new TableRow(this);
                riga_voto.setLayoutParams(aParams);
                riga_voto.setPadding(5,5,5,0);
                riga_voto.setBackgroundColor(Color.parseColor("#000000"));
                riga_voto.setGravity(Gravity.LEFT);

                TextView voto_label = new TextView(this);
                voto_label.setBackgroundColor(Color.parseColor("#ebca79"));
                voto_label.setText("Votato da:");
                voto_label.setLayoutParams(aParams);
                voto_label.setGravity(Gravity.LEFT);
                voto_label.setPadding(2,10,2,10);
                riga_voto.addView(voto_label);
                TextView voto = new TextView(this);
                voto.setWidth(larg);
                voto.setBackgroundColor(Color.parseColor("#ebca79"));
//                voto.setLines(1);
                voto.setPadding(2,10,2,10);
                voto.setHorizontallyScrolling(true);
                voto.setLayoutParams(aParams);
                voto.setMovementMethod(new ScrollingMovementMethod());
                voto.setText(pro.getString("voto").replace("[","").replace("]","").replace("\"",""));
                System.out.println("WORDSWORDS_LOG: .......... " + pro.getString("voto"));
                riga_voto.addView(voto);

                TableRow.LayoutParams aParams2 = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                aParams2.span = 2;
                TableRow riga_frase = new TableRow(this);
                riga_frase.setLayoutParams(aParams2);
                riga_frase.setGravity(Gravity.LEFT);
                riga_frase.setPadding(5,5,5,5);
                riga_frase.setBackgroundColor(Color.parseColor("#000000"));

                TextView frase = new TextView(this);
                frase.setText(pro.getString("frase"));
                frase.setBackgroundColor(Color.parseColor("#ebca79"));
                frase.setGravity(Gravity.CENTER);
                frase.setTextColor(Color.BLACK);
                frase.setLayoutParams(aParams2);
                frase.setPadding(2,10,2,10);
                riga_frase.addView(frase);

                TableRow riga_vuota_s = new TableRow(this);
                riga_vuota_s.setLayoutParams(aParams_g);
                riga_vuota_s.setGravity(Gravity.LEFT);
                riga_vuota_s.setPadding(5,5,5,5);
                TextView vuota_s = new TextView(this);
                vuota_s.setText(" ");
                vuota_s.setGravity(Gravity.CENTER);
                vuota_s.setPadding(2,10,2,10);
                riga_vuota_s.addView(vuota_s);

                tabella.addView(riga_nome, new TableLayout.LayoutParams());
                tabella.addView(riga_Punti, new TableLayout.LayoutParams());
                tabella.addView(riga_voto, new TableLayout.LayoutParams());
                tabella.addView(riga_frase, new TableLayout.LayoutParams());
                tabella.addView(riga_vuota_s, new TableLayout.LayoutParams());



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
    public void customToast(String text, int duration){
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.toast_layout,null);

        TextView tv = (TextView) view.findViewById(R.id.custom_text);
        tv.setText(text);
        Toast custom_toast = new Toast(this);
        custom_toast.setView(view);
        custom_toast.makeText(ClassificaWordsGame.this,text,duration);
        custom_toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,0 ,0);
        custom_toast.show();
    }



}




