package com.example.model

enum class InstagramMediaType {
    IMAGE,
    REELS,
    STORY
}

enum class PostStatus {
    QUEUED,
    PUBLISHING,
    PUBLISHED,
    FAILED
}

data class ScheduledPost(
    val id: String,
    val caption: String,
    val mediaUrl: String,
    val mediaType: InstagramMediaType = InstagramMediaType.IMAGE,
    val scheduledTimeMillis: Long,
    val scheduledTimeFormatted: String,
    val status: PostStatus = PostStatus.QUEUED,
    val creationId: String? = null,
    val publishedPostId: String? = null,
    val errorMessage: String? = null
)

data class InstagramProfile(
    val id: String = "17841405829402914",
    val username: String = "jarvis_command_center",
    val name: String = "JARVIS Neural Operations",
    val followersCount: Int = 14850,
    val mediaCount: Int = 142,
    val isApiConnected: Boolean = true
)
