package leon.longnote.ui

import android.app.Application
import leon.longnote.data.DataRepository
import leon.longnote.data.LocalDataSource
import leon.longnote.db.NoteDb
import leon.longnote.utils.AppExcutors
import leon.longnote.utils.InitialDatabase

/**
 * Created by liyl9 on 2018/3/19.
 */
class BasicApplication: Application() {
    private lateinit  var mAppExecutors: AppExcutors
    override fun onCreate() {
        super.onCreate()
        mAppExecutors = AppExcutors()
    }

    val db by lazy {
        NoteDb.create(this)
    }

    fun getRepository(): DataRepository {
        return DataRepository.getInstance(LocalDataSource(db,mAppExecutors))
    }
    fun getAppExecutors():AppExcutors{
        return mAppExecutors
    }
}
