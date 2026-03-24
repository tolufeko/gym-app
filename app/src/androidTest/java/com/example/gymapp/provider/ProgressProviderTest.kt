package com.example.gymapp.provider

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.gymapp.data.*
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.InputStreamReader

@RunWith(AndroidJUnit4::class)
class ProgressProviderTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var workoutDao: WorkoutDao
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var progressDao: ProgressDao

    private val testUserId = "testuser123"

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        val testDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        AppDatabase.overrideForTesting(testDb)
        db = testDb


        workoutDao = db.workoutDao()
        exerciseDao = db.exerciseDao()
        progressDao = db.progressDao()

        populateDatabase()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun populateDatabase() = runBlocking {
        val sessionId = workoutDao.insertSession(
            WorkoutSession(name = "Chest Day", notes = "Focus on upper chest")
        )

        exerciseDao.insert(
            Exercise(
                workoutSessionId = sessionId,
                name = "Incline Bench Press",
                sets = 3,
                reps = 10,
                weight = 80f
            )
        )

        progressDao.insertProgress(
            Progress(userId = testUserId, totalWorkouts = 1, timestamp = System.currentTimeMillis())
        )
    }

    @Test
    fun testProgressProvider_returnsExpectedContent() {
        val uri: Uri = Uri.parse("content://com.example.gymapp.provider/progress/$testUserId")
        val contentResolver: ContentResolver = InstrumentationRegistry.getInstrumentation().targetContext.contentResolver

        contentResolver.openInputStream(uri).use { inputStream ->
            assertNotNull("Input stream should not be null", inputStream)

            val content = BufferedReader(InputStreamReader(inputStream)).readText()
            assertTrue("Should contain session title", content.contains("Chest Day"))
            assertTrue("Should contain exercise", content.contains("Incline Bench Press"))
            assertTrue("Should contain set and rep", content.contains("3x10"))
        }
    }
}
