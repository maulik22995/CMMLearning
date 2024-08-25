package di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import dependencies.DbClient
import dependencies.MyViewModel
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.single

actual val platformModule: Module = module {
    singleOf(::DbClient)
    viewModelOf(::MyViewModel)
    single { Darwin.create() }
}
