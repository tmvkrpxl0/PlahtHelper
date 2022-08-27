package kr.dshs.planthelper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kr.dshs.planthelper.data.PlantProfile

class PlantProfileAdapter(context: Context, private val data: List<PlantProfile>): BaseAdapter() {
    private val inflater = LayoutInflater.from(context)

    override fun getCount() = data.size

    override fun getItem(position: Int) = data[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.plant_profile, null)

        val image = view.findViewById<ImageView>(R.id.photo)!!
        val name = view.findViewById<TextView>(R.id.name)!!

        val element = data[position]

        // image.setImageURI(element.photoUri)
        name.text = element.customName ?: element.academic.name

        return view
    }
}