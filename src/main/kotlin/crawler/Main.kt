package crawler

import java.util.concurrent.ForkJoinPool

fun main(args: Array<String>) {
    if (args.size < 1) {
        System.err.println("Usage: java -jar glowing-giggle-1.0-SNAPSHOT.jar <URL> [maxThreads] [maxDepth]")
        System.exit(1)
    }
    val url = args[0]
    val maxThreads = if (args.size > 1) args[1].toInt() else ForkJoinPool.commonPool().parallelism
    val maxDepth = if (args.size > 2) args[2].toInt() else -1 // -1 = unlimited
    val crawler = Crawler(JSoupLinkLoader(), maxThreads)
    crawler.crawl(url, maxDepth)
    println("(acyclic) site map:")
    SiteBuilder(crawler.siteMap, url).build().print()
}