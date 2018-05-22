package crawler

import com.google.common.collect.Maps

/*
    http://domain.com/ -> 2: [http://domain.com/childless-child/, http://domain.com/child-with-child/]
    http://domain.com/child-with-child/ -> 1: [http://domain.com/grandchild-with-child/]
    http://domain.com/childless-child/ -> 0: []
    http://domain.com/childless-great-grandchild/ -> 0: []
    http://domain.com/grandchild-with-child/ -> 1: [http://domain.com/childless-great-grandchild/]

    or

    <root>
       |
       +--childless-child/
       |
       +--child-with-child/
            |
            +--grandchild-with-child/
                 |
                 +--childless-great-grandchild/
 */
internal class CannedLinkLoader: LinkLoader {

    val links = createLinks()

    private fun createLinks(): Map<String, Collection<String>> {
        val map = Maps.newLinkedHashMap<String, List<String>>()
        map.put("http://domain.com/", listOf("http://domain.com/childless-child/", "http://domain.com/child-with-child/"))
        map.put("http://domain.com/child-with-child/", listOf("http://domain.com/grandchild-with-child/"))
        map.put("http://domain.com/grandchild-with-child/", listOf("http://domain.com/childless-great-grandchild/"))
        return map
    }

    override fun links(url: String): Collection<String>? {
        return links[url]
    }
}