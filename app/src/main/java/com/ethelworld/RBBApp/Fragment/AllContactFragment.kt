package com.ethelworld.RBBApp.Fragment

import android.content.Intent
import android.os.Bundle
//import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ethelworld.RBBApp.Adapter.ContactAdapter
import com.ethelworld.RBBApp.Item.Contact
import com.ethelworld.RBBApp.Presenter.ContactPresenter
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.UI.ContactDetailScreen
import com.ethelworld.RBBApp.View.ContactView
import com.ethelworld.RBBApp.tools.EndlessScrollListener
import com.ethelworld.RBBApp.tools.OnLoadMoreListener
import com.ethelworld.RBBApp.tools.OnPageListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class AllContactFragment : Fragment(), ContactView.View, OnPageListener {
    private var loadingFirst = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private var contactList: ArrayList<Contact?> = ArrayList()

    private lateinit var contactAdapter: ContactAdapter
    private lateinit var scrollListener: EndlessScrollListener
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var contactPresenter: ContactPresenter
    private lateinit var scrollTop: MaterialButton

    var page: Int = 1
    private var loading: Boolean = false

    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var isContactEnd = false

    private lateinit var searchView: SearchView
    private lateinit var querySearchListener: SearchView.OnQueryTextListener

    private var searchStore: String? = ""

    private var searchJob: Job? = null
    private var loadContactJob: Job? = null
    private var serverJob: Job? = null
    
    private lateinit var detailContactScreen: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_all_contact, container, false)

        contactRecyclerView = view.findViewById(R.id.contact_list)
        swipeRefreshLayout  = view.findViewById(R.id.refresh_swipe)
        scrollTop           = view.findViewById(R.id.scroll_top)

        contactPresenter = ContactPresenter(this, context)

        contactAdapter = ContactAdapter{ contact: Contact? ->
            detailContactScreen.putExtra("id", contact?.id)
            detailContactScreen.putExtra("id_user", contact?.idUser)
            detailContactScreen.putExtra("name", contact?.name)
            detailContactScreen.putExtra("city", contact?.city)
            detailContactScreen.putExtra("phoneNumber", contact?.phoneNumber)
            detailContactScreen.putExtra("occupation", contact?.occupation)

            startActivity(detailContactScreen)
        }

        setLayoutManager()
        setScrollListener()

        contactRecyclerView.adapter = contactAdapter

        contactAdapter.updateContact(contactList.toMutableList())

        swipeRefreshLayout.setOnRefreshListener {
            refreshContact()
        }

        detailContactScreen = Intent(context, ContactDetailScreen::class.java)

        scrollTop.setOnClickListener {
            contactRecyclerView.smoothScrollToPosition(0)
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        if(!loadingFirst) {
            loadingFirst = true

            syncContact()
            loadContact()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()

        activity?.menuInflater?.inflate(R.menu.list_menu, menu)

        val searchItem = menu.findItem(R.id.search_list)

        searchView = activity?.let { SearchView(it) }!!

        searchView.queryHint = "Cari Kontak..."

        querySearchListener = object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!loading) {
                    loading = true
                    isContactEnd = false

                    searchJob?.cancel()

                    searchStore = newText ?: ""

                    contactList.clear()

                    contactAdapter.updateContact(contactList.toMutableList())

                    page = 1

                    searchJob = CoroutineScope(Dispatchers.IO).launch {
                        contactPresenter.listContactLocal(searchStore, page)
                    }
                }
                return  true
            }
        }

        searchView.setOnQueryTextListener(querySearchListener)

        searchItem.actionView = searchView
    }

    private fun syncContact() {
        if(serverJob?.isCompleted == true || serverJob == null) {
            serverJob?.cancel()

            serverJob = CoroutineScope(Dispatchers.IO).launch {
                contactPresenter.syncContactFromServer()
            }
        }
    }

    private fun refreshContact() {
        if(!loading) {
            loading = true
            isContactEnd = false

            contactList.clear()
            contactAdapter.updateContact(contactList.toMutableList())

            page = 1

            syncContact()
            loadContactJob?.cancel()

            loadContactJob = CoroutineScope(Dispatchers.IO).launch {
                contactPresenter.listContactLocal(searchStore, page)
            }

            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadContact() {
        if(!loading) {
            loading = true

//            Log.i("PAGE", "page = $page")
            loadContactJob?.cancel()

            loadContactJob = CoroutineScope(Dispatchers.IO).launch {
                contactPresenter.listContactLocal(searchStore, page)
            }
        }
    }

    private fun setLayoutManager() {
        layoutManager                       = LinearLayoutManager(context)
        contactRecyclerView.layoutManager   = layoutManager

        contactRecyclerView.setHasFixedSize(true)
    }

    private fun setScrollListener() {
        layoutManager   = LinearLayoutManager(context)
        scrollListener  = EndlessScrollListener(layoutManager as LinearLayoutManager)

        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener{
            override fun onLoadMore() {
                if(!isContactEnd) {
//                    Log.i("SCROLL", "scroll more")
                    loadContact()
                }
            }

            override fun onTheTop() {
                scrollTop.visibility = View.GONE
            }
        })

        contactRecyclerView.addOnScrollListener(scrollListener)
    }

    override suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            contactList.add(null)
            contactAdapter.addLoadingView(contactList.toMutableList())
        }
    }

    override suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            if(contactList.size > 0) {
                if(contactList[contactList.size - 1] == null) {
                    contactList.removeAt(contactList.size - 1)
                }
            }

            contactAdapter.removeLoadingView(contactList.toMutableList())

            swipeRefreshLayout.isRefreshing = false
        }
    }

    override suspend fun showError(code: Int, msg: String?) {
        withContext(Dispatchers.Main) {
            loading = false

            if (msg != null) {
                view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT).show() }
            }
        }
    }

    override suspend fun onSuccess(contacts: ArrayList<Contact?>) {
        contactList.addAll(0, contacts)

        withContext(Dispatchers.Main) {
            loading = false

            contactAdapter.updateContact(contactList.toMutableList())

            view?.let {
                Snackbar.make(
                    it,
                    "Kontak baru berhasil ditambahkan hari ini.",
                    Snackbar.LENGTH_SHORT).show()
            }

            scrollTop.visibility = View.VISIBLE
        }
    }

    override suspend fun onSuccessLocal(contacts: ArrayList<Contact?>) {
        contactList.addAll(contacts)

        withContext(Dispatchers.Main) {
            loading = false

            contactAdapter.updateContact(contactList.toMutableList())
            scrollListener.setLoaded()

            page++
//            Log.i("CONTACPRESENTER", "SUCCESS FINISH")
        }
    }

    override suspend fun onContactLocalEnd() {
        withContext(Dispatchers.Main) {
            loading         = false
            isContactEnd    = true

            view?.let { Snackbar.make(it, "${contactAdapter.itemCount} kontak berhasil dimuat.", Snackbar.LENGTH_LONG).show() }
//            Log.i("CONTACPRESENTER", "SUCCESS FINISH")
        }
    }

    override fun OnLoad() {
        if(!loadingFirst) {
            loadingFirst = true

            syncContact()
            loadContact()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        contactPresenter.onDestroy()
    }
}