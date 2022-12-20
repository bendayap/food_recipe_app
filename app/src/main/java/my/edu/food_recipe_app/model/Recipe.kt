package my.edu.food_recipe_app.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Recipe(
    var id: String? = null,
    var name: String? = null,
//    var price: Double? = null,
    var type: String? = null,
    var description: String? = null,
    var create_date: Timestamp? = null,
    var update_date: Timestamp? = null,
    var photoUrl: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
//        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString(),

    ) {
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
//            "price" to price,
            "type" to type,
            "description" to description,
            "create_date" to create_date,
            "update_date" to update_date,
            "photoUrl" to photoUrl,
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
//        parcel.writeValue(price)
        parcel.writeString(type)
        parcel.writeString(description)
        parcel.writeParcelable(create_date, flags)
        parcel.writeParcelable(update_date, flags)
        parcel.writeString(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}
