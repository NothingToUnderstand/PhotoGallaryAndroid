package com.example.photogallery

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.photogallery.api.FlickrFetchr
import com.example.photogallery.model.GalleryItem

private const val TAG = "PollWorker"
class PollWorker(val context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    override fun doWork(): Result {
        val query = QueryPreferences.getStoredQuery(context)
        val lastResultId = QueryPreferences.getLastResultId(context).toLong()
        val items: List<GalleryItem> = if (query.isEmpty()) {
            FlickrFetchr().fetchPhotosRequest()
                .execute()
                .body()
                ?.galleryItems
        } else {
            FlickrFetchr().searchPhotosRequest(query)
                .execute()
                .body()
                ?.galleryItems
        } ?: emptyList()
        if (items.isEmpty()) {
            return Result.success()
        }
        val resultId = items.first().id
        if (resultId == lastResultId) {
            Log.i(TAG, "Got an old result: $resultId")
        } else {
            Log.i(TAG, "Got a new result: $resultId")
            QueryPreferences.setLastResultId(context, resultId)
        }
        return Result.success()
    }
}