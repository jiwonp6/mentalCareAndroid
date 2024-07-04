package com.busanit.mentalcareandroid.fragment

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.adapter.BoardAdapter
import com.busanit.mentalcareandroid.databinding.FragmentMentalBinding
import com.busanit.mentalcareandroid.model.Board
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheeringFragment : Fragment() {
    lateinit var binding: FragmentMentalBinding
    lateinit var boardAdapter: BoardAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMentalBinding.inflate(inflater, container, false)
        return binding.root


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            getBoards()
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getLongExtra("deletedBoardId", 0L)?.let {
                        deletedBoardId -> boardAdapter.removeByBoardId(deletedBoardId)
                }
            }
        }

        boardAdapter = BoardAdapter(activityResultLauncher)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = boardAdapter

        getBoards()
    }

    // 게시글 목록 가져오기
    private fun getBoards() {
        RetrofitClient.api.boardTagCheering().enqueue(object : Callback<List<Board>> {
            override fun onResponse(call: Call<List<Board>>, response: Response<List<Board>>) {
                if (response.isSuccessful) {
                    val boards = response.body() ?: emptyList()
                    boardAdapter.updateBoards(boards.toMutableList())
                } else {
                    Log.d("mylog", "onResponse: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Board>>, t: Throwable) {
                Log.d("mylog", "onFailure: ${t.message}")
            }

        })
    }

}