package com.budilov.aws.comprehend

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder
import com.amazonaws.services.comprehend.model.*
import com.budilov.aws.Properties
import com.google.common.base.Splitter

data class ComprehendResult(val keyPhrases: List<PhrasesResult>, val sentiment: SentimentResult, val entities: List<TokensResult>)

data class SentimentResult(val sentiment: String?, val positiveScore: Number?, val mixedScore: Number?, val neutralScore: Number?, val negativeScore: Number?)
data class PhrasesResult(val text: String,
                         val score: Number,
                         val beginOffset: Number,
                         val endOffset: Number)

data class TokensResult(val text: String,
                        val type: String,
                        val score: Number,
                        val beginOffset: Number,
                        val endOffset: Number)

/**
 * @author Vladimir Budilov
 */
object ComprehendService {
    val comprehendClient = AmazonComprehendClientBuilder.standard()
            .withCredentials(DefaultAWSCredentialsProviderChain())
            .withRegion(Properties.regionName)
            .build()

    fun getSentiment(input: String): SentimentResult {
        if (input.toByteArray().size > 5000)
            println("Length of string is greater than 5000 -- shorten it... length: ${input.length}")

        // Call detectSentiment API
        val detectSentimentRequest = DetectSentimentRequest().withText(input)
                .withLanguageCode("en")
        val detectSentimentResult: DetectSentimentResult? = try {
            comprehendClient.detectSentiment(detectSentimentRequest)
        } catch (e: Exception) {
            println("Exception caught: ${e.message}")
            null
        }

        return SentimentResult(sentiment = detectSentimentResult?.sentiment,
                positiveScore = detectSentimentResult?.sentimentScore?.positive,
                mixedScore = detectSentimentResult?.sentimentScore?.mixed,
                negativeScore = detectSentimentResult?.sentimentScore?.negative,
                neutralScore = detectSentimentResult?.sentimentScore?.neutral)
    }

    fun getKeyPhrases(input: String): List<PhrasesResult> {

        val phrasesList = mutableListOf<PhrasesResult>()

        Splitter
                .fixedLength(4500)
                .split(input).forEach {

            val detectKeyPhrasesRequest = DetectKeyPhrasesRequest().withText(it)
                    .withLanguageCode("en")

            val detectKeyPhrasesResult = try {
                comprehendClient.detectKeyPhrases(detectKeyPhrasesRequest)
            } catch (e: Exception) {
                null
            }

            detectKeyPhrasesResult?.keyPhrases?.forEach {
                phrasesList.add(PhrasesResult(it.text, it.score, it.beginOffset, it.endOffset))
            }

        }

        return phrasesList

    }

    fun getTokens(input: String): List<TokensResult> {
        val tokenList = mutableListOf<TokensResult>()

        Splitter
                .fixedLength(4500)
                .split(input).forEach {
            val detectEntitiesRequest = DetectEntitiesRequest().withText(it)
                    .withLanguageCode("en")
            val detectEntitiesResult: DetectEntitiesResult? = try {
                comprehendClient.detectEntities(detectEntitiesRequest)
            } catch (e: Exception) {
                println("Exception caught: ${e.message}")
                null
            }

            detectEntitiesResult?.entities?.forEach {
                tokenList.add(TokensResult(it.text, it.type, it.score, it.beginOffset, it.endOffset))
            }
        }

        return tokenList
    }

    fun getAll(input: String): ComprehendResult {

        return ComprehendResult(keyPhrases = getKeyPhrases(input), sentiment = getSentiment(input), entities = getTokens(input))
    }
}
