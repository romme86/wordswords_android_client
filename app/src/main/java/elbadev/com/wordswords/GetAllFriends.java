package elbadev.com.wordswords;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by romme86 on 11/9/2017.
 */

public class GetAllFriends extends AsyncTask<AppDatabase,Void,Bundle> {
    public ListView friendsList= null;
    public Context ct = null;

    public GetAllFriends(ListView friendsList, Context ct) {
        this.friendsList = friendsList;
        this.ct = ct;
    }

    @Override
    protected Bundle doInBackground(AppDatabase... dbs) {
        int count = dbs.length;
        System.out.println("WORDSWORDS_LOG: getting all friends:  " + count) ;
        if(count == 1){
            ArrayList<String> arrayNomiAmici = new ArrayList<String>();
            String nomeAmico = "";
            for (int d = 0; d<dbs[0].friendDao().getAll().size(); d++ ) {
                nomeAmico = dbs[0].friendDao().getAll().get(d).nome;
                System.out.println("WORDSWORDS_LOG: in db: " + nomeAmico);
                arrayNomiAmici.add(nomeAmico);
            }
            ArrayAdapter adapter = new ArrayAdapter(ct,R.layout.custom_list_row,arrayNomiAmici);
            friendsList.setAdapter(adapter);

        }
        return null;
    }
}
