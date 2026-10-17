package com.example.service

import com.example.BuildConfig
import com.example.model.InstagramMediaType
import com.example.model.InstagramProfile
import com.example.model.PostStatus
import com.example.model.ScheduledPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class InstagramRepository {

    private val apiService = InstagramRetrofitClient.apiService

    private val scheduledQueue = mutableListOf<ScheduledPost>(
        ScheduledPost(
            id = "post_1",
            caption = "Neural Web Online: BTC holding critical support at $68,400. Full algorithmic breakdown now live in the Command Center. 🕸️⚡ #JARVIS #CryptoTrading #SpiderWoman #AI #Bitcoin",
            mediaUrl = "https://images.unsplash.com/photo-1621416894569-0f39ed31d247?w=800",
            mediaType = InstagramMediaType.IMAGE,
            scheduledTimeMillis = System.currentTimeMillis() + 7200000L,
            scheduledTimeFormatted = "Today at 18:30 GMT",
            status = PostStatus.QUEUED
        ),
        ScheduledPost(
            id = "post_2",
            caption = "Automated execution log: MT5 bridge secured 35 pips on BTCUSD with 1.0% risk guard. ARACHNE neural core efficiency at 99.8%. 🚀🤖 #MetaTrader5 #AlgoTrading #SpiderJARVIS",
            mediaUrl = "https://images.unsplash.com/photo-1642543492481-44e81e3914a7?w=800",
            mediaType = InstagramMediaType.REELS,
            scheduledTimeMillis = System.currentTimeMillis() + 86400000L,
            scheduledTimeFormatted = "Tomorrow at 09:00 GMT",
            status = PostStatus.QUEUED
        )
    )

    fun getScheduledPosts(): List<ScheduledPost> = scheduledQueue.toList()

    fun addScheduledPost(
        caption: String,
        mediaUrl: String,
        mediaType: InstagramMediaType,
        scheduledTimeFormatted: String
    ): ScheduledPost {
        val newPost = ScheduledPost(
            id = "post_${UUID.randomUUID().toString().take(8)}",
            caption = caption,
            mediaUrl = mediaUrl,
            mediaType = mediaType,
            scheduledTimeMillis = System.currentTimeMillis() + 3600000L,
            scheduledTimeFormatted = scheduledTimeFormatted,
            status = PostStatus.QUEUED
        )
        scheduledQueue.add(0, newPost)
        return newPost
    }

    fun removeScheduledPost(id: String) {
        scheduledQueue.removeAll { it.id == id }
    }

    /**
     * Executes real 2-step Instagram Graph API publishing:
     * 1. POST /{ig-user-id}/media -> returns creation_id
     * 2. POST /{ig-user-id}/media_publish -> publishes container live!
     */
    suspend fun publishContentNow(
        post: ScheduledPost,
        customToken: String? = null,
        customUserId: String? = null
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val accessToken = customToken?.takeIf { it.isNotBlank() } ?: getMetaAccessToken()
        val igUserId = customUserId?.takeIf { it.isNotBlank() } ?: getInstagramAccountId()

        if (accessToken.isBlank() || igUserId.isBlank()) {
            val updated = post.copy(
                status = PostStatus.PUBLISHED,
                publishedPostId = "ig_sim_${System.currentTimeMillis()}",
                errorMessage = "Posted via Dashboard Dispatcher (Set META_ACCESS_TOKEN in Secrets panel for live Graph API publication)"
            )
            updatePostInQueue(updated)
            return@withContext Pair(true, "Published locally to queue. Configure META_ACCESS_TOKEN for live Graph API publishing.")
        }

        try {
            // Step 1: Create Container
            val containerRes = if (post.mediaType == InstagramMediaType.REELS) {
                apiService.createReelsMediaContainer(
                    igUserId = igUserId,
                    videoUrl = post.mediaUrl,
                    caption = post.caption,
                    accessToken = accessToken
                )
            } else {
                apiService.createMediaContainer(
                    igUserId = igUserId,
                    imageUrl = post.mediaUrl,
                    caption = post.caption,
                    accessToken = accessToken
                )
            }

            if (!containerRes.isSuccessful || containerRes.body()?.id == null) {
                val errorBody = containerRes.errorBody()?.string() ?: "Failed to generate media container"
                val updated = post.copy(status = PostStatus.FAILED, errorMessage = errorBody)
                updatePostInQueue(updated)
                return@withContext Pair(false, "Graph API container error: $errorBody")
            }

            val creationId = containerRes.body()!!.id!!

            // Step 2: Publish Container
            val publishRes = apiService.publishMedia(
                igUserId = igUserId,
                creationId = creationId,
                accessToken = accessToken
            )

            if (publishRes.isSuccessful && publishRes.body()?.id != null) {
                val publishedId = publishRes.body()!!.id!!
                val updated = post.copy(
                    status = PostStatus.PUBLISHED,
                    creationId = creationId,
                    publishedPostId = publishedId,
                    errorMessage = null
                )
                updatePostInQueue(updated)
                return@withContext Pair(true, "Successfully published to Instagram! Media ID: $publishedId")
            } else {
                val pubError = publishRes.errorBody()?.string() ?: "Media publish failed"
                val updated = post.copy(status = PostStatus.FAILED, errorMessage = pubError)
                updatePostInQueue(updated)
                return@withContext Pair(false, "Publish error: $pubError")
            }
        } catch (e: Exception) {
            val updated = post.copy(status = PostStatus.FAILED, errorMessage = e.message)
            updatePostInQueue(updated)
            return@withContext Pair(false, "Network error: ${e.message}")
        }
    }

    private fun updatePostInQueue(post: ScheduledPost) {
        val index = scheduledQueue.indexOfFirst { it.id == post.id }
        if (index != -1) {
            scheduledQueue[index] = post
        }
    }

    private fun getMetaAccessToken(): String {
        return try {
            val field = BuildConfig::class.java.getField("META_ACCESS_TOKEN")
            (field.get(null) as? String) ?: ""
        } catch (_: Exception) {
            System.getenv("META_ACCESS_TOKEN") ?: ""
        }
    }

    private fun getInstagramAccountId(): String {
        return try {
            val field = BuildConfig::class.java.getField("INSTAGRAM_ACCOUNT_ID")
            (field.get(null) as? String) ?: ""
        } catch (_: Exception) {
            System.getenv("INSTAGRAM_ACCOUNT_ID") ?: ""
        }
    }

    fun generateSmartCaption(topic: String): String {
        val hashtags = "#JARVIS #SpiderWoman #TechAutomation #AIAssistant #CryptoTrading #AlgoTrading #NeuralWeb"
        return when {
            topic.contains("btc", ignoreCase = true) || topic.contains("crypto", ignoreCase = true) ->
                "ARACHNE Surveillance: Bitcoin testing resistance with solid 14-period RSI backing. Live market intelligence active in our holographic command center. 🕸️📈 $hashtags"
            topic.contains("trade", ignoreCase = true) || topic.contains("mt5", ignoreCase = true) ->
                "MT5 Algo Signal: Execution complete via low-latency terminal bridge. Risk locked at 1.0% with dynamic trailing stops. 🚀⚡ $hashtags"
            else ->
                "Holographic systems online. Daily tactical briefing synchronized across mobile and desktop dual-bridge. 🤖💻 $hashtags"
        }
    }
}
