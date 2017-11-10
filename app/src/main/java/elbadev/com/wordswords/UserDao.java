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
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT nome FROM user WHERE nome LIKE :name")
    String findByName(String name);
    @Insert
    void insertAll(User users);

    @Delete
    void delete(User user);
}
