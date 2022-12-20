package my.edu.food_recipe_app

import android.R
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
import my.edu.food_recipe_app.databinding.ActivityUpdateBinding

class UpdateActivity : AppCompatActivity() {

    private lateinit var storageRef: StorageReference
    private lateinit var photoUrl: String
    private var imageUri: Uri? = null

    //    var recipeTypes = arrayOf("Fast Food", "Dessert", "Malaysian")
    var type = ""

    private lateinit var list: ArrayList<Recipe>

    private var selected: Recipe = Recipe()

    private val recipeViewModel: RecipeViewModel by viewModels()

    private var binding : ActivityUpdateBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding?.root)
//        setContentView(R.layout.activity_add_recipe)

        initElement()
        initViewModel()
    }

    private fun initElement() {
        list = ArrayList()

        binding!!.submit.setOnClickListener {
            create()
        }

        val spinner = binding!!.typeSpinner
        val recipeTypes = resources.getStringArray(my.edu.food_recipe_app.R.array.recipetypes)
        val arrayAdapter = ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item, recipeTypes)

        val bundle = intent.extras
        if (bundle != null){
            selected = intent.getParcelableExtra<Recipe>("selected")!!
            binding!!.name.setText("${bundle.getString("name")}")
            spinner.adapter = arrayAdapter
            var spinnerPosition = arrayAdapter.getPosition(bundle.getString("type").toString())
            spinner.setSelection(spinnerPosition)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                binding!!.type.setText(recipeTypes[position])
                    type = recipeTypes[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
//                binding!!.type.setText("Fast Food")
                    type = bundle.getString("type").toString()
                }
            }
            binding!!.description.setText("${bundle.getString("description")}")
            photoUrl = bundle.getString("photoUrl").toString()
        }

        Picasso
            .get()
            .load(photoUrl)
//            .resize(50, 50)
            .fit()
            .into(binding!!.image)
        binding!!.image.setOnClickListener(){
            resultLauncher.launch("image/*")
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
    }

    private fun onCreate(it: Boolean) {
        if (it) {
            recipeViewModel.getList()
            resetText()
            Toast.makeText(this,"Added Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onUpdate(it: Boolean) {
        if (it) {
            recipeViewModel.getList()
            resetText()
            Toast.makeText(this,"Updated Successfully, refresh the list.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun create() {
        val recipe = Recipe(
            selected.id,
            binding!!.name.text.toString(),
//            binding!!.price.text.toString().toDouble(),
//            binding!!.type.text.toString(),
            type,
            binding!!.description.text.toString(),
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

        binding!!.name.text = null
//        binding!!.price.text = null
//        binding!!.type.text = null
        type = ""
        binding!!.description.text = null
    }

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
