package crawler

import com.google.common.collect.Lists
import org.slf4j.LoggerFactory
import java.util.concurrent.RecursiveAction

/**
 * Link indexer. Uses a fork join pool to decompose the crawling of links on a page to multiple worker threads. Such
 * decomposition can occur up to a maximum of maxDepth times if maxDepth is a positive integer, or until all pages on
 * a site have been crawled to their leaf nodes if maxDepth is -1.
 */
class Indexer(val crawler: Crawler, val linkLoader: LinkLoader, val url: String, val depth: Int, val maxDepth: Int) : RecursiveAction() {

    companion object {
        val LOGGER = LoggerFactory.getLogger(Indexer::class.java)
    }

    // this function is invoked in a worker thread by the fork join pool
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

                            // create a sub-task to crawl the child link
                            actions.add(Indexer(crawler, linkLoader, link, depth + 1, maxDepth))
                        } else {
                            LOGGER.debug("already visited link {}", link)
                        }
                    }
                }
                LOGGER.debug("parent: {}, depth: {}, # of child: {}, total visits (all URLs): {}", url, depth, links?.size, crawler.size)
                invokeAll(actions) // tells the fork join pool to invoke sub-tasks to crawl all child links
            } else {
                LOGGER.debug("depth {} of {} exceeds maxDepth {}, not loading links", depth, url, maxDepth)
            }
        } else {
            LOGGER.debug("already visited {}", url)
        }
    }

    private fun depthLessThanMaxDepth() = if (maxDepth > 0) depth < maxDepth else true
}