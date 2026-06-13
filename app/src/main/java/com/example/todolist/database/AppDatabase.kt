package com.example.todolist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todolist.model.Category
import com.example.todolist.model.Task
import com.example.todolist.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Task::class, Category::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.taskDao(), database.categoryDao())
                }
            }
        }

        suspend fun populateDatabase(taskDao: TaskDao, categoryDao: CategoryDao) {
            // Seed Categories
            val work = Category(name = "Work", color = "#4285F4")
            val personal = Category(name = "Personal", color = "#EA4335")
            val shopping = Category(name = "Shopping", color = "#FBBC05")
            val study = Category(name = "Study", color = "#34A853")

            categoryDao.insertCategory(work)
            categoryDao.insertCategory(personal)
            categoryDao.insertCategory(shopping)
            categoryDao.insertCategory(study)

            // Re-fetch categories to get IDs (auto-generated)
            // Or just use the fact that they are 1, 2, 3, 4 if we reset everything
            
            // Seed Tasks with category IDs
            val tasks = listOf(
                Task(title = "Meeting with Client", description = "Discuss project requirements", deadline = "20 June 2026", priority = "High", categoryId = 1),
                Task(title = "Buy Groceries", description = "Eggs, Milk, Bread, Fruits", deadline = "06 June 2026", priority = "Medium", categoryId = 3),
                Task(title = "Finish Homework", description = "Complete math exercises", deadline = "07 June 2026", priority = "High", categoryId = 4),
                Task(title = "Go for a Run", description = "5km in the park", deadline = "05 June 2026", priority = "Low", categoryId = 2, isDone = true),
                Task(title = "Read a Book", description = "Read 20 pages of 'Atomic Habits'", deadline = "08 June 2026", priority = "Medium", categoryId = 2),
                Task(title = "Fix Bug #123", description = "Fix login crash issue", deadline = "10 June 2026", priority = "High", categoryId = 1),
                Task(title = "Clean the House", description = "Vacuum and dust all rooms", deadline = "12 June 2026", priority = "Low", categoryId = 2),
                Task(title = "Update Portfolio", description = "Add new projects to website", deadline = "15 June 2026", priority = "Medium", categoryId = 1)
            )
            tasks.forEach { taskDao.insertTask(it) }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_db"
                )
                .addCallback(AppDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
