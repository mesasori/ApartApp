package com.example.apartapp.ui.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apartapp.R
import com.example.apartapp.databinding.ItemSuggestBinding

class SuggestListAdapter : RecyclerView.Adapter<SuggestListAdapter.SuggestViewHolder>() {

    var items: List<SuggestHolderItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SuggestViewHolder(
        ItemSuggestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SuggestViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class SuggestViewHolder(private val binding: ItemSuggestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val spanColor = ContextCompat.getColor(binding.root.context, R.color.black)
        fun bind(item: SuggestHolderItem) = with(binding) {
            textTitle.text = item.title.text
            textSubtitle.text = item.subtitle?.text
            binding.root.setOnClickListener { item.onClick() }
        }
    }
}

data class SuggestHolderItem(
    val title: com.yandex.mapkit.SpannableString,
    val subtitle: com.yandex.mapkit.SpannableString?,
    val onClick: () -> Unit
)
