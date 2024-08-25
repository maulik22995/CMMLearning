package dependencies

import network.InsultCensorClient
import utils.NetworkError
import utils.Result

interface MyRepository {
    fun helloWorld(): String
    suspend fun getCenceredText(unCenceredText: String): Result<String, NetworkError>
}

class MyRepositoryImpl(
    private val dbClient: DbClient,
    private val client: InsultCensorClient
) : MyRepository {
    override fun helloWorld(): String {
        return "Hello World"
    }

    override suspend fun getCenceredText(unCenceredText: String): Result<String, NetworkError> {
        return client.censorWords(uncensoredWord = unCenceredText)
    }
}