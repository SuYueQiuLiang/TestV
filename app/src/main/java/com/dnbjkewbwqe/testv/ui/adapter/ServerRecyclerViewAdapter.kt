package com.dnbjkewbwqe.testv.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dnbjkewbwqe.testv.beans.TestServer
import com.dnbjkewbwqe.testv.databinding.ItemServerBinding
import com.dnbjkewbwqe.testv.utils.ServerManager

class ServerRecyclerViewAdapter(private val serverList : List<TestServer>,private val onSelectCallback : (TestServer)-> Unit) : RecyclerView.Adapter<ServerRecyclerViewAdapter.VH>() {
    class VH(val binding: ItemServerBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(ItemServerBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun getItemCount(): Int = serverList.size


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.serverName.text = serverList[position].fullName()
        holder.binding.flagImg.setImageResource(serverList[position].flagResourceId())
        if(ServerManager.selectServer == serverList[position])
            holder.binding.connectBtn.visibility = View.GONE
        holder.binding.root.setOnClickListener {
            onSelectCallback.invoke(serverList[position])
        }
    }
}