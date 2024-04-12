package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class AppDataRepo {
    companion object {
        private const val DATA_FILE_NAME = "spotify_client_data.json"
        private val charset = Charsets.UTF_8
    }

    suspend fun init() {
        withContext(Dispatchers.IO) {
            val file = File(DATA_FILE_NAME)
            if (!file.exists()) {
                file.createNewFile()
                FileWriter(DATA_FILE_NAME, charset).use {
                    it.write(JSONObject().toString())
                }
            }
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            FileWriter(DATA_FILE_NAME, charset).use {
                it.write("{}")
            }
        }
    }

    suspend fun save(data: JSONObject) {
        withContext(Dispatchers.IO) {
            FileWriter(DATA_FILE_NAME, charset).use {
                it.write(data.toString())
            }
        }
    }

    suspend fun getData(): JSONObject {
        return withContext(Dispatchers.IO) {
            val result: String
            FileReader(DATA_FILE_NAME, charset).use {
                result = it.readText()
            }
            return@withContext JSONObject(result)
        }
    }
}
