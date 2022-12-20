package my.edu.food_recipe_app.adapter.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import my.edu.food_recipe_app.R
import my.edu.food_recipe_app.model.Recipe

class RecipeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val nameText: TextView = itemView.findViewById(R.id.nameText)
    private val typeText: TextView = itemView.findViewById(R.id.typeText)
    private val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
    private val createDateText: TextView = itemView.findViewById(R.id.createDateText)
    private val updateDateText: TextView = itemView.findViewById(R.id.updateDateText)
    private val image: ImageView = itemView.findViewById(R.id.image)

    fun bindItem(recipe: Recipe) {
        itemView.apply {
            nameText.text = recipe.name
//            priceText.text = "${recipe.price} Bath"
            typeText.text = recipe.type
            descriptionText.text = recipe.description
            createDateText.text = recipe.create_date!!.toDate().toString()
            Picasso
                .get()
                .load(recipe.photoUrl)
                .fit()
                .into(image)

            if (recipe.update_date != null) {
                updateDateText.text = recipe.update_date!!.toDate().toString()
            }
        }
    }
}
