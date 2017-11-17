package elbadev.com.wordswords;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by romme86 on 11/8/2017.
 */

@Entity
public class User {

    @PrimaryKey
    @NonNull
    public String nome;

    @ColumnInfo
    public String password;

    public User(String nome,String password)
    {
        this.nome = nome;
        this.password = password;
    }
}
