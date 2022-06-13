package com.hlag.customurlhandler

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.net.MalformedURLException
import java.net.URL


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var adapter: UrlItemAdapter
    private val TAG = "MainActivity"
    var isFABOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val urlItems = DbHelper.getInstance(this).getUrlItems()

        val urlStr: String? = intent.data.toString()
        urlStr?.let {
            try {
                val url: URL? = URL(urlStr)
                for (urlItem in urlItems) {
                    if (URL(urlItem.url).equals(url)) {
                        val launchIntent =
                            packageManager.getLaunchIntentForPackage(urlItem.app)
                        launchIntent?.let { startActivity(it) }

                        sendBroadcast(Intent(urlItem.broadcast))
                        finish()
                    }
                }
            } catch (e: MalformedURLException) {
            }

        }

        adapter = UrlItemAdapter(this, urlItems)
        view_pager.adapter = adapter

        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(view_pager)


        menu_fab.setOnClickListener(this)
        fab1.setOnClickListener(this)
        fab2.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (null != data) {
            val message = data.getStringExtra("MESSAGE_package_name")
            adapter.setAppName(requestCode, message!!)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.setup()
    }

    override fun onClick(v: View?) {
        when (v) {
            menu_fab -> if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
            fab1 -> adapter.delUrlItem(view_pager.currentItem)
            fab2 -> adapter.addUrlItem()
        }
    }

    private fun showFABMenu() {
        isFABOpen = true
        fab1.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        fab2.animate().translationY(-resources.getDimension(R.dimen.standard_105))
    }

    private fun closeFABMenu() {
        isFABOpen = false
        fab1.animate().translationY(0F)
        fab2.animate().translationY(0F)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        closeFABMenu()
    }

    override fun onPause() {
        val pos = view_pager.currentItem
        val curView = view_pager.focusedChild
        curView?.let { adapter.save(pos, curView) }

        DbHelper.getInstance(this).close()
        super.onPause()
    }
}