package com.yb.part4_chapter03

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.yb.part4_chapter03.MapActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import com.yb.part4_chapter03.databinding.ActivityMainBinding
import com.yb.part4_chapter03.model.LocationLatLngEntity
import com.yb.part4_chapter03.model.SearchResultEntity
import com.yb.part4_chapter03.response.search.Poi
import com.yb.part4_chapter03.response.search.Pois
import com.yb.part4_chapter03.utility.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var searchRecyclerViewAdapter: SearchRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        job = Job()

        initAdapter()
        initViews()
        bindViews()
        initData()
    }

    private fun initViews() = with(activityMainBinding) {
        emptyResultTextView.isVisible = false
        recyclerView.adapter = searchRecyclerViewAdapter
    }

    private fun bindViews() = with(activityMainBinding) {
        searchButton.setOnClickListener {
            searchKeyword(inputKeywordEditText.text.toString())
        }
    }

    private fun initAdapter() {
        searchRecyclerViewAdapter = SearchRecyclerViewAdapter()
    }

    private fun initData() {
        searchRecyclerViewAdapter.notifyDataSetChanged()
    }

    private fun setData(pois: Pois) {
        val dataList = pois.poi.map {
            SearchResultEntity(
                address = it.name ?: "빌딩명 없음",
                detailAddress = makeMainAdress(it),
                locationLatLng = LocationLatLngEntity(it.noorLat, it.noorLon)
            )
        }
        searchRecyclerViewAdapter.setSearchResultList(dataList) {
            startActivity(Intent(this, MapActivity::class.java).apply {
                putExtra(SEARCH_RESULT_EXTRA_KEY, it)
            })

        }
    }

    private fun searchKeyword(keywordString: String) {
        launch(coroutineContext) {
            try {
                //IO Thread로 변경
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keywordString
                    )

                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.d("Response", body.toString())
                            body.let {searchResponse ->
                                searchResponse?.searchPoiInfo?.let { setData(it.pois) }
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun makeMainAdress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }
}