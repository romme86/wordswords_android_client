package elbadev.com.wordswords;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/**
 * Created by romme86 on 11/9/2017.
 */

public class GetUser extends AsyncTask<AppDatabase,Void,Void> {
    Handler credentialHandler;

    public GetUser(Handler credentialHandler) {
        this.credentialHandler = credentialHandler;
    }

    @Override
    protected Void doInBackground( AppDatabase... dbs) {
        User result = null;
        if(dbs[0] != null){
            int countUsers = dbs[0].userDao().getAll().size();
            if(countUsers != 0) {
                result = dbs[0].userDao().getAll().get(countUsers-1);
//                username_tv.setText(result.nome);
//                password_tv.setText(result.password);
                Message credential_message = credentialHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("username", result.nome);
                bundle.putString("password", result.password);
                credential_message.setData(bundle);
                credential_message.sendToTarget();
            }
        }
        return null;
    }
}
