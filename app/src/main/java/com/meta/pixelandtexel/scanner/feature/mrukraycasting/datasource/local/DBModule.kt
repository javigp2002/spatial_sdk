import androidx.room.Room.databaseBuilder
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.datasource.local.MrukDatabase
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.datasource.local.MrukLocalDatasource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single {
        databaseBuilder(
            androidContext(),
            MrukDatabase::class.java,
            "mruk_db"
        ).build()
    }

    single { get<MrukDatabase>().mrukDao() }
    single { MrukLocalDatasource(get()) }
}