package elbadev.com.wordswords;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by romme86 on 11/8/2017.
 */

@Entity
public class Friend {

    @PrimaryKey
    public String nome;

    public Friend(String nome){
        this.nome = nome;
    }
}
