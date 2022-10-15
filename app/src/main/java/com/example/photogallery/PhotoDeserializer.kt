package com.example.photogallery

import android.util.Log
import com.example.photogallery.api.PhotoResponse
import com.google.gson.*
import java.lang.reflect.Type


private const val TAG = "DeserializerPhoto"

class PhotoDeserializer : JsonDeserializer<PhotoResponse> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PhotoResponse {
        return Gson().fromJson(
            json!!.asJsonObject.get("photos").asJsonObject,
            PhotoResponse::class.java
        )
    }
}