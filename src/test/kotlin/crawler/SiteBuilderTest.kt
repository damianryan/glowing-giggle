package crawler

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

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
internal class SiteBuilderTest {

    @Test
    fun buildsExpectedPages() {
        val root = SiteBuilder(CannedLinkLoader().links, "http://domain.com/").build()
        assertEquals("http://domain.com/", root.url)
        assertEquals(2, root.child.size)
        assertEquals("http://domain.com/childless-child/", root.child[0].url)
        assertEquals(0, root.child[0].child.size)
        assertEquals("http://domain.com/child-with-child/", root.child[1].url)
        assertEquals(1, root.child[1].child.size)
        assertEquals("http://domain.com/grandchild-with-child/", root.child[1].child[0].url)
        assertEquals(1, root.child[1].child[0].child.size)
        assertEquals("http://domain.com/childless-great-grandchild/", root.child[1].child[0].child[0].url)
        assertEquals(0, root.child[1].child[0].child[0].child.size)
    }
}