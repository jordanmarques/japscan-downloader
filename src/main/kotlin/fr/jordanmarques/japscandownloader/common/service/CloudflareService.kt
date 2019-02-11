package fr.jordanmarques.japscandownloader.common.service

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.Page
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.util.Cookie
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service
import java.io.InputStream
import java.net.URL
import java.util.concurrent.TimeUnit

@Service
class CloudflareService {

    private var webClient: WebClient = initClient()

    fun fetchAsInputStream(url: String): InputStream? = fetchPage<HtmlPage>(url).webResponse.contentAsStream

    fun fetchAsDocument(url: String): Document? = Jsoup.parse(fetchAsInputStream(url), Charsets.UTF_8.name(), url)

    fun fetchImageAsInputStream(url: String): InputStream? = fetchPage<Page>(url).webResponse.contentAsStream

    private fun <T> fetchPage(url: String): T where T: Page{
        val client = webClient

        var page = client.getPage<T>(url)

        if (page.webResponse.contentAsString.contains("Checking your browser")) {
            client.getPage<T>(url)
            TimeUnit.SECONDS.sleep(6)
            page = client.getPage(url)
            updateWebClientCookies(page)
        }

        return page
    }

    fun updateWebClientCookies(page: Page) {
        val cookies = webClient.cookieManager.cookies

        cookies.forEach {
            webClient.addRequestHeader(it.name, it.value)
        }
    }

    private fun initClient(): WebClient{
        val client = WebClient(BrowserVersion.CHROME)

        with(client) {
            options.isCssEnabled = false
            options.isJavaScriptEnabled = true
            options.isThrowExceptionOnFailingStatusCode = false
            options.isThrowExceptionOnScriptError = false
            options.isRedirectEnabled = true
            cache.maxSize = 0
            javaScriptTimeout = 10000
            waitForBackgroundJavaScript(10000)
            waitForBackgroundJavaScriptStartingBefore(10000)
        }

        return client
    }

    fun fetch(url: String, cookies: Set<Cookie>): InputStream? {
        val connection = URL(url).openConnection()
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")

        connection.setRequestProperty("Cookie", cookies.joinToString(separator = ";") {"${it.name}=${it.value}"})
        connection.connect()

        return try {
            connection.getInputStream()
        } catch (e: Exception){
            null
        }
    }
}


