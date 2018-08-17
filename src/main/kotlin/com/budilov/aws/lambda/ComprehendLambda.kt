package com.budilov.aws.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.budilov.aws.comprehend.ComprehendService
import com.budilov.cognito.lambda.ApiGatewayRequest
import com.budilov.cognito.lambda.ApiGatewayResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.jsoup.Jsoup

/**
 * @author Vladimir Budilov
 */
class ComprehendLambda : RequestHandler<ApiGatewayRequest.Input,
        ApiGatewayResponse> {


    override fun handleRequest(request: ApiGatewayRequest.Input?,
                               context: Context?): ApiGatewayResponse {

        val urlHeader = request?.headers?.get("url")
        val urls = urlHeader?.split(',')

        var fullText = ""

        for (url in urls.orEmpty()) {

            if (url != null) {
                try {
                    val doc = Jsoup.connect(url).get()
                    fullText += " " + doc.body().text()
                } catch (e: Exception) {
                }

            }
        }

        var status = 400
        var response = ""

        try {
            response = Gson().toJson(ComprehendService.getAll(fullText))
            println("response: $response")
            status = 200
        } catch (e: Exception) {
            status = 400
        }

        return ApiGatewayResponse(statusCode = status, body = response)
    }
}

fun main(args: Array<String>) {
    val url = "http://www.bbc.com"

    val doc = Jsoup.connect(url).get()
    val gson = GsonBuilder().setPrettyPrinting().create()

    println("response: ${gson.toJson(ComprehendService.getAll(doc.body().text()))}")
}
