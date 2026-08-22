package com.example.myapplication.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 统一本地存储：SharedPreferences + JSON。
 * 负责持久化：已开启状态、今日抽卡、成就、漂流瓶、彩蛋记录。
 */
object Store {
    private const val PREFS = "summer_start"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun todayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    // ── 已开启暑假 ──
    fun summerStarted(context: Context): Boolean =
        prefs(context).getBoolean("started", false)

    fun setSummerStarted(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean("started", value).apply()
    }

    // ── 今日抽卡（每天固定一张） ──
    fun todayCardIndex(context: Context): Int? {
        val p = prefs(context)
        val date = p.getString("card_date", null) ?: return null
        if (date != todayKey()) return null
        val index = p.getInt("card_index", -1)
        return if (index in 0 until CARDS.size) index else null
    }

    fun setTodayCard(context: Context, index: Int) {
        prefs(context).edit()
            .putString("card_date", todayKey())
            .putInt("card_index", index)
            .apply()
    }

    /** 仅供调试使用：重置今日抽卡。 */
    fun resetTodayCard(context: Context) {
        prefs(context).edit().remove("card_date").remove("card_index").apply()
    }

    // ── 成就 ──
    fun unlockedAchievements(context: Context): Set<String> =
        prefs(context).getStringSet("achievements", emptySet()) ?: emptySet()

    /** 解锁成就；返回是否为新解锁。 */
    fun unlock(context: Context, id: String): Boolean {
        val set = unlockedAchievements(context).toMutableSet()
        if (!set.add(id)) return false
        prefs(context).edit().putStringSet("achievements", HashSet(set)).apply()
        return true
    }

    // ── 彩蛋记录 ──
    fun eggSeen(context: Context, id: String): Boolean =
        prefs(context).getBoolean("egg_$id", false)

    fun markEgg(context: Context, id: String) {
        prefs(context).edit().putBoolean("egg_$id", true).apply()
    }

    // ── 愿望漂流瓶 ──
    fun wishes(context: Context): List<Wish> {
        val raw = prefs(context).getString("wishes", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Wish(
                    id = o.getLong("id"),
                    text = o.getString("text"),
                    done = o.getBoolean("done"),
                    createdAt = o.getLong("createdAt"),
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveWishes(context: Context, list: List<Wish>) {
        val arr = JSONArray()
        list.forEach { w ->
            arr.put(
                JSONObject()
                    .put("id", w.id)
                    .put("text", w.text)
                    .put("done", w.done)
                    .put("createdAt", w.createdAt)
            )
        }
        prefs(context).edit().putString("wishes", arr.toString()).apply()
    }

    fun addWish(context: Context, text: String): List<Wish> {
        val list = wishes(context) + Wish(
            id = System.currentTimeMillis() + (0..999).random(),
            text = text.trim(),
            done = false,
            createdAt = System.currentTimeMillis(),
        )
        saveWishes(context, list)
        return list
    }

    fun toggleWish(context: Context, id: Long): List<Wish> {
        val list = wishes(context).map { if (it.id == id) it.copy(done = !it.done) else it }
        saveWishes(context, list)
        return list
    }

    fun deleteWish(context: Context, id: Long): List<Wish> {
        val list = wishes(context).filterNot { it.id == id }
        saveWishes(context, list)
        return list
    }
}
