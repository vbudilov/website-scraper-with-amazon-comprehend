package com.budilov.aws.comprehend

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest
import com.amazonaws.services.comprehend.model.DetectSentimentRequest
import com.rometools.fetcher.FeedFetcher
import com.rometools.fetcher.impl.HttpClientFeedFetcher
import com.rometools.rome.feed.synd.SyndFeed
import java.net.URL


data class FeedItem(val title: String, val description: String)

object Feeder {


    fun getItems(url: String): List<FeedItem> {
        var response = mutableListOf<FeedItem>()
        val url = URL(url)
        val feedFetcher: FeedFetcher = HttpClientFeedFetcher()
        val feed: SyndFeed = feedFetcher.retrieveFeed(url)

        for (item in feed.entries) {
            response.add(FeedItem(item.title, item.description.value))
        }
        return response
    }

    fun getItem(url: String): FeedItem {
        val url = URL(url)
        val feedFetcher: FeedFetcher = HttpClientFeedFetcher()
        val feed: SyndFeed = feedFetcher.retrieveFeed(url)

        return FeedItem(title = feed.title, description = feed.description)
    }
}

fun main(args: Array<String>) {
    val reutersFeed = "http://feeds.reuters.com/Reuters/domesticNews"
    val bbcWorldFeed = "http://feeds.bbci.co.uk/news/rss.xml"
    for (items in Feeder.getItems("https://www.goodnewsnetwork.org/category/good-talks/feed/")) {
        val text = items.description

        val comprehendClient = AmazonComprehendClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain())
                .withRegion("us-east-1")
                .build()

        // Call detectSentiment API
        val detectEntitiesRequest = DetectEntitiesRequest().withText(text)
                .withLanguageCode("en")
        val detectEntitiesResult = comprehendClient.detectEntities(detectEntitiesRequest)
        println("Entities: ${detectEntitiesResult.entities}")
        val detectSentimentRequest = DetectSentimentRequest().withText(text)
                .withLanguageCode("en")
        val detectSentimentResult = comprehendClient.detectSentiment(detectSentimentRequest)
        println("Description: ${text}")
        println(detectSentimentResult)
        println("End of DetectSentiment\n")
        println("Done")
    }
}