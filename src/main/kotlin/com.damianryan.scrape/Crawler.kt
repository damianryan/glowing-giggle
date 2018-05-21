package com.damianryan.scrape

import com.google.common.base.Stopwatch
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit

class Crawler(private val linkLoader: LinkLoader, private val maxThreads: Int) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Crawler::class.java)
    }

    private val visited = Collections.synchronizedMap(Maps.newLinkedHashMap<String, Int>())
    private val mySiteMap = Collections.synchronizedMap(Maps.newLinkedHashMap<String, MutableCollection<String>>())
    private val myPool = ForkJoinPool(maxThreads)

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
        myPool.invoke(Indexer(this, linkLoader, url, 0, maxDepth))
        stopWatch.stop()
        LOGGER.info("sitemap crawl took {}s and produced:", stopWatch.elapsed(TimeUnit.SECONDS))
        siteMap.keys.sorted().forEach { LOGGER.info("{} -> {}: {}", it, siteMap[it]?.size, siteMap[it])}
    }
}