package com.dicoding.github

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.dicoding.github.data.model.UserGithubRespone
import com.dicoding.github.databinding.ItemRowUserBinding

class GithubAdapter(
    private val data: MutableList<UserGithubRespone.Item> = mutableListOf(),
    private val listener: (UserGithubRespone.Item) -> Unit
) :
    RecyclerView.Adapter<GithubAdapter.UserViewHolder>() {

    fun setData(data: MutableList<UserGithubRespone.Item>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    class UserViewHolder(private val v: ItemRowUserBinding) : RecyclerView.ViewHolder(v.root) {
        fun bind(item: UserGithubRespone.Item) {
            v.image.load(item.avatar_url) {
                transformations(CircleCropTransformation())
            }

            v.username.text = item.login
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount(): Int = data.size
}