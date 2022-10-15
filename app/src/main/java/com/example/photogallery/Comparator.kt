package com.example.photogallery

import androidx.recyclerview.widget.DiffUtil
import com.example.photogallery.model.GalleryItem

class Comparator : DiffUtil.ItemCallback<GalleryItem>() {
    override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean =
        oldItem == newItem
}
