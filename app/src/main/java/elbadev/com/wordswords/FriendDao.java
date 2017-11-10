package elbadev.com.wordswords;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by romme86 on 11/9/2017.
 */
@Dao
public interface FriendDao {
    @Query("SELECT * FROM friend")
    List<Friend> getAll();

    @Insert
    void insertAll(Friend friends);

    @Delete
    void delete(Friend friend);
}
