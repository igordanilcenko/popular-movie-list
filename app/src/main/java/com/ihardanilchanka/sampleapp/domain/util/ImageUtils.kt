package com.ihardanilchanka.sampleapp.domain.util

const val IMAGE_SIZE_PREFIX = "original"
/**
 * To build an image URL, you will need 3 pieces of data. The base_url, size and file_path.
 * Simply combine them all and you will have a fully qualified URL.
 * More: https://developers.themoviedb.org/3/getting-started/images
 */
fun getImageUrl(baseUrl: String, filePath: String): String {
    return "$baseUrl/$IMAGE_SIZE_PREFIX/$filePath"
}