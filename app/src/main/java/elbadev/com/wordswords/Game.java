package elbadev.com.wordswords;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
//import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by frome on 10/07/2017.
 */


public class Game extends Activity{
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private AutoCompleteTextView invitables;

    private String[] names = {
        ""
    };
    ListView lv;
    List<String> fruits_list = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    //HANDLER

    //Handler Toast Gnam
    private Handler handler_toast = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if (Looper.getMainLooper() == null)
            {
                Looper.prepare();
            }
            String messaggio = message.getData().getString("messaggio");
            LayoutInflater li = getLayoutInflater();
            View view = li.inflate(R.layout.toast_layout,null);
            TextView tv = (TextView) view.findViewById(R.id.custom_text);
            tv.setText(messaggio);
            Toast custom_toast = new Toast(Game.this);
            custom_toast.setView(view);
            custom_toast.makeText(Game.this,messaggio,Toast.LENGTH_LONG);
            custom_toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,0 ,0);
            custom_toast.show();

//            Toast.makeText(Game.this, message.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    //Handler Lista Utenti Management

    private Handler handler_users = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {

            final Bundle bobbo = message.getData();
            System.out.println("WORDSWORDS_LOG: Lista Utenti Management " +  bobbo.getString("lista"));
            String zimba = bobbo.getString("lista");
            lv.setAdapter(null);

            String[] fruits = new String[]{};
            fruits_list = new ArrayList<String>(Arrays.asList(fruits));
            arrayAdapter = new ArrayAdapter<String>
                    (Game.this, android.R.layout.simple_list_item_1, fruits_list);

            // Assign adapter to ListView
            lv.setAdapter(arrayAdapter);

                String[] spirit = zimba.split(",");

                for(int i = 0; i < spirit.length; i++) {
                    fruits_list.add(spirit[i]);
                    arrayAdapter.notifyDataSetChanged();
                    System.out.println("WORDSWORDS_LOG: Lista Utenti Management cinque " + spirit[i]);
                }
        }
    };

    //<<<<<<<<<   INIZIO LISTA EMETTITORI    >>>>>>>>>

    //Cerca Partita Casuale
    private Emitter.Listener on_cerca_partita = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: Ricerca avversario");
        }
    };


    //Numero minimo utenti per giocare
    private Emitter.Listener on_abbastanza = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: Abbastanza " + GlobalState.getAbbasta());
            GlobalState.setAbbasta(true);
            System.out.println("WORDSWORDS_LOG: Abbastanza " +  GlobalState.getAbbasta());
        }
    };

    //Esci dalla Partita
    private Emitter.Listener on_partita_distrutta = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: Fine Partita");
            finish();
        }
    };

    //Invita Amico
    private Emitter.Listener on_cerca_amico = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: on_cerca_amico");

        }
    };

    //Ricezione Notifica Avvenuto contatto con amico
    private Emitter.Listener on_amico_trovato = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: on_amico_trovato");
        }
    };
    //entra_giocatore
    private Emitter.Listener on_entra_giocatore = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String nome_giocatore_entrato = (String)args[0];
            System.out.println("WORDSWORDS_LOG: Giocatore entrato: " + nome_giocatore_entrato);
            Bundle bundle = new Bundle();
            bundle.putString("messaggio" , nome_giocatore_entrato + " é entrato nella stanza!");
            Message message = handler_toast.obtainMessage();
            message.setData(bundle);
            message.sendToTarget();
        }
    };


    //Server spedisce tutti in partita
    private Emitter.Listener on_trasferimento_partita = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject obj = (JSONObject) args[0];

            try
            {

                if(obj == null){
                    System.out.println("WORDSWORDS_LOG: ERRORE qualche utente ha finito le frasi");

                }else{
                    System.out.println("WORDSWORDS_LOG: trasferimento partita " + obj.getString("prima_parte"));
                    if(GlobalState.getTurno() == 0 ){
                        GlobalState.setTurno(1);
                    }else{
                        System.out.println("WORDSWORDS_LOG: trasferimento partita con turno != 0");
                        //GlobalState.setTurno(GlobalState.getTurno()+1);
                    }

                    Intent i = new Intent(Game.this, WordsGame.class);
                    i.putExtra("prima_parte", obj.getString("prima_parte"));
                    i.putExtra("seconda_parte", obj.getString("seconda_parte"));
                    i.putExtra("titolo", obj.getString("titolo"));
                    i.putExtra("autore", obj.getString("autore"));
                    i.putExtra("tipo", obj.getString("tipo"));
                    GlobalState.setTurno_gioco(i);
                    startActivity(i);
                }

            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }

        }
    };


    //Ricezione dal Server della lista utenti in partita da far vedere nella Listview
    private Emitter.Listener on_lista_utenti = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject obj = (JSONObject) args[0];

            try
            {

                System.out.println("WORDSWORDS_LOG: Ricezione dal Server della lista utenti in partita " + obj.getString("utenti"));
                Bundle bibbo = new Bundle();
                bibbo.putString("lista" , obj.getString("utenti"));
                Message message = handler_users.obtainMessage();
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
    public void onDestroy() {
        super.onDestroy();
        GlobalState.getmSocket().off("partita_distrutta", on_partita_distrutta);
    }


    @Override
    public void onBackPressed() {
        customToast(GlobalState.getRandomWisenessBackButton(),Toast.LENGTH_LONG);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFriends(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_game);
        final TypeWriter tw = (TypeWriter) findViewById(R.id.title_game);
        tw.setText("");
        tw.animateText("Stanza");
        GlobalState.setTurno(0);

        if(GlobalState.getLeader() == 0)
        {
            Button bstart1 = (Button) findViewById(R.id.bottone_start);
            bstart1.setVisibility(View.GONE);
        }


        // Get ListView object from xml
        lv = (ListView) findViewById(R.id.lobby);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id){
                // qui dentro stabilisco cosa fare dopo il click
                // chiedere se aggiungere agli amici
                final String scrittore = (String)adattatore.getItemAtPosition(pos);
                if(!scrittore.equals(GlobalState.getMia_email())){
                    AlertDialog alert = new AlertDialog.Builder(Game.this).create();
                    alert.setTitle("Invito di Gioco");
                    alert.setMessage("Puoi aggiungere " +  scrittore + " alla tua lista amici");
                    alert.setButton(Dialog.BUTTON_NEGATIVE,"Aggiungi",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("WORDSWORDS_LOG: Vuole aggiungere agli amici");
                            new InsertFriend(scrittore).execute(GlobalState.getDb());
                            //in qualche modo la lista amici deve essere aggiornata dopo questo
                            customToast(scrittore + " sará presente nella lista amici al prossimo riavvio",Toast.LENGTH_LONG);

                        }
                    });

                    alert.show();
                }else{
                    customToast("non puoi aggiungerti o invitarti!",Toast.LENGTH_LONG);
                }
            }
        });


//        GlobalState.getTypewriter_multi_sound().start();
        final Animation button_translate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation button_scale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        final AnimationSet button_as = new AnimationSet(true);
        button_as.addAnimation(button_translate);
        button_as.addAnimation(button_scale);
        String[] fruits = new String[]{};

        fruits_list = new ArrayList<String>(Arrays.asList(fruits));

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, fruits_list);

        //recupero gli amici dal db:
        final ArrayAdapter adapter_friends = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,names);
        final ListView lva;
        lva = (ListView) findViewById(R.id.lista_amici);
        lva.setAdapter(adapter_friends);



        // Assign adapter to ListView
        lv.setAdapter(arrayAdapter);
        // Add new Items to List
        fruits_list.add(GlobalState.getMia_email());
        arrayAdapter.notifyDataSetChanged();

        GlobalState.getmSocket().emit("lista_utenti", GlobalState.getId_stanza_attuale());

        //GESTIONE SOCKET


        GlobalState.getmSocket().on("cerca_partita", on_cerca_partita).on("partita_distrutta", on_partita_distrutta).on("utente_entrato", on_cerca_amico).on("invito_inviato", on_amico_trovato).on("lista_utenti", on_lista_utenti).on("trasferimento_partita", on_trasferimento_partita).on("abbastanza", on_abbastanza).on("entra_giocatore",on_entra_giocatore);
        Button btn_chiudi = (Button) findViewById(R.id.button_chiudi);
        btn_chiudi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                //  GlobalState.getButtonSound().start();

                finish();
                System.exit(0);

            }
        });

        Button btn_compra = (Button) findViewById(R.id.button_compra);
        btn_compra.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                //      GlobalState.getButtonSound().start();



            }
        });
        invitables = (AutoCompleteTextView) findViewById(R.id.invitables);
        names = GlobalState.getUtenti_online();
        final Context ctx = this;
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,names);
        final ListView lv;
        lv = (ListView) findViewById(R.id.lista_online_game);
        lv.setAdapter(adapter);

        invitables.setThreshold(1);
        invitables.setAdapter(adapter);

//        new GetAllFriends((ListView) findViewById(R.id.lista_amici),this).execute(GlobalState.getDb());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id){
                // qui dentro stabilisco cosa fare dopo il click
                // chiedere se invitare o aggiungere agli amici
                final String scrittore = (String)adattatore.getItemAtPosition(pos);
                if(!scrittore.equals(GlobalState.getMia_email())){
                    AlertDialog alert = new AlertDialog.Builder(Game.this).create();
                    alert.setTitle("Invito di Gioco");
                    alert.setMessage("Puoi invitare " + scrittore+ " in partita, oppure aggiungere " +  scrittore + " alla tua lista amici");
                    alert.setButton(Dialog.BUTTON_NEGATIVE,"Aggiungi",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("WORDSWORDS_LOG: Vuole aggiungere agli amici");
                            new InsertFriend(scrittore).execute(GlobalState.getDb());
                            //in qualche modo la lista amici deve essere aggiornata dopo questo
                            customToast(scrittore + " sará presente nella lista amici al prossimo riavvio",Toast.LENGTH_LONG);

                        }
                    });
                    alert.setButton(Dialog.BUTTON_POSITIVE,"Invita",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalState.getmSocket().emit("invita_in_partita", scrittore);
                            System.out.println("WORDSWORDS_LOG: Vuole invitare in partita  " );
                        }
                    });
                    alert.show();
                }else{
                    customToast("non puoi aggiungerti o invitarti!",Toast.LENGTH_LONG);
                }
            }
        });
        ListView flv = findViewById(R.id.lista_amici);

        flv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id){
                // qui dentro stabilisco cosa fare dopo il click
                // chiedere se invitare o aggiungere agli amici
                final String scrittore = (String)adattatore.getItemAtPosition(pos);
                if(!scrittore.equals(GlobalState.getMia_email())){
                    AlertDialog alert = new AlertDialog.Builder(Game.this).create();
                    alert.setTitle("Invito di Gioco");
                    alert.setMessage("Vuoi invitare " + scrittore+ " in partita?");
                    alert.setButton(Dialog.BUTTON_POSITIVE,"Invita",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalState.getmSocket().emit("invita_in_partita", scrittore);
                            System.out.println("WORDSWORDS_LOG: Vuole invitare in partita  " );
                        }
                    });
                    alert.show();
                }else{
                    customToast("non puoi aggiungerti o invitarti!",Toast.LENGTH_LONG);
                }
            }
        });


        //Gestione Pulsante Cerca Amico
        Button button = (Button) findViewById(R.id.button_friend);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GlobalState.getButtonSound().start();

                final TextView tv = (TextView) findViewById(R.id.invitables);
                tv.setVisibility(View.VISIBLE);

                Button button2 = (Button) findViewById(R.id.button_trova);
                button2.setVisibility(View.VISIBLE);

                Button button3 = (Button) findViewById(R.id.button_friend);
                button3.setVisibility(View.GONE);

                button2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        v.startAnimation(button_as);
                        GlobalState.getButtonSound().start();
                        if(tv.getText().toString().matches("")){
                            customToast("Riempi il campo Username Amico con il nome del tuo amico.",Toast.LENGTH_LONG);
//                            Toast.makeText(Game.this,"Riempi il campo Username Amico con il nome del tuo amico.", Toast.LENGTH_SHORT).show();
                            System.out.println("WORDSWORDS_LOG: chiamato trova amico senza nome amico.");
                        }else{
                            GlobalState.getmSocket().emit("invita_in_partita", tv.getText());
                            System.out.println("WORDSWORDS_LOG: cerco amico: " + tv.getText());
                        }
                    }
                });
            }
        });

        //Gestione Ricerca Avversario Casuale

        Button fate = (Button) findViewById(R.id.button_casual);

        fate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        ImageView btn_toc = (ImageView) findViewById(R.id.tocca_game);
        btn_toc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();

                DrawerLayout dl = (DrawerLayout)findViewById(R.id.drawer_layout_game);
                dl.openDrawer(Gravity.LEFT);

            }
        });

        ImageView btn_toc_right = (ImageView) findViewById(R.id.tocca_game_2);
        btn_toc_right.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("WORDSWORDS_LOG: apro dx: " );
                v.startAnimation(button_as);
                //GlobalState.getButtonSound().start();

                DrawerLayout dl = (DrawerLayout)findViewById(R.id.drawer_layout_game);
                dl.openDrawer(Gravity.RIGHT);

            }
        });
        //Inizia Partita

        Button bstart = (Button) findViewById(R.id.bottone_start);

        bstart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if(GlobalState.getAbbasta() == false) {
//                    customToast("Per iniziare dovete essere almeno in quattro, elefanti che si dondolavano sopra il filo di una ragnatela...",Toast.LENGTH_LONG);
//                }
//                else
//                {
//
//                    v.startAnimation(button_as);
//                    GlobalState.getButtonSound().start();
//                    GlobalState.getmSocket().emit("inizia_partita", GlobalState.getId_stanza_attuale());
//                    GlobalState.setAbbasta(false);
//                }
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                GlobalState.getmSocket().emit("inizia_partita", GlobalState.getId_stanza_attuale());
                GlobalState.setAbbasta(false);

            }
        });

        //Gestione Pulsante Esci Partita

        Button uscita = (Button) findViewById(R.id.button_exit);

        uscita.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                GlobalState.getmSocket().emit("distruggi_partita", GlobalState.getId_stanza_attuale());

            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_game);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                GlobalState.getTypewriter_drin().start();

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                GlobalState.getTypewriter_paper_3().start();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    public void customToast(String text, int duration){
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.toast_layout,null);
        TextView tv = (TextView) view.findViewById(R.id.custom_text);
        tv.setText(text);
        Toast custom_toast = new Toast(this);
        custom_toast.setView(view);
        custom_toast.makeText(Game.this,text,duration);
        custom_toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,0 ,0);
        custom_toast.show();
    }
    private void refreshFriends(Context context){
        new GetAllFriends((ListView) findViewById(R.id.lista_amici),context).execute(GlobalState.getDb());
    }
}


