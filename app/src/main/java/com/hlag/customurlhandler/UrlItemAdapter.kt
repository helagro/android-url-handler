package com.hlag.customurlhandler

import android.R.attr.label
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.getSystemService
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager


class UrlItemAdapter(private var context: Context, private val urlItems: ArrayList<UrlItem>) :
    PagerAdapter() {
    companion object {
        const val TAG = "Adapter"
    }

    private var dbHelper: DbHelper? = null
    var layoutToAddAppNameTo: View? = null

    //publics
    fun setup() {
        dbHelper = DbHelper.getInstance(context)
    }

    fun addUrlItem() {
        val newItem = UrlItem(
            -1,
            "",
            "http://hlagr.github.io/",
            "",
            null
        )
        urlItems.add(newItem)
        notifyDataSetChanged()
    }

    fun delUrlItem(pos: Int) {
        dbHelper!!.delUrlItem(urlItems[pos].id)
        urlItems.removeAt(pos)
        notifyDataSetChanged()
    }

    fun setAppName(pos: Int, name: String) {
        urlItems[pos].app = name
        layoutToAddAppNameTo!!.findViewById<EditText>(R.id.app_name).setText(urlItems[pos].app)
    }


    //overrides
    override fun getPageTitle(position: Int): CharSequence? {
        return urlItems[position].name
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return urlItems.size
    }

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val layout =
            inflater.inflate(R.layout.url_item_view, collection, false) as ViewGroup
        val urlItem = urlItems[position]

        layout.findViewById<EditText>(R.id.name).setText(urlItem.name)
        val urlView = layout.findViewById<EditText>(R.id.url)
        urlView.setText(urlItem.url)
        layout.findViewById<EditText>(R.id.app_name).setText(urlItem.app)
        layout.findViewById<EditText>(R.id.broadcast).setText(urlItem.broadcast)

        layout.findViewById<TextView>(R.id.copy_url).setOnClickListener {
            val clipboard: ClipboardManager? =
                context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("Url", urlView.text.toString().replace("http://", ""))
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_LONG).show()
        }
        layout.findViewById<TextView>(R.id.app_link).setOnClickListener {
            layoutToAddAppNameTo = layout
            val appIntent = Intent(context, Chooser::class.java)
            startActivityForResult(context as Activity, appIntent, 1, null)
        }

        collection.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        save(position, `object` as View)
        (container as ViewPager).removeView(`object`)
    }


    fun save(pos: Int, view: View) {
        val urlItem = urlItems[pos]

        urlItem.name = view.findViewById<EditText>(R.id.name).text.toString()
        urlItem.url = view.findViewById<EditText>(R.id.url).text.toString()
        urlItem.app = view.findViewById<EditText>(R.id.app_name).text.toString()
        urlItem.broadcast = view.findViewById<EditText>(R.id.broadcast).text.toString()
        dbHelper!!.updateItem(urlItem)

        Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show()
    }
}