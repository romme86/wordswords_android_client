package elbadev.com.wordswords;

import android.os.AsyncTask;
import android.os.Bundle;

/**
 * Created by romme86 on 11/9/2017.
 */

public class RemoveFriend extends AsyncTask<AppDatabase,Void,Bundle> {
    public String friendName = "";
    public RemoveFriend(String friendName){
        this.friendName = friendName;
    }
    @Override
    protected Bundle doInBackground(AppDatabase... dbs) {
        int count = dbs.length;
        System.out.println("WORDSWORDS_LOG: count: " + count);
        for (int i=0; i<count;i++){
            Friend friend = new Friend(friendName);
            dbs[i].friendDao().delete(friend);
            for (int d = 0; d<dbs[i].friendDao().getAll().size(); d++ ) {
                System.out.println("WORDSWORDS_LOG: in db: " + dbs[i].friendDao().getAll().get(d).nome);
            }

        }


        return null;
    }
}
