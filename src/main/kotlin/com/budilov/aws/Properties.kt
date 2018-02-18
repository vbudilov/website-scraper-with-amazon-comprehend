package com.budilov.aws

/**
 * @author Vladimir Budilov
 *
 */
object Properties {

    val regionName: String = try { System.getenv("REGION_NAME") } catch (e: Exception) {"us-east-1"}
    val elasticSearchEndpoint: String? = try {System.getenv("ELASTIC_SEARCH_ENDPOINT") } catch (e: Exception) {null}
}
