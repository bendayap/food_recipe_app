package my.edu.food_recipe_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.food_recipe_app.adapter.RecipeAdapter
import my.edu.food_recipe_app.model.Recipe
import my.edu.food_recipe_app.viewmodel.RecipeViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import my.edu.food_recipe_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), RecipeAdapter.OnItemClickListener {

    private lateinit var rvList: RecyclerView

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var list: ArrayList<Recipe>

    var filter = "None"

    private var selected: Recipe = Recipe()

    private val recipeViewModel: RecipeViewModel by viewModels()

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initElement()
        initViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_appbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout -> {
                logout()
                return true
            }
            else -> {super.onOptionsItemSelected(item)}
        }
    }

    private fun initElement() {
        rvList = binding!!.rvList

        list = ArrayList()

        binding!!.btnAdd.setOnClickListener() {
            val intent = Intent(this@MainActivity, AddRecipeActivity::class.java)
            startActivity(intent)
        }

        binding!!.btnRefresh.setOnClickListener() {
            getList()
        }

        binding!!.btnFilter.setOnClickListener(){
            filter = if (binding!!.typeFilter.text.isBlank()) {
                "None"
            } else {
                binding!!.typeFilter.text.toString()
            }
            getList()
        }

        // Get list
        getList()

    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun getList() {
        if (filter == "None")
            recipeViewModel.getList()
        else
            recipeViewModel.getFilteredList(filter)
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

        recipeViewModel.getListLiveData.observe(this) {
            onGetList(it)
        }
    }

    private fun onCreate(it: Boolean) {
        if (it) {
            recipeViewModel.getList()
        }
    }

    private fun onUpdate(it: Boolean) {
        if (it) {
            recipeViewModel.getList()
        }
    }

    private fun onDelete(it: Boolean) {
        if (it) {
            recipeViewModel.getList()
            Toast.makeText(this,"Deleted Successfully", Toast.LENGTH_SHORT).show()

        }
    }

    private fun onGetList(it: List<Recipe>) {
        list = ArrayList()
        list.addAll(it)

        recipeAdapter = RecipeAdapter(list, this)

        rvList.adapter = recipeAdapter
        rvList.layoutManager = LinearLayoutManager(baseContext)

        recipeAdapter.notifyDataSetChanged()
    }

    override fun onClick(item: Recipe, position: Int) {
        selected = item
        selected.update_date = Timestamp.now()

//        name.setText(selected.name)
//        price.setText(selected.price.toString())
//        description.setText(selected.description)
        var bundle = Bundle()
        bundle.putParcelable("selected", selected)
        bundle.putString("name", selected.name)
//        bundle.putString("price", selected.price.toString())
        bundle.putString("type", selected.type)
        bundle.putString("description", selected.description)
        bundle.putString("photoUrl", selected.photoUrl)

        val intent = Intent(this@MainActivity, UpdateActivity::class.java).also {
            it.putExtras(bundle)
        }
        startActivity(intent)
    }

    override fun onDelete(item: Recipe, position: Int) {
        recipeViewModel.delete(item.id!!)
    }
}
