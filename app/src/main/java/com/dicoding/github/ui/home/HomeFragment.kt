package com.dicoding.github.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.github.GithubAdapter
import com.dicoding.github.ui.home.HomeViewModel
import com.dicoding.github.R
import com.dicoding.github.data.local.SettingPreferences
import com.dicoding.github.data.model.UserGithubRespone
import com.dicoding.github.databinding.FragmentHomeBinding
import com.dicoding.github.detail.DetailActivity
import com.dicoding.github.ui.favorite.FavoriteFragment
import com.dicoding.github.result.Result
import com.dicoding.github.ui.setting.SettingFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {
        GithubAdapter { user ->
            Intent(requireActivity(), DetailActivity::class.java).apply {
                putExtra("item", user)
                startActivity(this)
            }
        }
    }

    private val viewModel by viewModels<HomeViewModel> {
        HomeViewModel.Factory(SettingPreferences(requireActivity()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTheme().observe(viewLifecycleOwner) { theme ->
            if (theme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            viewModel.getUser(theme)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.getUser(p0.toString())
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean = false

        })

        viewModel.resultUser.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success<*> -> {
                    adapter.setData(it.data as MutableList<UserGithubRespone.Item>)
                }
                is Result.Error -> {
                    Toast.makeText(requireActivity(), it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    binding.progressBar.isVisible = it.isLoading
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}