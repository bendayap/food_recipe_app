package my.edu.food_recipe_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import my.edu.food_recipe_app.model.Recipe
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecipeViewModel: ViewModel() {

    private var db = Firebase.firestore
    private val recipes = "recipes"

    val createLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val updateLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val getListLiveData: MutableLiveData<List<Recipe>> by lazy {
        MutableLiveData<List<Recipe>>()
    }

    val deleteLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun create(recipe: Recipe) {
        val docRef = db.collection(recipes)
        docRef.add(recipe.toMap()).addOnSuccessListener {
            createLiveData.postValue(true)
        }.addOnFailureListener {
            Log.d("create", it.localizedMessage!!)
            createLiveData.postValue(false)
        }
    }

    fun update(recipe: Recipe) {
        val docRef = db.collection(recipes)
        docRef.document(recipe.id!!).update(recipe.toMap()).addOnSuccessListener {
            updateLiveData.postValue(true)
        }.addOnFailureListener {
            Log.d("update", it.localizedMessage!!)
            updateLiveData.postValue(false)
        }
    }

    fun delete(id: String) {
        val docRef = db.collection(recipes)
        docRef.document(id).delete().addOnSuccessListener {
            deleteLiveData.postValue(true)
        }.addOnFailureListener {
            Log.d("delete", it.localizedMessage!!)
            deleteLiveData.postValue(false)
        }
    }

    fun getList() {
        val docRef = db.collection(recipes)
        docRef.get().addOnSuccessListener {
            val recipes = ArrayList<Recipe>()
            for (item in it.documents) {
                val recipe = Recipe()
                recipe.id = item.id
                recipe.name = item.data!!["name"] as String?
//                recipe.price = item.data!!["price"].toString().toDouble()
                recipe.type = item.data!!["type"] as String?
                recipe.description = item.data!!["description"] as String?
                recipe.create_date = item.data!!["create_date"] as Timestamp?
                recipe.update_date = item.data!!["update_date"] as Timestamp?
                recipe.photoUrl = item.data!!["photoUrl"] as String?
                recipes.add(recipe)
            }

            getListLiveData.postValue(recipes)
        }.addOnFailureListener {
            Log.d("get", it.localizedMessage!!)
            getListLiveData.postValue(null)
        }
    }

    fun getFilteredList(filter: String) {
        val docRef = db.collection(recipes)
        docRef.whereEqualTo("type", filter).get().addOnSuccessListener {
            val recipes = ArrayList<Recipe>()
            for (item in it.documents) {
                val recipe = Recipe()
                recipe.id = item.id
                recipe.name = item.data!!["name"] as String?
                recipe.type = item.data!!["type"] as String?
                recipe.description = item.data!!["description"] as String?
                recipe.create_date = item.data!!["create_date"] as Timestamp?
                recipe.update_date = item.data!!["update_date"] as Timestamp?
                recipe.photoUrl = item.data!!["photoUrl"] as String?
                recipes.add(recipe)
            }

            getListLiveData.postValue(recipes)
        }.addOnFailureListener {
            Log.d("get", it.localizedMessage!!)
            getListLiveData.postValue(null)
        }
    }
}
