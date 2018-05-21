package crawler

import com.google.common.collect.Maps

internal class CannedLinkLoader: LinkLoader {

    private val links = createLinks()

    private fun createLinks(): Map<String, Collection<String>> {
        val map = Maps.newHashMap<String, List<String>>()
        map.put("http://domain.com/", listOf("http://domain.com/childless-child", "http://domain.com/child-with-child"))
        map.put("http://domain.com/child-with-child", listOf("http://domain.com/grandchild-with-child"))
        map.put("http://domain.com/grandchild-with-child", listOf("http://domain.com/childless-great-grandchild"))
        return map
    }

    override fun links(url: String): Collection<String>? {
        return links[url]
    }
}