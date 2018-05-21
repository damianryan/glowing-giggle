package com.damianryan.scrape

interface LinkLoader {

    fun links(url: String): Collection<String>?
}