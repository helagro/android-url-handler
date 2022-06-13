package com.hlag.customurlhandler

import android.app.ListActivity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import java.util.*


class Chooser : ListActivity() {
    var adapter: AppAdapter? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chooser_layout)
        val pm = packageManager
        val main = Intent(Intent.ACTION_MAIN, null)
        main.addCategory(Intent.CATEGORY_LAUNCHER)
        val launchables = pm.queryIntentActivities(main, 0)
        Collections.sort(
            launchables,
            ResolveInfo.DisplayNameComparator(pm)
        )
        adapter = AppAdapter(pm, launchables)
        listAdapter = adapter
    }

    override fun onListItemClick(
        l: ListView?, v: View?,
        position: Int, id: Long
    ) {
        val launchable = adapter!!.getItem(position)
        val activity = launchable!!.activityInfo
        val name = ComponentName(
            activity.applicationInfo.packageName,
            activity.name
        )

        val packName = name.packageName

        val intentMessage = Intent()
        intentMessage.putExtra("MESSAGE_package_name", packName)
        setResult(1, intentMessage)
        finish()
    }

    inner class AppAdapter(
        pm: PackageManager?,
        apps: List<ResolveInfo?>?
    ) :
        ArrayAdapter<ResolveInfo?>(this@Chooser, R.layout.row, apps!!) {
        private var pm: PackageManager? = null
        override fun getView(
            position: Int, convertView: View?,
            parent: ViewGroup
        ): View {
            var convertView1: View? = convertView
            if (convertView1 == null) {
                convertView1 = newView(parent)
            }
            bindView(position, convertView1)
            return convertView1
        }

        private fun newView(parent: ViewGroup): View {
            return layoutInflater.inflate(R.layout.row, parent, false)
        }

        private fun bindView(position: Int, row: View?) {
            val label = row?.findViewById(R.id.label) as TextView
            label.text = getItem(position)!!.loadLabel(pm)
            val icon: ImageView = row.findViewById(R.id.icon) as ImageView
            icon.setImageDrawable(getItem(position)!!.loadIcon(pm))
        }

        init {
            this.pm = pm
        }
    }
}