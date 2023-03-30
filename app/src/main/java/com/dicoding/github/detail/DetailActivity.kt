package com.dicoding.github.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.annotation.ColorRes
import coil.transform.CircleCropTransformation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import coil.load
import com.dicoding.github.R
import com.dicoding.github.data.local.DbModule
import com.dicoding.github.data.model.DetailUserResponse
import com.dicoding.github.data.model.UserGithubRespone
import com.dicoding.github.databinding.ActivityDetailBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.dicoding.github.result.Result
import com.dicoding.github.detail.follow.FragmentFollow


class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        DetailViewModel.Factory(DbModule(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val item = intent.getParcelableExtra<UserGithubRespone.Item>("item")
        val username = item?.login ?: ""

        viewModel.resultDetailUser.observe(this) {
            when (it) {
                is Result.Success<*> -> {
                    val user = it.data as DetailUserResponse
                    binding.image.load(user.avatarUrl) {
                        transformations(CircleCropTransformation())
                    }

                    binding.nama.text = user.name
                    binding.medsos.text = user.login
                    binding.nolfol.text = user.followers.toString()
                    binding.nolfob.text = user.following.toString()
                }
                is Result.Error -> {
                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    binding.progressBar.isVisible = it.isLoading
                }
            }
        }
        viewModel.getDetailUser(username)

        val fragments = mutableListOf<Fragment>(
            FragmentFollow.newInstance(FragmentFollow.FOLLOWERS),
            FragmentFollow.newInstance(FragmentFollow.FOLLOWING)
        )
        val titleFragments = mutableListOf(
            getString(R.string.followers), getString(R.string.following),
        )
        val adapter = DetailAdapter(this, fragments)
        binding.viewpager.adapter = adapter

        TabLayoutMediator(binding.tab, binding.viewpager) { tab, posisi ->
            tab.text = titleFragments[posisi]
        }.attach()

        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    viewModel.getFollowers(username)
                } else {
                    viewModel.getFollowing(username)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        viewModel.getFollowers(username)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

fun FloatingActionButton.changeIconColor(@ColorRes color: Int) {
    imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this.context, color))
}