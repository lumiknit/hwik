package lumiknit.app.hwik.core

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.ProvidedTypeConverter
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update

@ProvidedTypeConverter
class ArticleConverter {
	@TypeConverter
	fun stringToArticle(value: String): Article {
		return Article.fromJSON(value)
	}

	@TypeConverter
	fun articleToString(article: Article): String {
		return article.toJSON()
	}
}

@Entity(
	tableName = "ArticleEntity"
)
data class ArticleEntity(
	@PrimaryKey()
	val href: String,

	var article: Article,

	var read: Boolean = false
)

@Dao
interface ArticleDao {
	@Insert
	suspend fun insert(script: ArticleEntity)

	@Query("SELECT * FROM ArticleEntity")
	suspend fun getAll(): List<ArticleEntity>

	@Query("SELECT * FROM ArticleEntity WHERE href = :href")
	suspend fun getById(href: String): ArticleEntity?

	@Update
	suspend fun update(script: ArticleEntity)

	@Query("DELETE FROM ArticleEntity WHERE href = :href")
	suspend fun deleteById(href: String)
}


@Database(
	entities = [ArticleEntity::class],
	version = 1,
	exportSchema = false
)
@TypeConverters(
	value = [
		ArticleConverter::class
	]
)
abstract class ArticleDatabase : RoomDatabase() {
	abstract fun psScriptDao(): SSItemDao

	companion object {
		@Volatile
		private var INSTANCE: ArticleDatabase? = null

		fun getDatabase(context: Context): ArticleDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					ArticleDatabase::class.java,
					"article_database"
				)
					.addTypeConverter(ArticleConverter())
					.build()
				INSTANCE = instance
				instance
			}
		}
	}
}
