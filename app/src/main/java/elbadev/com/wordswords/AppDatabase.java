package elbadev.com.wordswords;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by romme86 on 11/9/2017.
 */
@Database(entities = {Friend.class,User.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FriendDao friendDao();
    public abstract UserDao userDao();
}

