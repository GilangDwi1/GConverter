package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import utils.HistoryItem

class HistoryAdapter(
    private val items: List<HistoryItem>,
    private val onItemClick: (File) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.imgFileIcon)
        val name: TextView = view.findViewById(R.id.txtFileName)
        val info: TextView = view.findViewById(R.id.txtFileInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.info.text = "${item.size} | ${item.date}"

        // Logika memilih ikon berdasarkan ekstensi file
        val extension = item.file.extension.lowercase()
        val iconRes = when (extension) {
            "pdf" -> R.drawable.ic_pdftoword
            "doc", "docx" -> R.drawable.ic_word
            "xls", "xlsx" -> R.drawable.ic_excel
            "jpg", "jpeg", "png", "webp" -> R.drawable.ic_jpg // atau ic_png
            "mp4", "mov" -> R.drawable.ic_mp4 // atau ic_mov
            "mp3", "wav" -> R.drawable.ic_mp3
            else -> R.drawable.file_icon // icon default
        }
        holder.icon.setImageResource(iconRes)

        holder.itemView.setOnClickListener { onItemClick(item.file) }
    }

    override fun getItemCount() = items.size
}
