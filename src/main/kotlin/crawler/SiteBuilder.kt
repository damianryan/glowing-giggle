package crawler

/**
 * SiteBuilder takes a map between URLs and the links from those URLs and builds a generic tree structure of Pages.
 */
class SiteBuilder(val map: Map<String, Collection<String>>, val rootURL: String) {

    fun build(): Page {
        val pageMap = mutableMapOf<String, Page?>()
        for (entry in map.entries) {
            val url = entry.key
            val page = pageMap.getOrPut(url) { Page(url) }!!
            for (child in entry.value) {
                val childPage = pageMap.getOrPut(child) { Page(child) }!!
                page.addChild(childPage)
            }
        }
        return pageMap.get(rootURL)!!
    }
}