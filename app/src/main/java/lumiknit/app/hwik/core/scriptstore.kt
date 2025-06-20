package lumiknit.app.hwik.core

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.ProvidedTypeConverter
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.datetime.Instant

@ProvidedTypeConverter
class Converters {
	@TypeConverter
	fun instantToLong(instant: Instant): Long {
		return instant.toEpochMilliseconds()
	}

	@TypeConverter
	fun longToInstant(value: Long): Instant {
		return Instant.fromEpochMilliseconds(value)
	}
}

@ProvidedTypeConverter
class SourceScriptTypeConverter {
	@TypeConverter
	fun stringToSourceScript(value: String): SourceScript {
		return SourceScript.fromJSON(value)
	}

	@TypeConverter
	fun sourceScriptToString(script: SourceScript): String {
		return script.toJSON()
	}
}

/**
 * SourceOrigin represents the origin of a source.
 * If the source origin is URL, it'll be fetch from the web.
 * Otherwise, it'll be a fixed string.
 */
@Entity(
	tableName = "SSOriginEntity"
)
data class SSOriginEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	val url: String? = null,
	val lastFetched: Instant,
	val src: String? = null,
)

@Dao
interface SSOriginDao {
	@Insert
	suspend fun insert(origin: SSOriginEntity)

	@Query("Select * FROM SSOriginEntity")
	suspend fun getAll(): List<SSOriginEntity>

	@Query("SELECT * FROM SSOriginEntity WHERE id = :id")
	suspend fun getById(id: Int): SSOriginEntity?

	@Update
	suspend fun update(origin: SSOriginEntity)

	@Query("DELETE FROM SSOriginEntity WHERE id = :id")
	suspend fun deleteById(id: Int)
}

@Entity(
	tableName = "SSItemEntity"
)
data class SSItemEntity(
	@PrimaryKey
	val id: String,

	val originID: Int,

	val script: SourceScript
)

@Dao
interface SSItemDao {
	@Insert
	suspend fun insert(script: SSItemEntity)

	@Query("SELECT * FROM SSItemEntity")
	suspend fun getAll(): List<SSItemEntity>

	@Query("SELECT * FROM SSItemEntity WHERE id = :id")
	suspend fun getById(id: String): SSItemEntity?

	@Update
	suspend fun update(script: SSItemEntity)

	@Query("DELETE FROM SSItemEntity WHERE id = :id")
	suspend fun deleteById(id: String)

	@Query("DELETE FROM SSItemEntity WHERE originID = :originID")
	suspend fun deleteByOriginId(originID: Int)
}


@Database(
	entities = [SSOriginEntity::class, SSItemEntity::class],
	version = 1,
	exportSchema = false
)
@TypeConverters(
	value = [
		Converters::class,
		SourceScriptTypeConverter::class
	]
)
abstract class SourceScriptDatabase : RoomDatabase() {
	abstract fun ssOriginDao(): SSOriginDao
	abstract fun ssScriptDao(): SSItemDao

	companion object {
		@Volatile
		private var INSTANCE: SourceScriptDatabase? = null

		fun getDatabase(context: Context): SourceScriptDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = androidx.room.Room.databaseBuilder(
					context.applicationContext,
					SourceScriptDatabase::class.java,
					"source_script_database"
				)
					.addTypeConverter(Converters())
					.addTypeConverter(SourceScriptTypeConverter())
					.build()
				INSTANCE = instance
				instance
			}
		}
	}
}
