package elbadev.com.wordswords;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by romme86 on 10/20/2017.
 */

public class UnDueTred extends AsyncTask<Void,Void,Bundle> {
    IInAppBillingService mService;
    private String prezzo_bigliettino = "";
    private String prezzo_fogli_di_carta = "";
    UnDueTred(IInAppBillingService mService){
        super();
        this.mService = mService;
    }
    @Override
    protected Bundle doInBackground(Void... voids) {
        ArrayList<String> skuList = new ArrayList<String> ();
        skuList.add("fogli_di_carta");
        skuList.add("bigliettino");
        Bundle querySkus = new Bundle();
        Bundle skuDetails = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        try {
            skuDetails = mService.getSkuDetails(3, "elbadev.com.wordswords", "inapp", querySkus);
            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                System.out.println("WORDSWORDS_LOG: entro nel negozio. numero oggetti: " + responseList.size());
                for (String thisResponse : responseList) {
                    JSONObject object = new JSONObject(thisResponse);
                    String sku = object.getString("productId");
                    String price = object.getString("price");
                    if (sku.equals("bigliettino")){
                        prezzo_bigliettino = price;
                        System.out.println("WORDSWORDS_LOG: bigliettino.");
                    }
                    else if (sku.equals("fogli_di_carta")){
                        prezzo_fogli_di_carta = price;
                        System.out.println("WORDSWORDS_LOG: fogli.");
                    }
                }
            }
        }catch (RemoteException e){
            System.out.println("WORDSWORDS_LOG: Errore mentre cercavo di vedere cosa si puó comprare.");
            e.printStackTrace();
        }catch (JSONException je){
        System.out.println("WORDSWORDS_LOG: Errore mentre cercavo di vedere i prodotti.");
        je.printStackTrace();
    }
        return skuDetails;
    }
}
