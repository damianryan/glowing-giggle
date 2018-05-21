package crawler

import com.google.common.base.Stopwatch
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit

/**
 * Multi-threaded link crawler. Uses a fork join pool to decompose the job of crawling a website into smaller tasks
 * that are farmed out to multiple worker threads. Idle workers can steal work from busier workers in order to achieve
 * higher throughput. This class is responsible for storing the state (visited links, which links are children of
 * which URLs) in a thread-safe manner.
 */
class Crawler(private val linkLoader: LinkLoader, private val maxThreads: Int) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Crawler::class.java)
    }

    private val visited = Collections.synchronizedMap(Maps.newLinkedHashMap<String, Int>())
    private val mySiteMap = Collections.synchronizedMap(Maps.newLinkedHashMap<String, MutableCollection<String>>())
    private val pool = ForkJoinPool(maxThreads)

    val size
        get() = visited.size

    val siteMap: Map<String, Collection<String>>
        get() = mySiteMap

    fun addVisited(url: String, depth: Int) {
        visited[url] = depth
        LOGGER.debug("visited: {} at depth: {}", url, depth)
    }

    fun visited(url: String) = visited.containsKey(url)

    fun addToSiteMap(url: String) {
        mySiteMap[url] = Collections.synchronizedSet(Sets.newHashSet<String>())
    }

    fun addChild(parent: String, child: String) {
        mySiteMap.get(parent)?.add(child)
        LOGGER.debug("added {} to {}, which now has {} children: {}", child, parent, mySiteMap.get(parent)?.size, mySiteMap.get(parent))
    }

    fun crawl(url: String, maxDepth: Int) {
        LOGGER.info("crawling: {} using {} threads to a maximum link depth of {}", url, maxThreads, maxDepth)
        val stopWatch = Stopwatch.createStarted()

        // next line creates a top-level task to crawl a website from the given URL
        pool.invoke(Indexer(this, linkLoader, url, 0, maxDepth))
        stopWatch.stop()
        LOGGER.info("sitemap crawl took {}s and produced:", stopWatch.elapsed(TimeUnit.SECONDS))
        siteMap.keys.sorted().forEach { LOGGER.info("{} -> {}: {}", it, siteMap[it]?.size, siteMap[it])}
    }
}