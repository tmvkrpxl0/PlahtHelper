package kr.dshs.planthelper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import kr.dshs.planthelper.data.PlantProfile

class PlantProfileAdapter(context: Context, private val data: MutableList<PlantProfile>): ArrayAdapter<PlantProfile>(context, R.layout.plant_profile, data) {
    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val element = data[position]

        val view = if (convertView != null) convertView else {
            val findResult = plantProfiles.indexOfFirst { it.id == element.id }
            inflater.inflate(R.layout.plant_profile, parent, false)
        }

        val image = view.findViewById<ImageView>(R.id.photo)!!
        val name = view.findViewById<TextView>(R.id.name)!!

        if (element.photoFile?.exists() == true) image.setImageURI(element.photoFile.toUri())
        name.text = element.getAnyName()

        return view
    }

    override fun add(`object`: PlantProfile?) {
        super.add(`object`)
    }

    override fun remove(`object`: PlantProfile?) {
        super.remove(`object`)
    }

    override fun addAll(collection: MutableCollection<out PlantProfile>) {
        super.addAll(collection)
    }

    override fun addAll(vararg items: PlantProfile?) {
        super.addAll(*items)
    }
}