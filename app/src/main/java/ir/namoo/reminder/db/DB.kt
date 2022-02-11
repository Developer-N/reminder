package ir.namoo.reminder.db

import android.content.Context
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Database(entities = [Word::class], version = 2, exportSchema = false)
abstract class WordDB : RoomDatabase() {
    abstract fun WordDao(): WordDao

    companion object {
        private var instance: WordDB? = null
        public fun getInstance(context: Context): WordDB {
            return instance ?: synchronized(this) {
                val ins = Room.databaseBuilder(
                    context.applicationContext, WordDB::class.java, "word-db"
                ).fallbackToDestructiveMigration().build()
                instance = ins
                ins
            }
        }
    }
}

@Dao
interface WordDao {
    @Query("select * from words")
    suspend fun getAllWords(): List<Word>

    @Query("select * from words where id=:id")
    suspend fun getWordByID(id: Int): Word?

    @Query("select * from words where word=:word")
    suspend fun getWordByWord(word: String): Word?

    @Insert(onConflict = REPLACE)
    suspend fun insert(word: Word)

    @Update
    suspend fun update(word: Word)

    @Delete
    suspend fun delete(word: Word)
}

@Entity(tableName = "words", indices = [Index(value = ["word"], unique = true)])
data class Word(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "word")
    var word: String,
    @ColumnInfo(name = "insert_date")
    var insertDate: Long
)