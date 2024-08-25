package di

import dependencies.DbClient
import dependencies.MyViewModel
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::DbClient)
    viewModelOf(::MyViewModel)
    single { OkHttp.create() }
}