package crawler

fun main(args: Array<String>) {
    if (args.size < 1) {
        System.err.println("Usage: java -jar scraper.jar <URL> [maxThreads] [maxDepth]")
        System.exit(1)
    }
    val url = args[0]
    val maxThreads = if (args.size > 1) args[1].toInt() else 3
    val maxDepth = if (args.size > 2) args[2].toInt() else -1
    Crawler(JSoapLinkLoader(), maxThreads).crawl(url, maxDepth)
}