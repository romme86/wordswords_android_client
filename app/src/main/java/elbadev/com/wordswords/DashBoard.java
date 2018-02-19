package elbadev.com.wordswords;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL;

public class DashBoard extends Activity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String fsocket;

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalState.getmSocket().disconnect();
    }

    @Override
    public void onBackPressed(){
        customToast(GlobalState.getRandomWisenessBackButton(),Toast.LENGTH_LONG);

        //super.onBackPressed();
    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("WORDSWORDS_LOG: dashboard resumed");
        Button button1 = (Button) findViewById(R.id.button_start);
        button1.setEnabled(true);
        //se in global state logout é a true eseguo il logout
        if(GlobalState.isLogout()){
            LinearLayout wait_layout = findViewById(R.id.wait_layout);
            wait_layout.setVisibility(View.VISIBLE);
            Runnable slogga = new Runnable() {
                @Override
                public void run() {
                    //do work
                    GlobalState.setLogout(false);
                    System.out.println("WORDSWORDS_LOG: eseguo logout");
                    onDestroy();
                    RequestQueue mRequestQueue;
                    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
                    Network network = new BasicNetwork(new HurlStack());
                    mRequestQueue = new RequestQueue(cache, network);
                    mRequestQueue.start();
                    JSONObject postParam = new JSONObject();
                    try {
                        postParam.put("email", GlobalState.getMia_email());
                        postParam.put("password", GlobalState.getMia_password());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject pino = postParam;
                    System.out.println("WORDSWORDS_LOG: JSON OUTPUT di logout->" + pino.toString());
                    GlobalState.getmSocket().emit("distruggi_partita", "Cessa sta Ressa");
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            GlobalState.getAddress() + "logout", pino,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getInt("logout_ok") == 1) {
                                            finish();
                                        }
                                        else {

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("WORDSWORDS_LOG: A Puttane " + error.getMessage());
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }
                    };
                    mRequestQueue.add(jsonObjReq);
                }
            };
            (new Handler()).postDelayed(slogga, 3000);
        }else{
            LinearLayout wait_layout = findViewById(R.id.wait_layout);
            wait_layout.setVisibility(View.GONE);
        }
    }

    //HANDLER

    //Handler Popup
    private Handler handler_alert = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {

            final Bundle bobbo = message.getData();

            AlertDialog alert = new AlertDialog.Builder(DashBoard.this).create();
            alert.setTitle("Invito di Gioco");
            alert.setMessage(bobbo.getString("nome") + " ti ha invitato a giocare! Vuoi partecipare?");
            alert.setButton(Dialog.BUTTON_NEGATIVE,"No",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("WORDSWORDS_LOG: +++++++++++");
                    GlobalState.getmSocket().emit("invito_respinto", bobbo.getString("room"));
                }
            });
            alert.setButton(Dialog.BUTTON_POSITIVE,"Si",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("WORDSWORDS_LOG: ?????????????????????????????  " + bobbo.getString("room") );
                    GlobalState.getmSocket().emit("invito_accettato", bobbo.getString("room"));
                    GlobalState.setId_stanza_attuale(bobbo.getString("room"));
                    GlobalState.setLeader(0);
                }
            });
            alert.show();
        }
    };

    //Handler Toast Gnam
    private Handler handler_toast = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            customToast( message.obj.toString(),Toast.LENGTH_LONG);
        }
    };




    private Handler handler_online = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {


            final Bundle bobbo = message.getData();

            System.out.println("WORDSWORDS_LOG: Lista Utenti online " +  bobbo.getString("lista"));
            String zimba = bobbo.getString("lista");
            lv.setAdapter(null);

            String[] fruits = new String[]{};
            System.out.println("WORDSWORDS_LOG: Lista Utenti online due ");
            lista_gamers = new ArrayList<String>(Arrays.asList(fruits));
            arrayAdapter = new ArrayAdapter<String>
                    (DashBoard.this, android.R.layout.simple_list_item_1, lista_gamers);
            System.out.println("WORDSWORDS_LOG: Lista Utenti online tre ");

            // Assign adapter to ListView
            lv.setAdapter(arrayAdapter);

            System.out.println("WORDSWORDS_LOG: Lista Utenti online quattro ");
            String[] spirit = zimba.split("___");

            GlobalState.setUtenti_online(spirit);

            for(int i = 0; i < spirit.length; i++) {
                lista_gamers.add(spirit[i]);
                arrayAdapter.notifyDataSetChanged();
                System.out.println("WORDSWORDS_LOG: Lista Utenti online cinque " + spirit[i]);
            }
        }
    };

    // FINE HANDLER

    //<<<<<<<<<   INIZIO LISTA EMETTITORI    >>>>>>>>>
    //Vai in partita
    private Emitter.Listener on_creata_partita = new Emitter.Listener()
    {
        @Override
        public void call(Object... args) {

            System.out.println("WORDSWORDS_LOG: Entro in Partita");
            JSONObject obj = (JSONObject) args[0];
            try
            {
                GlobalState.setId_stanza_attuale(obj.getString("room_id"));
                GlobalState.setLeader(1);
                Intent i = new Intent(DashBoard.this, Game.class);
                startActivity(i);
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }

        }
    };



    //Arriva invito di gioco
    private Emitter.Listener on_notifica_invito = new Emitter.Listener()
    {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: Invito Ricevuto");
            JSONObject obj = (JSONObject)args[0];

            try
            {
                Bundle bibbo = new Bundle();
                bibbo.putString("nome", obj.getString("nome"));
                bibbo.putString("room", obj.getString("room_id"));

                Message message = handler_alert.obtainMessage();
                message.setData(bibbo);
                message.sendToTarget();
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }


        }
    };

    //Invito Respinto
    private Emitter.Listener on_non_ha_accettato = new Emitter.Listener()
    {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: Sei stato rifiutato!!!");
            JSONObject obj_rifiuto = (JSONObject)args[0];

            try
            {
                String msg = obj_rifiuto.getString("nome") +  "ha rifiutato la richiesta di gioco...";
                Message message = handler_toast.obtainMessage();
                message.obj = msg;
                message.sendToTarget();
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
        }
    };

    //Ricevo avvenuto invito
    private Emitter.Listener on_invito_inviato = new Emitter.Listener()
    {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: Notifica di invito inviato avvenuta");

            JSONObject obj = (JSONObject)args[0];

            try
            {
                String msg = "Invito inviato a " + obj.getString("nome");
                Message message = handler_toast.obtainMessage();
                message.obj = msg;
                message.sendToTarget();
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }


        }
    };

    //Nella disconnessione
    private Emitter.Listener on_disconnessione = new Emitter.Listener()
    {
        @Override
        public void call(Object... args) {
            System.out.println("WORDSWORDS_LOG: Socket Disconnesso");
            GlobalState.getmSocket().off();
        }
    };

    //Ricezione dal Server della lista utenti in partita da far vedere nella Listview
    private Emitter.Listener on_lista_utenti = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject obj = (JSONObject) args[0];
            System.out.println("WORDSWORDS_LOG: Ricezione dal Server della lista utenti in partita" + obj);

        }
    };

    private Emitter.Listener on_entra_gioco = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject obj = (JSONObject)args[0];

            try
            {
                String da_cerca_partita = obj.getString("cerca");
                System.out.println("WORDSWORDS_LOG: da_cerca_partita= " + da_cerca_partita);
                if(da_cerca_partita != null){
                    String room_id = obj.getString("room_id");
                    GlobalState.setId_stanza_attuale(room_id);
                    GlobalState.setLeader(0);
                }
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }


            System.out.println("WORDSWORDS_LOG: Entro in Partita da invito o cerca partita");
            Intent i = new Intent(DashBoard.this, Game.class);
            startActivity(i);

        }
    };

    private Emitter.Listener on_partita_distrutta = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            System.out.println("WORDSWORDS_LOG: Torno al menu da fine partita");
            Intent j = GlobalState.getDesktop();
            j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            GlobalState.setTurno(1);
            startActivityIfNeeded(j, 0);
        }
    };


    //Ricezione dal Server della lista utenti in partita da far vedere nella Listview
    private Emitter.Listener on_lista_utenti_online = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject obj = (JSONObject) args[0];

            try
            {
                System.out.println("WORDSWORDS_LOG: Ricezione dal Server della lista utenti online " + obj.getString("utenti"));
                Bundle bibbo = new Bundle();
                bibbo.putString("lista" , obj.getString("utenti"));

                Message message = handler_online.obtainMessage();
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





    ListView lv;
    List<String> lista_gamers = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        {
            try {

                IO.Options opts = new IO.Options();
                opts.query = "token=" + fsocket;
                //opts.transports = new String[]{"websocket"};
                opts.forceNew = false;
                opts.reconnection = true;
                //Passo il Socket creato alla classe genearale
                GlobalState.setmSocket(IO.socket(GlobalState.getAddress(), opts));

            } catch (URISyntaxException e) {
            }
        }




        setContentView(R.layout.activity_dashboard);
        LinearLayout wait_layout = findViewById(R.id.wait_layout);
        wait_layout.setVisibility(View.GONE);




        if(!GlobalState.getSofia())
        {
            LinearLayout rux = (LinearLayout) findViewById(R.id.rules);
            rux.setVisibility(LinearLayout.VISIBLE);

        }else{
            final GridLayout ribbon = (GridLayout) findViewById(R.id.ribbon);
            ribbon.setVisibility(LinearLayout.VISIBLE);
        }



        GlobalState.getmSocket().connect();


        // Get ListView object from xml
        lv = (ListView) findViewById(R.id.lista_online);

        String[] fruits = new String[]{};

        lista_gamers = new ArrayList<String>(Arrays.asList(fruits));

        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, lista_gamers);
        // Assign adapter to ListView
        lv.setAdapter(arrayAdapter);

        // Add new Items to List
        lista_gamers.add(GlobalState.getMia_email());
        arrayAdapter.notifyDataSetChanged();

        final TypeWriter tw = (TypeWriter) findViewById(R.id.dashboard_title);
        tw.setText("");
    //    tw.animateText("Wordswords");
//        GlobalState.getTypewriter_multi_sound().start();
        final Animation button_translate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation button_scale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        final AnimationSet button_as = new AnimationSet(true);
        button_as.addAnimation(button_translate);
        button_as.addAnimation(button_scale);

//        Button btn_chiudi = (Button) findViewById(R.id.button_chiudi);
//        btn_chiudi.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                v.startAnimation(button_as);
//              //  GlobalState.getButtonSound().start();
//
//                finish();
//                System.exit(0);
//
//            }
//        });


        //GESTIONE SOCKET

        GlobalState.getmSocket().on("creata_partita", on_creata_partita).on("invito", on_notifica_invito).on(Socket.EVENT_DISCONNECT, on_disconnessione).on("non_ha_accettato", on_non_ha_accettato).on("invito_inviato", on_invito_inviato).on("lista_utenti", on_lista_utenti).on("entra_gioco", on_entra_gioco).on("partita_distrutta", on_partita_distrutta).on("lista_utenti_online", on_lista_utenti_online);

        // FINE GESTIONE SOCKET

        Intent g = getIntent();
        this.fsocket = g.getStringExtra("token");

        final String tokk = this.fsocket;
        //System.out.println("WORDSWORDS_LOG: >>>>>>>>>>>>>>>>>>>>>>>>>>   " + this.fsocket);

        JSONObject jo = new JSONObject();
        try {
            jo.put("token", tokk);
            GlobalState.getmSocket().emit("authenticate", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        GlobalState.getmSocket().emit("richiesta_utenti_online", "onlne");

        //BOTTONE CREA PARTITA
        final Button button = (Button) findViewById(R.id.button_start);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button.setEnabled(false);
                v.startAnimation(button_as);
           //     GlobalState.getButtonSound().start();
                GlobalState.getmSocket().emit("crea_partita", "Voglio unirmi a una partita");

            }
        });


        //BOTTONE CERCA PARTITA
    /*    final Button bunion = (Button) findViewById(R.id.button_union);

        bunion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bunion.setEnabled(false);
                v.startAnimation(button_as);
              //  GlobalState.getButtonSound().start();
                GlobalState.getmSocket().emit("cerca_partita", "Voglio unirmi a una partita casuale");
                System.out.println("WORDSWORDS_LOG:  bunion");

            }
        });
*/

        //LOGOUT
        final Button bottone_logout = (Button) findViewById(R.id.button_logout);

        bottone_logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onDestroy();
                v.startAnimation(button_as);
         //       GlobalState.getButtonSound().start();

                RequestQueue mRequestQueue;
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
                Network network = new BasicNetwork(new HurlStack());
                mRequestQueue = new RequestQueue(cache, network);
                mRequestQueue.start();

                JSONObject postParam = new JSONObject();
                try {
                    postParam.put("email", GlobalState.getMia_email());
                    postParam.put("password", GlobalState.getMia_password());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JSONObject pino = postParam;
                System.out.println("WORDSWORDS_LOG: JSON OUTPUT di logout->" + pino.toString());

                GlobalState.getmSocket().emit("distruggi_partita", "Cessa sta Ressa");

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        GlobalState.getAddress() + "logout", pino,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getInt("logout_ok") == 1) {
                                        finish();
                                    }
                                    else {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("WORDSWORDS_LOG: A Puttane " + error.getMessage());

                    }
                }) {

                    /**
                     * Passing some request headers
                     * */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };

                mRequestQueue.add(jsonObjReq);







            }
        });
        ImageView btn_toc = (ImageView) findViewById(R.id.tocca_dash);
        btn_toc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();

                DrawerLayout dl = (DrawerLayout)findViewById(R.id.drawer_layout_dashboard);
                dl.openDrawer(Gravity.LEFT);

            }
        });

        //CHIUDI REGOLE
        final Button bottone_close_rul = (Button) findViewById(R.id.button_okregole);

        bottone_close_rul.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                v.startAnimation(button_as);
             //   GlobalState.getButtonSound().start();

                final LinearLayout rilo = (LinearLayout) findViewById(R.id.rules);
                rilo.setVisibility(LinearLayout.GONE);
                final GridLayout ribbon = (GridLayout) findViewById(R.id.ribbon);
                ribbon.setVisibility(LinearLayout.VISIBLE);

                GlobalState.setSofia(true);


            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_dashboard);
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
        custom_toast.makeText(DashBoard.this,text,duration);
        custom_toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,0 ,0);
        custom_toast.show();
    }
}






