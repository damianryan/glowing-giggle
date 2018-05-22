package crawler

/**
 * Page represents a page in a website. It may have zero or more child pages (that is, pages it links to). This class
 * exists pretty much to enable printing of the site map as an acyclic graph where intermediate and leaf nodes may
 * appear multiple times if they are linked from multiple locations. No attempt is made to make nodes appear only
 * once in the graph, as this would make the tree cyclic and/or interlinked and much more difficult to render on
 * a console.
 */
data class Page(val url: String, val child: MutableList<Page> = mutableListOf()) {

    fun addChild(newChild: Page) {
        child.add(newChild)
    }

    fun print() {
        print(1)
    }

    private fun print(level: Int) {
        for (i in 1..level) {
            print("  ")
        }
        println(url)
        for (page in child) {
            page.print(level + 1);
        }
    }
}