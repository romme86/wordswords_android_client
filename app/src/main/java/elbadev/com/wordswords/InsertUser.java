package elbadev.com.wordswords;

import android.os.AsyncTask;
import android.os.Bundle;

/**
 * Created by romme86 on 11/9/2017.
 */

public class InsertUser extends AsyncTask<AppDatabase,Void,Bundle> {
    public String userName = "";
    public String userPassword = "";
    public InsertUser(String userName, String userPassword){
        this.userName = userName;
        this.userPassword = userPassword;
    }
    @Override
    protected Bundle doInBackground(AppDatabase... dbs) {
        int count = dbs.length;
        System.out.println("WORDSWORDS_LOG: count: " + count);
        for (int i=0; i<count;i++){
            User user = new User(userName,userPassword);
            //cerco se l'utente é giá nel DB
            String nome = dbs[i].userDao().findByName(userName);
            System.out.println("WORDSWORDS_LOG: nome: " + nome + " username " + userName);
            if(nome == null){
                dbs[i].userDao().insertAll(user);
                for (int d = 0; d<dbs[i].userDao().getAll().size(); d++ ) {
                    System.out.println("WORDSWORDS_LOG: in db: " + dbs[i].userDao().getAll().get(d).nome);
                }
            }else if(!nome.equals(userName)){
                dbs[i].userDao().insertAll(user);
                for (int d = 0; d<dbs[i].userDao().getAll().size(); d++ ) {
                    System.out.println("WORDSWORDS_LOG: in db: " + dbs[i].userDao().getAll().get(d).nome);
                }
            }
        }


        return null;
    }
}
