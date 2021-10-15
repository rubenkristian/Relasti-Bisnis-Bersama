package com.ethelworld.RBBApp.tools

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class EndlessScrollListener: RecyclerView.OnScrollListener {
    private var visibleThreshold = 20
    private lateinit var OnLoadMoreListener: OnLoadMoreListener
    private var lastVisibleItem: Int = 0
    private var currentPage = 0
    private var previousTotalItemCount = 0
    private var loading = true
    private val startingPageIndex = 1
    private var totalItemCount:Int = 0
    private var layoutManage: RecyclerView.LayoutManager

    fun setLoaded() {
        loading = false
    }

    fun getLoaded(): Boolean {
        return loading
    }

    fun setOnLoadMoreListener(OnLoadMoreListener: OnLoadMoreListener) {
        this.OnLoadMoreListener = OnLoadMoreListener
    }

    constructor(layoutManager:LinearLayoutManager) {
        this.layoutManage = layoutManager
    }

    constructor(layoutManager:GridLayoutManager) {
        this.layoutManage = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    constructor(layoutManager:StaggeredGridLayoutManager) {
        this.layoutManage = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy <= 0) {
            OnLoadMoreListener.onTheTop()
            return
        }

        totalItemCount = layoutManage.itemCount

        if (layoutManage is StaggeredGridLayoutManager) {
            val lastVisibleItemPositions =
                (layoutManage as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
            // get maximum element within the list
            lastVisibleItem = getLastVisibleItem(lastVisibleItemPositions)
        } else if (layoutManage is GridLayoutManager) {
            lastVisibleItem = (layoutManage as GridLayoutManager).findLastVisibleItemPosition()
        } else if (layoutManage is LinearLayoutManager) {
            lastVisibleItem = (layoutManage as LinearLayoutManager).findLastVisibleItemPosition()
        }
//        Log.i("ENDLESS", String.format("total item = %d, last visible = %d", totalItemCount, (lastVisibleItem + visibleThreshold)))
        if (!loading && totalItemCount <= lastVisibleItem + visibleThreshold) {
            OnLoadMoreListener.onLoadMore()
            loading = true
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if(newState == RecyclerView.SCROLL_STATE_IDLE) {
            val canScrollDownMore: Boolean = recyclerView.canScrollVertically(1)

            if(!canScrollDownMore && !loading) {
                onScrolled(recyclerView, 0, 1)
            }
        }
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    fun resetState() {
        this.currentPage = this.startingPageIndex
        this.previousTotalItemCount = 0
        this.loading = true
    }
}