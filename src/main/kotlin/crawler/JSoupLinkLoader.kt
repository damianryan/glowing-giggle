package crawler

import com.google.common.collect.Sets
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.net.URL

class JSoupLinkLoader : LinkLoader {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(JSoupLinkLoader::class.java)
    }

    override fun links(url: String): Collection<String>? {
        val domain = URL(url).host
        val response  = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).timeout(10000).execute()
        val type = response.contentType()
        if (type.startsWith("text") || type.contains("html")) {
            val document = response.parse()
            val links = document.select("a[href]")
            val filtered = links.map { it.attr("abs:href") } // make hrefs absolute
                                .filter { it.startsWith("http") } // exclude mailto, other non-http(s) links
                                .filter { domain == URL(it).host } // exclude external links (even subdomains)
                                .map { it.substringBefore("#") } // ignore anchor portions of links
                                .filter { !image(it) } // ignore PNG, JPEG and GIF images
                                .map { if (it.endsWith("/")) it else it + "/"} // ensure links end in "/"
            val unique = Sets.newHashSet<String>(filtered)
            LOGGER.debug("found {} unique links in {}: {}", unique.size, url, unique)
            return unique
        }
        LOGGER.debug("non-text URL: {}", url)
        return null
    }

    private fun image(url: String): Boolean {
        val norm = url.toLowerCase()
        when {
            norm.endsWith(".jpg") || norm.endsWith(".jpeg") || norm.endsWith(".png") || norm.endsWith(".gif") -> return true
            else -> return false
        }
    }
}