package di

import DATA_STORE_FILE_PATH
import createDataStore
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import dependencies.DbClient
import io.ktor.client.engine.okhttp.OkHttp

actual val platformModule: Module = module {
   singleOf(::DbClient)
   single { OkHttp.create() }
   single { createDataStore { DATA_STORE_FILE_PATH } }
}