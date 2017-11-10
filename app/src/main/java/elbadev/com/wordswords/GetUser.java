package elbadev.com.wordswords;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by romme86 on 11/9/2017.
 */

public class GetUser extends AsyncTask<AppDatabase,Void,Void> {
    public String friendName = "";
    TextView username_tv = null;
    TextView password_tv= null;
    public GetUser(TextView username_tv,TextView password_tv){
        //this.friendName = friendName;
        this.username_tv = username_tv;
        this.password_tv = password_tv;
    }
    @Override
    protected Void doInBackground( AppDatabase... dbs) {
        User result = null;
        if(dbs[0] != null){
            int countUsers = dbs[0].userDao().getAll().size();
            if(countUsers != 0) {
                result = dbs[0].userDao().getAll().get(countUsers-1);
                username_tv.setText(result.nome);
                password_tv.setText(result.password);
            }
        }
        return null;
    }
}
