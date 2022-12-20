package my.edu.food_recipe_app

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import my.edu.food_recipe_app.adapter.RecipeAdapter
import my.edu.food_recipe_app.model.Recipe
import my.edu.food_recipe_app.viewmodel.RecipeViewModel
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import my.edu.food_recipe_app.databinding.ActivityAddRecipeBinding

class AddRecipeActivity : AppCompatActivity(), RecipeAdapter.OnItemClickListener {

    private lateinit var storageRef: StorageReference

    //    var recipeTypes = arrayOf("Fast Food", "Dessert", "Malaysian")
    var type = ""
    var photoUrl = "https://images.unsplash.com/photo-1531928351158-2f736078e0a1?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8ZW1wdHklMjBwbGF0ZXxlbnwwfHwwfHw%3D&w=1000&q=80"
    private var imageUri: Uri? = null

    val REQUEST_CODE = 100

    private lateinit var name: EditText
//    private lateinit var type: EditText
    private lateinit var description: EditText
    private lateinit var submit: Button
    private lateinit var image: ImageView

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var list: ArrayList<Recipe>

    private var selected: Recipe = Recipe()

    private val recipeViewModel: RecipeViewModel by viewModels()

    private var binding : ActivityAddRecipeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        initElement()
        initViewModel()
    }

    private fun initElement() {
        val spinner = binding!!.typeSpinner
        val recipeTypes = resources.getStringArray(R.array.recipetypes)
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, recipeTypes)

        name = binding!!.name
//        type = findViewById(R.id.type)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                binding!!.type.setText(recipeTypes[position])
                type = recipeTypes[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
//                binding!!.type.setText("Fast Food")
                type = "Fast Food"
            }
        }
        description = binding!!.description
        submit = binding!!.submit
        image = binding!!.image
        Picasso
            .get()
            .load(photoUrl)
//            .resize(50, 50)
            .fit()
            .into(image)

        list = ArrayList()

        binding!!.image.setOnClickListener(){
            resultLauncher.launch("image/*")
        }

        submit.setOnClickListener {
            create()
        }

        // Get list
        recipeViewModel.getList()

    }

    private fun initViewModel() {
        recipeViewModel.createLiveData.observe(this) {
            onCreate(it)
        }

        recipeViewModel.updateLiveData.observe(this) {
            onUpdate(it)
        }

        recipeViewModel.deleteLiveData.observe(this) {
            onDelete(it)
        }
    }

    private fun onCreate(it: Boolean) {
        if (it) {
            recipeViewModel.getList()
            resetText()
            Toast.makeText(this,"Added Successfully, refresh the list.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun onUpdate(it: Boolean) {
        if (it) {
            recipeViewModel.getList()
            resetText()
        }
    }

    private fun onDelete(it: Boolean) {
        if (it) {
            recipeViewModel.getList()
            resetText()
        }
    }

    private fun create() {
        val recipe = Recipe(
            selected.id,
            name.text.toString(),
//            price.text.toString().toDouble(),
//            type.text.toString(),
            type,
            description.text.toString(),
            selected.create_date ?: Timestamp.now(),
            selected.update_date,
            photoUrl
        )
        if (recipe.id != null) {
            recipeViewModel.update(recipe)
        } else {
            recipeViewModel.create(recipe)
        }
    }

    private fun resetText() {
        selected = Recipe()

        name.text = null
//        price.text = null
        type = ""
        description.text = null
        photoUrl = ""
    }

    override fun onClick(item: Recipe, position: Int) {
        selected = item
        selected.update_date = Timestamp.now()

        name.setText(selected.name)
//        price.setText(selected.price.toString())
//        type.setText(selected.type)
        description.setText(selected.description)
    }

    override fun onDelete(item: Recipe, position: Int) {
        recipeViewModel.delete(item.id!!)
    }

//    private fun openGalleryForImage() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, REQUEST_CODE)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
//            image.setImageURI(data?.data) // handle chosen image
//        }
//    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        imageUri = it
        photoUrl = imageUri.toString()
        binding!!.image.setImageURI(it)
        uploadImage()
    }

    private fun uploadImage() {
        storageRef = FirebaseStorage.getInstance().reference.child("Images")
            .child(System.currentTimeMillis().toString())
//        imageUri?.let {
            var uploadTask = storageRef.putFile(imageUri!!)
            var urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageRef.downloadUrl
            }.addOnSuccessListener { task->
//                if (task.isSuccessful) {
//                    photoUrl = task.toString()
                photoUrl = task.toString()
//                } else {
//                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
//                }
            }
    }

}
