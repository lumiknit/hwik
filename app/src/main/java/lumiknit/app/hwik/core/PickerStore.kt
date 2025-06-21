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
class KotlinInstantConverter {
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
class PickerScriptConverter {
	@TypeConverter
	fun stringToPickerScript(value: String): PickerScript {
		return PickerScript.fromJSON(value)
	}

	@TypeConverter
	fun pickerScriptToString(script: PickerScript): String {
		return script.toJSON()
	}
}

@Entity(
	tableName = "PSSourceEntity"
)
data class PSSourceEntity(
	@PrimaryKey
	val id: String = "",

	// If fetched from URL, this will be the URL.
	val url: String? = null,

	val lastFetched: Instant,

	val rawScript: String,

	val script: PickerScript
)

@Dao
interface SSItemDao {
	@Insert
	suspend fun insert(script: PSSourceEntity)

	@Query("SELECT * FROM PSSourceEntity")
	suspend fun getAll(): List<PSSourceEntity>

	@Query("SELECT * FROM PSSourceEntity WHERE id = :id")
	suspend fun getById(id: String): PSSourceEntity?

	@Update
	suspend fun update(script: PSSourceEntity)

	@Query("DELETE FROM PSSourceEntity WHERE id = :id")
	suspend fun deleteById(id: String)
}


@Database(
	entities = [PSSourceEntity::class],
	version = 1,
	exportSchema = false
)
@TypeConverters(
	value = [
		KotlinInstantConverter::class,
		PickerScriptConverter::class
	]
)
abstract class PSDatabase : RoomDatabase() {
	abstract fun psScriptDao(): SSItemDao

	companion object {
		@Volatile
		private var INSTANCE: PSDatabase? = null

		fun getDatabase(context: Context): PSDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = androidx.room.Room.databaseBuilder(
					context.applicationContext,
					PSDatabase::class.java,
					"picker_script_database"
				)
					.addTypeConverter(KotlinInstantConverter())
					.addTypeConverter(PickerScriptConverter())
					.build()
				INSTANCE = instance
				instance
			}
		}
	}
}
