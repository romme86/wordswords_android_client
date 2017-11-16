package elbadev.com.wordswords;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
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

import com.android.vending.billing.IInAppBillingService;
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
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import elbadev.com.wordswords.util.IabBroadcastReceiver;
import elbadev.com.wordswords.util.IabHelper;
import elbadev.com.wordswords.util.IabResult;
import elbadev.com.wordswords.util.Inventory;
import elbadev.com.wordswords.util.Purchase;

public class WordsWords extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private IabHelper mHelper;
    private IabBroadcastReceiver mBroadcastReceiver;
    private IInAppBillingService mService;
    private String prezzo_bigliettino = "";
    private String prezzo_fogli_di_carta = "";
    private String id_fogli_di_carta = "";
    private String id_bigliettino = "";
    private String id_block_notes = "";
    private String prezzo_block_notes;


    @Override
    public void onBackPressed() {
        customToast(GlobalState.getRandomWisenessBackButton(),Toast.LENGTH_LONG);
        //super.onBackPressed();
    }


    public void esistenza() {
        System.out.println("WORDSWORDS_LOG: inizio volley alive");

        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://46.101.105.29/alive_w";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (GlobalState.getPrima_esistenza()) {
                                GlobalState.setDate_alive(response.getString("date_alive"));
                                GlobalState.setPrima_esistenza(false);
                            }

                            System.out.println("WORDSWORDS_LOG: reboot nuova " + response.getString("date_alive") + " vecchia " + GlobalState.getDate_alive());
                            if (!GlobalState.getDate_alive().equals(response.getString("date_alive"))) {
                                GlobalState.setRebootta(true);
                                System.out.println("WORDSWORDS_LOG: reboot volley  " + GlobalState.getRebootta());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("WORDSWORDS_LOG: reboot Error.Response" + error.getMessage());
                    }
                }
        );
        if(GlobalState.getRichiesteVolleyInCorso()<3){
            queue.add(getRequest);
            GlobalState.setRichiesteVolleyInCorso(GlobalState.getRichiesteVolleyInCorso()+1);
            System.out.println("WORDSWORDS_LOG: mando richiesta alive in coda");
        }else{
            queue.cancelAll(queue);
            GlobalState.setRichiesteVolleyInCorso(0);
            System.out.println("WORDSWORDS_LOG: ci so troppe cose nella coda di volley");
        }
        System.out.println("WORDSWORDS_LOG: fine volley alive");
    }


//        JSONObject postParam = new JSONObject();
//
//        JSONObject pino = postParam;
//        System.out.println("WORDSWORDS_LOG: JSON OUTPUT->" + pino.toString());
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
//                Request.Method.POST,
//                GlobalState.getAddress() + "alive",
//                pino,
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            System.out.println("WORDSWORDS_LOG: Risposta Alive :::::  "  + response.getString("date_alive"));
//
//                            GlobalState.setDate_alive(response.getString("date_alive"));
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(WordsWords.this, "A Puttane alive " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                System.out.println("WORDSWORDS_LOG: A Puttane alive " + error.getMessage());
//                error.printStackTrace();
//            }
//        }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                return headers;
//            }
//        };
//
//        mRequestQueue.add(jsonObjReq);

    private Handler credentialHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            TextView email_1 = (TextView) findViewById(R.id.email);
            TextView password_1 = (TextView) findViewById(R.id.password);
            email_1.setText(message.getData().getString("username"));
            password_1.setText(message.getData().getString("password"));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ServiceConnection mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                System.out.println("WORDSWORDS_LOG: Servizio connesso.");
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FontsOverride.setDefaultFont(this, "DEFAULT", "OldNewspaperTypes.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "OldNewspaperTypes.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "OldNewspaperTypes.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "OldNewspaperTypes.ttf");

        setContentView(R.layout.activity_wordswords);
        GlobalState.setTypewriter_multi_sound(MediaPlayer.create(this, R.raw.typewriter_multitype_2));
        GlobalState.setTypewriter_button_sound_0(MediaPlayer.create(this, R.raw.typewriter_singletype_1));
        GlobalState.setTypewriter_button_sound_1(MediaPlayer.create(this, R.raw.typewriter_singletype_2));
        GlobalState.setTypewriter_button_sound_2(MediaPlayer.create(this, R.raw.typewriter_singletype_3));
        GlobalState.setTypewriter_button_sound_3(MediaPlayer.create(this, R.raw.typewriter_singletype_4));
        GlobalState.setTypewriter_button_sound_4(MediaPlayer.create(this, R.raw.typewriter_singletype_5));
        GlobalState.setTypewriter_drin(MediaPlayer.create(this, R.raw.typewriter_drin_1));
        GlobalState.setTypewriter_paper(MediaPlayer.create(this, R.raw.typewriter_paper_1));
        GlobalState.setTypewriter_paper_3(MediaPlayer.create(this, R.raw.typewriter_paper_3));
        final Animation button_translate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation button_scale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        final AnimationSet button_as = new AnimationSet(true);
        button_as.addAnimation(button_translate);
        button_as.addAnimation(button_scale);
        //esistenza();





        final TypeWriter tw = (TypeWriter) findViewById(R.id.title_login);
        tw.setText("");
        tw.animateText("Wordswords");
        GlobalState.getTypewriter_multi_sound().start();
//        final Timer t = new Timer();
//
//        t.scheduleAtFixedRate(new TimerTask() {
//
//                                  @Override
//                                  public void run() {
//
//                                      esistenza();
//
//                                      if (GlobalState.getRebootta()) {
//                                          //Reboot App
//                                          Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
//                                          i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                          startActivity(i);
//                                          t.cancel();
//                                          System.out.println("WORDSWORDS_LOG: reboot riavvio " + GlobalState.getRebootta());
//                                          GlobalState.setRebootta(false);
//                                          GlobalState.setPrima_esistenza(true);
//                                      }
//                                  }
//                              },
//                3000,
//                40000);
        final GridLayout ribbon = (GridLayout) findViewById(R.id.ribbon);
        final LinearLayout buy_panel = (LinearLayout) findViewById(R.id.buy_panel);
        Button btn_chiudi = (Button) findViewById(R.id.button_chiudi);
        btn_chiudi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();

                finish();
                System.exit(0);

            }
        });



        final IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

                System.out.println("WORDSWORDS_LOG: query inventario fatta.");

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Is it a failure?
                if (result.isFailure()) {
                    System.out.println("WORDSWORDS_LOG: errore. " + result);
                    return;
                }

                System.out.println("WORDSWORDS_LOG: la query è un successone. " + result);
                System.out.println("WORDSWORDS_LOG: inventario: " + inventory.getSkuDetails("fogli_di_carta"));

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

//                // Do we have the premium upgrade?
//                Purchase premiumPurchase = inventory.getPurchase("fogli_di_carta");
//                boolean mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
//                Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
//
//                // First find out which subscription is auto renewing
//                Purchase gasMonthly = inventory.getPurchase(SKU_INFINITE_GAS_MONTHLY);
//                Purchase gasYearly = inventory.getPurchase(SKU_INFINITE_GAS_YEARLY);
//                if (gasMonthly != null && gasMonthly.isAutoRenewing()) {
//                    mInfiniteGasSku = SKU_INFINITE_GAS_MONTHLY;
//                    mAutoRenewEnabled = true;
//                } else if (gasYearly != null && gasYearly.isAutoRenewing()) {
//                    mInfiniteGasSku = SKU_INFINITE_GAS_YEARLY;
//                    mAutoRenewEnabled = true;
//                } else {
//                    mInfiniteGasSku = "";
//                    mAutoRenewEnabled = false;
//                }
//
//                // The user is subscribed if either subscription exists, even if neither is auto
//                // renewing
//                mSubscribedToInfiniteGas = (gasMonthly != null && verifyDeveloperPayload(gasMonthly))
//                        || (gasYearly != null && verifyDeveloperPayload(gasYearly));
//                Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
//                        + " infinite gas subscription.");
//                if (mSubscribedToInfiniteGas) mTank = TANK_MAX;
//
//                // Check for gas delivery -- if we own gas, we should fill up the tank immediately
//                Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
//                if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
//                    Log.d(TAG, "We have gas. Consuming it.");
//                    try {
//                        mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);
//                    } catch (IabHelper.IabAsyncInProgressException e) {
//                        complain("Error consuming gas. Another async operation in progress.");
//                    }
//                    return;
//                }
//
//                updateUi();
//                setWaitScreen(false);
//                Log.d(TAG, "Initial inventory query finished; enabling main UI.");
            }
        };
        mHelper = new IabHelper(this,GlobalState.getBase64publicKey());
        if(!GlobalState.getSetup_payment_done()){
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    if(!result.isSuccess()){
                        System.out.println("WORDSWORDS_LOG: setup pagamento a puttane " + result.toString());
                    }else{
                        System.out.println("WORDSWORDS_LOG: setup pagamento ok");
                        GlobalState.setSetup_payment_done(true);
                    }
                    System.out.println("WORDSWORDS_LOG: setup pagamento ok " + result.toString());
                    if (mHelper == null) return;
                    mBroadcastReceiver = new IabBroadcastReceiver(WordsWords.this);
                    IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                    registerReceiver(mBroadcastReceiver, broadcastFilter);
                    try {
                        System.out.println("WORDSWORDS_LOG: provo query inventario ");
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        System.out.println("WORDSWORDS_LOG: Error querying inventory. Another async operation in progress.");
                    }
                }

            });
        }
        //setto il db
        GlobalState.setDb(Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "archivio").build());
        //Setto username e password se le avevo salvate nel DB
        TextView email_1 = (TextView) findViewById(R.id.email);
        TextView password_1 = (TextView) findViewById(R.id.password);
        //prendo l'ultimo user inserito
        new GetUser(credentialHandler).execute(GlobalState.getDb());

        //----------------------------------------------------
        Button button_compra_fogli = (Button) findViewById(R.id.button_compra_fogli);
        button_compra_fogli.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                try {
                    int result = mService.isBillingSupported(3, getPackageName(), "inapp");
                    System.out.println("WORDSWORDS_LOG: Servizio ok per inapp purchase? " + String.valueOf(result));
                }catch (RemoteException e){
                    e.printStackTrace();
                }
                try {
                    Bundle buyIntentBundle = mService.getBuyIntent(3, "elbadev.com.wordswords",id_fogli_di_carta, "inapp", "fogli_di_carta");
                    PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                    startIntentSenderForResult(pendingIntent.getIntentSender(),1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),Integer.valueOf(0));
                }catch (RemoteException e){
                    e.printStackTrace();
                }catch (IntentSender.SendIntentException e){
                    e.printStackTrace();
                }

            }
        });
        Button button_compra_bigliettino = (Button) findViewById(R.id.button_compra_bigliettino);
        button_compra_bigliettino.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                System.out.println("WORDSWORDS_LOG: cliccato compra bigliettino");
                try {
                    Bundle buyIntentBundle = mService.getBuyIntent(3, "elbadev.com.wordswords",id_bigliettino, "inapp", "bigliettino");
                    PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                    startIntentSenderForResult(pendingIntent.getIntentSender(),1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),Integer.valueOf(0));
                }catch (RemoteException e){
                    e.printStackTrace();
                }catch (IntentSender.SendIntentException e){
                    e.printStackTrace();
                }
            }
        });
        Button button_compra_block_notes = (Button) findViewById(R.id.button_compra_block_notes);
        button_compra_block_notes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                System.out.println("WORDSWORDS_LOG: cliccato compra button_compra_block_notes");
                try {
                    Bundle buyIntentBundle = mService.getBuyIntent(3, "elbadev.com.wordswords",id_block_notes, "inapp", "id_block_notes");
                    PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                    startIntentSenderForResult(pendingIntent.getIntentSender(),1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),Integer.valueOf(0));
                }catch (RemoteException e){
                    e.printStackTrace();
                }catch (IntentSender.SendIntentException e){
                    e.printStackTrace();
                }
            }
        });


        Button btn_compra = (Button) findViewById(R.id.button_compra);
        btn_compra.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                System.out.println("WORDSWORDS_LOG: cliccato compra");
                buy_panel.setVisibility(LinearLayout.VISIBLE);
                ribbon.setVisibility(LinearLayout.GONE);
                System.out.println("WORDSWORDS_LOG: chiedo di vedere il negozio di: " + getPackageName());
                //questa va fatto in altro tred
                UnDueTred tred = new UnDueTred(mService);
                Bundle skuDetails = new Bundle();
                skuDetails = tred.doInBackground();
                //diotred
                try {
                    int response = skuDetails.getInt("RESPONSE_CODE");
                    if (response == 0) {
                        ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                        System.out.println("WORDSWORDS_LOG: entro nel negozio. numero oggetti: " + responseList.size());
                        for (String thisResponse : responseList) {
                            JSONObject object = new JSONObject(thisResponse);
                            String sku = object.getString("productId");
                            String price = object.getString("price");
                            if (sku.equals("bigliettino")) {
                                prezzo_bigliettino = price;
                                id_bigliettino = sku;
                                System.out.println("WORDSWORDS_LOG: bigliettino. ID " + id_bigliettino + " PREZZO " + prezzo_bigliettino);
                            } else if (sku.equals("fogli_di_carta")) {
                                prezzo_fogli_di_carta = price;
                                id_fogli_di_carta = sku;
                                System.out.println("WORDSWORDS_LOG: fogli. ID " + id_fogli_di_carta + " PREZZO " + prezzo_fogli_di_carta);
                            }else if (sku.equals("block_notes")) {
                                prezzo_block_notes = price;
                                id_block_notes = sku;
                                System.out.println("WORDSWORDS_LOG: block notes. ID " + id_block_notes + " PREZZO " + prezzo_block_notes);
                            }
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawers();
            }
        });
        Button button_indietro = (Button) findViewById(R.id.button_indietro);
        button_indietro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                System.out.println("WORDSWORDS_LOG: cliccato indietro");
                buy_panel.setVisibility(LinearLayout.GONE);
                ribbon.setVisibility(LinearLayout.VISIBLE);
            }
        });

        ImageView btn_toc = (ImageView) findViewById(R.id.tocca);
        btn_toc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();

                DrawerLayout dl = (DrawerLayout)findViewById(R.id.drawer_layout);
                dl.openDrawer(Gravity.LEFT);


            }
        });

        Button button = (Button) findViewById(R.id.button_registra);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                RequestQueue mRequestQueue;

                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
                Network network = new BasicNetwork(new HurlStack());
                mRequestQueue = new RequestQueue(cache, network);
                if(GlobalState.getRichiesteVolleyInCorso()%3 == 0){
                    mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
                        @Override
                        public boolean apply(Request<?> request) {
                            return true;
                        }
                    });
                }
                mRequestQueue = new RequestQueue(cache, network);
                mRequestQueue.start();

                TextView email = (TextView) findViewById(R.id.email);
                TextView password = (TextView) findViewById(R.id.password);

                JSONObject postParam = new JSONObject();
                try {
                    postParam.put("email", email.getText());
                    postParam.put("password", password.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject pino = postParam;
                System.out.println("WORDSWORDS_LOG: JSON OUTPUT->" + pino.toString());

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        GlobalState.getAddress() + "registration", pino,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println("WORDSWORDS_LOG: registrato 0 " + response);
                                int errore = 0;
                                try{
                                    errore =response.getInt("errore");
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                                if(errore == 0) {
                                    try {
                                        if (response.length() != 0) {
                                            if (response.getInt("utente_registrato") == 1) {
                                                customToast("Ti sei registrato/a! Accedi per iniziare a giocare!", Toast.LENGTH_LONG);
//                                            Toast.makeText(WordsWords.this, , Toast.LENGTH_SHORT).show();
                                                Button rec = (Button) findViewById(R.id.button_registra);
                                                rec.setVisibility(View.GONE);
                                                System.out.println("WORDSWORDS_LOG: registrato1 " + response);
                                            } else {
                                                customToast("Questo utente non é registrato", Toast.LENGTH_LONG);
//                                            Toast.makeText(WordsWords.this, " Utente non registrato ", Toast.LENGTH_SHORT).show();
                                                System.out.println("WORDSWORDS_LOG: registrato 2 " + response);
                                            }
                                        } else {
                                            System.out.println("WORDSWORDS_LOG: registrato 3 " + response);
                                            customToast("Devi inserire uno username! (e poi anche la password)", Toast.LENGTH_LONG);
//                                        Toast.makeText(WordsWords.this, "Nessun username immesso", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    customToast("Questo utente potrebbe essere giá registrato.", Toast.LENGTH_LONG);
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customToast("Devi inserire Username e Password prima! É anche possibile che il server non risponda.",Toast.LENGTH_LONG);
//                        Toast.makeText(WordsWords.this, "L'utente non esiste o il server non risponde.", Toast.LENGTH_SHORT).show();
                        System.out.println("WORDSWORDS_LOG: A Puttane " + error.getMessage());
                        System.out.println("WORDSWORDS_LOG: registrato puttane ");

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







        Button button2 = (Button) findViewById(R.id.button_salva);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RequestQueue mRequestQueue;
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
                Network network = new BasicNetwork(new HurlStack());
                mRequestQueue = new RequestQueue(cache, network);
                mRequestQueue.start();
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                final TextView email = (TextView) findViewById(R.id.email);
                final TextView password = (TextView) findViewById(R.id.password);

                JSONObject postParam = new JSONObject();
                try {
                    postParam.put("email", email.getText());
                    postParam.put("password", password.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //RISCHIOSO
//                if (mService != null) {
//                    unbindService(mServiceConn);
//                }
                //FINE RISCHIOSO

                JSONObject pino = postParam;
                System.out.println("WORDSWORDS_LOG: JSON OUTPUT->" + pino.toString());

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                        Request.Method.POST,
                        GlobalState.getAddress() + "login",
                        pino,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    System.out.println("WORDSWORDS_LOG: Utente trovato:::::  " + response.getString("utente_trovato"));
                                    if (response.getInt("utente_trovato") == 1) {

                                        GlobalState.setMia_email(email.getText().toString());
                                        GlobalState.setMia_password(password.getText().toString());
//                                      esistenza();
                                        Intent i = new Intent(WordsWords.this, DashBoard.class);
                                        GlobalState.setDesktop(i);
                                        i.putExtra("token", response.getString("token"));
                                        startActivity(i);
                                        // salvo le credenziali nel DB
                                        new InsertUser(GlobalState.getMia_email(),GlobalState.getMia_password()).execute(GlobalState.getDb());
                                    } else {
                                        customToast("Questo utente non esiste, puoi registrarti cliccando su REGISTRATI",Toast.LENGTH_LONG);
//                                        Toast.makeText(WordsWords.this, " Utente non esistente ", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customToast("Prima inserisci Username e Password! Se le hai inserite potresti averle sbagliate.",Toast.LENGTH_LONG);
//                        Toast.makeText(WordsWords.this, "L'utente non esiste o il server non risponde.", Toast.LENGTH_SHORT).show();
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

        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                GlobalState.getTypewriter_drin().start();
                if (mService != null) {
                    unbindService(mServiceConn);
                }
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {

                Intent serviceIntent =
                        new Intent("com.android.vending.billing.InAppBillingService.BIND");
                serviceIntent.setPackage("com.android.vending");
                final boolean blnBind = bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
                System.out.println("WORDSWORDS_LOG: Servizio bindato." + String.valueOf(blnBind));
                super.onDrawerOpened(drawerView);
                GlobalState.getTypewriter_paper_3().start();

            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

    }
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("WORDSWORDS_LOG: sei arrivato a onActivityResult!");
        if (requestCode == 1001) {

            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
            System.out.println("WORDSWORDS_LOG: il codice era 1001! il result code é " + resultCode + " il response code é " + responseCode + " inapp sig " + dataSignature);
            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    System.out.println("WORDSWORDS_LOG: You have bought the " + sku + ". Excellent choice,adventurer!");
                }
                catch (JSONException e) {
                    System.out.println("WORDSWORDS_LOG: Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void receivedBroadcast() {

    }
    public void customToast(String text, int duration){
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.toast_layout,null);

        TextView tv = (TextView) view.findViewById(R.id.custom_text);
        tv.setText(text);
        Toast custom_toast = new Toast(this);
        custom_toast.setView(view);
        custom_toast.makeText(WordsWords.this,text,duration);
        custom_toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,0 ,0);
        custom_toast.show();
    }

}

