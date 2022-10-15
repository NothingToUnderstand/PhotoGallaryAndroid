package com.example.photogallery.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photogallery.PhotoDeserializer
import com.example.photogallery.model.GalleryItem
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "FlickrFetchr_Tag"

class FlickrFetchr {
    private val flickrApi: FlickrApi
    private lateinit var flickrRequest: Call<PhotoResponse>

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()
        val gson: Gson =
            GsonBuilder().registerTypeAdapter(PhotoResponse::class.java, PhotoDeserializer())
                .create()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }
    fun fetchPhotosRequest(): Call<PhotoResponse> {
        return flickrApi.fetchPhotos()
    }
    fun searchPhotosRequest(query: String): Call<PhotoResponse> {
        return flickrApi.searchPhotos(query)
    }
    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(fetchPhotosRequest())
    }
    fun searchPhotos(query: String): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(searchPhotosRequest(query))
    }

    @WorkerThread
    fun fetchPhoto(url: String): Bitmap? {
        val response: Response<ResponseBody> = flickrApi.fetchUrlBytes(url).execute()
        return response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
    }

    private fun fetchPhotoMetadata(flickrRequest: Call<PhotoResponse>)
            : LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        flickrRequest.enqueue(object : Callback<PhotoResponse> {
            override fun onFailure(call: Call<PhotoResponse>, t: Throwable) {
            }

            override fun onResponse(call: Call<PhotoResponse>, response: Response<PhotoResponse>) {
                var galleryItems: List<GalleryItem> = response.body()?.galleryItems ?: listOf()
                galleryItems = galleryItems.filterNot {
                    it.url.isBlank()
                }
                responseLiveData.value = galleryItems
            }
        })
        return responseLiveData
    }

    fun cancelRequestInFlight() {
        if (::flickrRequest.isInitialized) {
            flickrRequest.cancel()
        }
    }
}