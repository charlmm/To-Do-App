package com.charles.mvvmto_doapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.charles.mvvmto_doapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    class CallBack @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val dao = database.get().taskDao()
            applicationScope.launch {
                dao.insertTask(Task("wash the dishes"))
                dao.insertTask(Task("wash the cloths", important = true))
                dao.insertTask(Task("wash the dishes"))
                dao.insertTask(Task("cook", important = true))
                dao.insertTask(Task("code"))
                dao.insertTask(Task("Finish project"))
            }
        }
    }
}