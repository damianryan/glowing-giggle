package crawler

/**
 * Provides the URLs of links contained within a document at a specified URL.
 */
interface LinkLoader {

    fun links(url: String): Collection<String>?
}