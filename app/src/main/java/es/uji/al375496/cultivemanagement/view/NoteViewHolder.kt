package es.uji.al375496.cultivemanagement.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.uji.al375496.cultivemanagement.R

class NoteViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(R.id.nameTextView)
    val body: TextView = view.findViewById(R.id.bodyTextView)
}
