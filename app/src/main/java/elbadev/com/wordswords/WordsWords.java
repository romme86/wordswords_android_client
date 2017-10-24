package elbadev.com.wordswords;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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


    @Override
    public void onBackPressed() {
        Toast.makeText(this, GlobalState.getRandomWisenessBackButton(), Toast.LENGTH_SHORT).show();
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
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        final boolean blnBind = bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        System.out.println("WORDSWORDS_LOG: Servizio bindato." + String.valueOf(blnBind));


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
        Button btn_chiudi = (Button) findViewById(R.id.button_chiudi);
        btn_chiudi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();

                finish();
                System.exit(0);

            }
        });
        ArrayList<String> sku_list = new ArrayList<>();
        sku_list.add("fogli_di_carta");
        final Bundle query_sku = new Bundle();
        query_sku.putStringArrayList("ITEM_ID_LIST",sku_list);

        mHelper = new IabHelper(this,GlobalState.getBase64publicKey());
        Button btn_compra = (Button) findViewById(R.id.button_compra);



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

        btn_compra.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(button_as);
                GlobalState.getButtonSound().start();
                System.out.println("WORDSWORDS_LOG: cliccato compra");
                try {
                    int result = mService.isBillingSupported(3, getPackageName(), "inapp");
                    System.out.println("WORDSWORDS_LOG: Servizio ok per inapp purchase? " + String.valueOf(result));
                }catch (RemoteException e){
                    e.printStackTrace();
                }
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
                System.out.println("WORDSWORDS_LOG: chiedo di vedere il negozio di: " + getPackageName());
                //questa va fatto in altro tred
                UnDueTred tred = new UnDueTred(mService);
                tred.doInBackground();
                //diotred

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
                                try {
                                    if (response.length() != 0) {
                                        if (response.getInt("utente_registrato") == 1) {
                                            Toast.makeText(WordsWords.this, "Ti sei registrato/a! Accedi per iniziare a giocare!", Toast.LENGTH_SHORT).show();
                                            Button rec = (Button) findViewById(R.id.button_registra);
                                            rec.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(WordsWords.this, " Utente non registrato ", Toast.LENGTH_SHORT).show();
                                            System.out.println("WORDSWORDS_LOG: Azz " + response);
                                        }
                                    } else {
                                        Toast.makeText(WordsWords.this, "Nessun username immesso", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WordsWords.this, "L'utente non esiste o il server non risponde.", Toast.LENGTH_SHORT).show();
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
                if (mService != null) {
                    unbindService(mServiceConn);
                }
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
                                    } else {
                                        Toast.makeText(WordsWords.this, " Utente non esistente ", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WordsWords.this, "L'utente non esiste o il server non risponde.", Toast.LENGTH_SHORT).show();
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
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
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
    public void receivedBroadcast() {

    }
}

