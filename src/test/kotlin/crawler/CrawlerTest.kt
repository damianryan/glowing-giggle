package crawler

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/*
    http://domain.com/ -> 2: [http://domain.com/childless-child, http://domain.com/child-with-child]
    http://domain.com/child-with-child -> 1: [http://domain.com/grandchild-with-child]
    http://domain.com/childless-child -> 0: []
    http://domain.com/childless-great-grandchild -> 0: []
    http://domain.com/grandchild-with-child -> 1: [http://domain.com/childless-great-grandchild]

    or

    <root>
       |
       +--childless-child
       |
       +--child-with-child
            |
            +--grandchild-with-child
                 |
                 +--childless-great-grandchild
 */
internal class CrawlerTest {

    @Test
    fun unlimitedDepth() {
        val crawler = Crawler(CannedLinkLoader(), 2)
        crawler.crawl("http://domain.com/", -1)
        val siteMap = crawler.siteMap
        assertEquals(5, siteMap.size)
        assertEquals(2, siteMap["http://domain.com/"]?.size)
        assertEquals(0, siteMap["http://domain.com/childless-child"]?.size)
        assertEquals(1, siteMap["http://domain.com/child-with-child"]?.size)
        assertEquals(1, siteMap["http://domain.com/grandchild-with-child"]?.size)
        assertEquals(0, siteMap["http://domain.com/childless-great-grandchild"]?.size)
    }

    @Test
    fun depthLimitedTo2() {
        val crawler = Crawler(CannedLinkLoader(), 2)
        crawler.crawl("http://domain.com/", 2)
        val siteMap = crawler.siteMap
        assertEquals(4, siteMap.size)
        assertEquals(2, siteMap["http://domain.com/"]?.size)
        assertEquals(0, siteMap["http://domain.com/childless-child"]?.size)
        assertEquals(1, siteMap["http://domain.com/child-with-child"]?.size)
        assertEquals(0, siteMap["http://domain.com/grandchild-with-child"]?.size)
    }

    @Test
    fun depthLimitedTo1() {
        val crawler = Crawler(CannedLinkLoader(), 2)
        crawler.crawl("http://domain.com/", 1)
        val siteMap = crawler.siteMap
        assertEquals(3, siteMap.size)
        assertEquals(2, siteMap["http://domain.com/"]?.size)
        assertEquals(0, siteMap["http://domain.com/childless-child"]?.size)
        assertEquals(0, siteMap["http://domain.com/child-with-child"]?.size)
    }
}