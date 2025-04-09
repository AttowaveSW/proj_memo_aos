package com.example.proj_memo_aos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proj_memo_aos.data.model.MemoDataModel
import com.example.proj_memo_aos.databinding.ItemMemoListBinding
import com.example.proj_memo_aos.helper.MemoClickType
import com.example.proj_memo_aos.helper.OnMemoClick

class MemoAdapter(
    private var memoList: ArrayList<MemoDataModel>,
    private var listener: OnMemoClick
) : RecyclerView.Adapter<MemoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMemoListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = memoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(memoList[position])
    }

    // List clear -> addAll -> notifyDataSetChanged() 방식을 변경하려고 하는중이나 어려움...
    fun updateData(newList: List<MemoDataModel>) {
        /*
        val sortedNewList = newList.sortedWith(compareByDescending { it.editTimestamp })

        val memoUidInOldList = memoList.map { it.uid }
        val memoUidInNewList = sortedNewList.map { it.uid }

        val memoToAdd = sortedNewList.filter { it.uid !in memoUidInOldList }
        val memoToRemove = memoList.filter { it.uid !in memoUidInNewList }
        val memoToChangePosition = memoList.filter { it.uid in memoUidInNewList && memoList.indexOf(it) != sortedNewList.indexOf(it) }
        val memoToChangeValue = memoList.filter { it.uid in memoUidInNewList &&  }

        for (removedMemo in memoToRemove) {
            val removedIndex = memoList.indexOf(removedMemo)
            memoList.remove(removedMemo)
            notifyItemRemoved(removedIndex)
        }

        for (addedMemo in memoToAdd) {
            val addedIndex = sortedNewList.indexOf(addedMemo)
            memoList.add(addedIndex, addedMemo)
            notifyItemRemoved(addedIndex)
        }

        for (chnagedMemo in memoToChange) {
            val addedIndex = sortedNewList.indexOf(addedMemo)
            memoList.add(addedIndex, addedMemo)
            notifyItemRemoved(addedIndex)
        }






        for (newMemo in sortedNewList) {
            val existingMemoIndex = sortedNewList.indexOfFirst { it.uid == newMemo.uid }

            if(existingMemoIndex == -1) {

            } else {
                if (memoList[existingMemoIndex] != newMemo) {
                    memoList[existingMemoIndex] = newMemo
                    notifyItemChanged(existingMemoIndex)
                }
            }
        }

        val oldSize = memoList.size
        val newSize = sortedNewList.size

        if (oldSize == newSize) {
            for (i in 0 until oldSize) {
                if (memoList[i] != sortedNewList[i]) {
                    notifyItemChanged(i)
                }
            }
        } else {
            if (newSize > oldSize) {
                for (i in oldSize until newSize) {
                    notifyItemInserted(i)
                }
            } else {
                for (i in oldSize - 1 downTo newSize) {
                    notifyItemRemoved(i)
                }
            }
        }
        */

        val list = newList.sortedWith(compareByDescending { it.editTimestamp })
        memoList.clear()
        memoList.addAll(list)
        notifyDataSetChanged()
    }

    //하이라이트 표시를 위한 함수 (TBD)
    fun toggleMemoListHighlight(query: String) {
        memoList.map { memo ->
            if (memo.title.contains(query) ||
                memo.content.contains(query)
                ) {
                memo.isHighlight = true
            } else {
                memo.isHighlight = false
            }
        }
    }

    //memo 각각의 View에 접근하기 위한 ViewHolder Class
    inner class ViewHolder(private val binding: ItemMemoListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MemoDataModel) {
            with(binding) {
                itemChapter = item
                root.setOnClickListener {
                    listener.onMemoClick(item, MemoClickType.ClickBody)
                }
                root.setOnLongClickListener {
                    listener.onMemoClick(item, MemoClickType.LongClick)
                    return@setOnLongClickListener true
                }
            }
        }
    }
}