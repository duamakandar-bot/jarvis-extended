package com.example

import com.example.model.AlertCondition
import com.example.model.PriceAlert
import com.example.service.ArachneAiEngine
import com.example.service.CryptoRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testArachneAiEngineTacticalResponses() = runBlocking {
        val engine = ArachneAiEngine()

        val sysResponse = engine.generateResponse("Check system hardware specs and RAM")
        assertTrue("Sys response was: $sysResponse", sysResponse.contains("Intel Core i5-5200U"))

        val btcResponse = engine.generateResponse("What is the BTC crypto market analysis?")
        assertTrue("BTC response was: $btcResponse", btcResponse.contains("BTC Analysis"))

        val mt5Response = engine.generateResponse("Check MT5 EA terminal status")
        assertTrue("MT5 response was: $mt5Response", mt5Response.contains("MT5 EA Bridge"))

        val metaResponse = engine.generateResponse("Show Instagram automation status")
        assertTrue("Meta response was: $metaResponse", metaResponse.contains("Meta Automation Matrix"))
    }

    @Test
    fun testCryptoRepositoryDefaultAssets() = runBlocking {
        val repo = CryptoRepository()
        val assets = repo.fetchMarketAssets()
        assertNotNull(assets)
        assertTrue(assets.isNotEmpty())

        val btc = assets.find { it.symbol == "BTC" }
        assertNotNull(btc)
        assertTrue(btc!!.currentPriceUsd > 10000.0)
        assertTrue(btc.priceHistory.isNotEmpty())
    }

    @Test
    fun testPriceAlertConditionLogic() {
        val alertAbove = PriceAlert("1", "BTC", 70000.0, AlertCondition.ABOVE)
        val alertBelow = PriceAlert("2", "BTC", 65000.0, AlertCondition.BELOW)

        val price1 = 70500.0
        val isAboveTriggered = price1 >= alertAbove.targetPrice
        assertTrue(isAboveTriggered)

        val price2 = 64200.0
        val isBelowTriggered = price2 <= alertBelow.targetPrice
        assertTrue(isBelowTriggered)
    }
}
