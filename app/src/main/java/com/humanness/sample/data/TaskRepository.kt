package com.humanness.sample.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object TaskRepository {

    private const val PREFS_NAME = "tasks_prefs"
    private const val KEY_TASKS = "tasks_json"

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    fun getTasks(context: Context): List<Task> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_TASKS, null) ?: return emptyList()
        return try {
            json.decodeFromString(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addTask(context: Context, task: Task) {
        val current = getTasks(context).toMutableList()
        current.add(task)
        val encoded = json.encodeToString(current)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TASKS, encoded).apply()
    }
}