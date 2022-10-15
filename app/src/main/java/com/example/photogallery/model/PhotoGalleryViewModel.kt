package com.example.photogallery.model

import android.app.Application
import androidx.lifecycle.*
import com.example.photogallery.QueryPreferences
import com.example.photogallery.api.FlickrFetchr

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
     val galleryItemLiveData: LiveData<List<GalleryItem>>
     private val flickrFetchr = FlickrFetchr()
     private val mutableSearchTerm = MutableLiveData<String>()
     val searchTerm: String
          get() = mutableSearchTerm.value ?: ""
     init {
          mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)
          galleryItemLiveData = flickrFetchr.searchPhotos(searchTerm)
          Transformations.switchMap(mutableSearchTerm) { searchTerm ->
               if (searchTerm.isBlank()) {
                    flickrFetchr.fetchPhotos()
               } else {
                    flickrFetchr.searchPhotos(searchTerm)
               }
          }
     }
     fun fetchPhotos(query: String = "") {
          QueryPreferences.setStoredQuery(app, query)
          mutableSearchTerm.value = query
     }
     override fun onCleared() {
          super.onCleared()
          flickrFetchr.cancelRequestInFlight()
     }
}

