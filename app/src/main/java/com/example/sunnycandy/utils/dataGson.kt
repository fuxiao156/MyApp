package com.example.sunnycandy.utils
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
object dataGson: TypeAdapter<Date>(){
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 你的日期格式

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Date?) {
        if (value != null) {
            out.value(dateFormat.format(value))
        } else {
            out.nullValue()
        }
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Date? {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        val dateStr = `in`.nextString()
        return try {
            dateFormat.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }
}