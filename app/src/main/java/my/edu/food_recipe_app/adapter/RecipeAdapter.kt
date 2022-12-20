package my.edu.food_recipe_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import my.edu.food_recipe_app.R
import my.edu.food_recipe_app.model.Recipe
import my.edu.food_recipe_app.adapter.viewholder.RecipeViewHolder

class RecipeAdapter(
    var list: List<Recipe>,
    var onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val item = list[position]
        holder.bindItem(item)
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(item, position)
        }
        holder.itemView.findViewById<Button>(R.id.delete).setOnClickListener {
            onItemClickListener.onDelete(item, position)
        }
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(item: Recipe, position: Int)
        fun onDelete(item: Recipe, position: Int)
    }
}
