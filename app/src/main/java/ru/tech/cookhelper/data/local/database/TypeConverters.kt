package ru.tech.cookhelper.data.local.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.Types
import ru.tech.cookhelper.data.utils.JsonParser
import ru.tech.cookhelper.domain.model.FileData
import java.lang.reflect.Type
import javax.inject.Inject

@ProvidedTypeConverter
class TypeConverters @Inject constructor(private val jsonParser: JsonParser) {

    private fun getListType(type: Type): Type {
        return Types.newParameterizedType(
            List::class.java, type
        )
    }

    @TypeConverter
    fun fromStringList(data: List<String>): String {
        return jsonParser.toJson(
            data, getListType(String::class.java)
        ) ?: ""
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        return jsonParser.fromJson<List<String>>(
            data, getListType(String::class.java)
        ) ?: emptyList()
    }

    @TypeConverter
    fun fromFileDataList(data: List<FileData>): String {
        return jsonParser.toJson(
            data, getListType(FileData::class.java)
        ) ?: ""
    }

    @TypeConverter
    fun toFileDataList(data: String): List<FileData> {
        return jsonParser.fromJson<List<FileData>>(
            data, getListType(FileData::class.java)
        ) ?: emptyList()
    }
}
