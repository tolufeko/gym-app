package com.example.gymapp.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.example.gymapp.data.AppDatabase
import com.example.gymapp.data.Progress
import com.example.gymapp.data.WorkoutWithExercises
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date

class ProgressProvider : ContentProvider() {
    companion object {
        const val AUTHORITY = "com.example.gymapp.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/progress")
        private const val CODE_PROGRESS = 1
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "progress/*", CODE_PROGRESS)
        }
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
        val userId = uri.lastPathSegment ?: throw IllegalArgumentException("Missing user ID")

        val file = File(context!!.cacheDir, "gym_progress_${userId}_${System.currentTimeMillis()}.txt").apply {
            createNewFile()
            deleteOnExit()
        }

        val sessions = runBlocking {
            val db = AppDatabase.getDatabase(context!!)
            FirebaseAuth.getInstance().currentUser?.uid?.let {
                db.workoutDao().getAllSessionsWithExercises(
                    it
                )
                    .first()
            }
        }

        // Write to file with proper typing
        FileWriter(file).use { writer ->
            writer.write(sessions?.let { generateProgressText(it) })
        }

        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    private fun generateProgressText(sessions: List<WorkoutWithExercises>): String {
        return buildString {
            appendLine("🏋️ Gym Progress Report 🏋️")
            appendLine("Generated: ${SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())}")
            appendLine()

            sessions.forEach { session ->
                appendLine("Session: ${session.session.name}")
                appendLine("Date: ${Date(session.session.timestamp)}")
                session.exercises.forEach { exercise ->
                    appendLine("- ${exercise.name}: ${exercise.sets}x${exercise.reps} @ ${exercise.weight}kg")
                }
                appendLine()
            }
        }
    }

    override fun getType(uri: Uri): String? = when (uriMatcher.match(uri)) {
        CODE_PROGRESS -> "text/plain"
        else -> null
    }

    override fun onCreate(): Boolean = true
    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int = 0
}