package es.uji.al375496.cultivemanagement.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.uji.al375496.cultivemanagement.R
import es.uji.al375496.cultivemanagement.model.database.entities.Note

class NoteAdapter(private val notes: MutableList<Note>, val onclick : (Note) -> Unit) : RecyclerView.Adapter<NoteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notes, parent,false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        with(notes[position]) {
            holder.title.text = title
            holder.body.text = text
            holder.view.setOnClickListener {
                onclick(this)
            }
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

}
