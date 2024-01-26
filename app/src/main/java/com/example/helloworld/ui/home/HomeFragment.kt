package com.example.helloworld.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.helloworld.databinding.FragmentHomeBinding
import com.example.helloworld.retrofit.ImageApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val KEY_AUTHOR = "KEY_AUTHOR"
private const val KEY_DOWNLOAD_URL = "KEY_DOWNLOAD_URL"

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val imageApi: ImageApi
    private lateinit var homeViewModel: HomeViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val adapter = ImageAdapter()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://picsum.photos")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        imageApi = retrofit.create(ImageApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        homeViewModel = if (savedInstanceState == null) {
            HomeViewModel(
                null,
                null,
            )
        } else {
            HomeViewModel(
                savedInstanceState.getString(KEY_AUTHOR),
                savedInstanceState.getString(KEY_DOWNLOAD_URL)!!,
            )
        }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        binding.homeLoadImageButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val image = imageApi.getImage()
                activity?.runOnUiThread {
                    homeViewModel = HomeViewModel(
                        image.author,
                        image.download_url
                    )
                    updateUi()
                    initHomeRecyclerView()
                }
            }
        }

        return root
    }

    private fun updateUi() {
      //  binding.homeTV.text = homeViewModel.author
      //  Glide.with(requireContext()).load(homeViewModel.download_url).into(binding.homeImageView)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_AUTHOR, homeViewModel.author)
        outState.putString(KEY_DOWNLOAD_URL, homeViewModel.download_url)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initHomeRecyclerView() {
        binding.apply {
            homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            homeRecyclerView.adapter = adapter

            val imageVm = HomeViewModel(homeViewModel.author, homeViewModel.download_url)
            adapter.addImage(imageVm)

        }
    }

}