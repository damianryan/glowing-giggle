package com.damianryan.scrape

import com.google.common.collect.Lists
import org.slf4j.LoggerFactory
import java.util.concurrent.RecursiveAction

class Indexer(val crawler: Crawler, val linkLoader: LinkLoader, val url: String, val depth: Int, val maxDepth: Int) : RecursiveAction() {

    companion object {
        val LOGGER = LoggerFactory.getLogger(Indexer::class.java)
    }

    override fun compute() {
        if (!crawler.visited(url)) {
            LOGGER.debug("unvisited URL: {}", url)
            val actions = Lists.newArrayList<RecursiveAction>()
            crawler.addVisited(url, depth)
            crawler.addToSiteMap(url)
            if (depthLessThanMaxDepth()) {
                val links = linkLoader.links(url)
                if (null != links) {
                    for (link in links) {
                        if (!crawler.visited(link)) {
                            LOGGER.debug("unvisited link from {}: {}", url, link)
                            LOGGER.debug("adding depth {} child {} to {}", depth, link, url)
                            crawler.addChild(url, link)
                            actions.add(Indexer(crawler, linkLoader, link, depth + 1, maxDepth))
                        } else {
                            LOGGER.debug("already visited link {}", link)
                        }
                    }
                }
                LOGGER.debug("parent: {}, depth: {}, # of children: {}, total visits (all URLs): {}", url, depth, links?.size, crawler.size)
                invokeAll(actions)
            } else {
                LOGGER.debug("depth {} of {} exceeds maxDepth {}, not loading links", depth, url, maxDepth)
            }
        } else {
            LOGGER.debug("already visited {}", url)
        }
    }

    fun depthLessThanMaxDepth() = if (maxDepth > 0) depth < maxDepth else true
}