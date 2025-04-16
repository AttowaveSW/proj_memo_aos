package com.example.proj_memo_aos.ui.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.proj_memo_aos.data.model.MemoDataModel
import com.example.proj_memo_aos.databinding.GridViewItemMemoListBinding
import com.example.proj_memo_aos.databinding.ListViewItemMemoListBinding
import com.example.proj_memo_aos.helper.MemoClickType
import com.example.proj_memo_aos.helper.OnItemSelectedCountChange
import com.example.proj_memo_aos.helper.OnMemoClick
import com.example.proj_memo_aos.helper.OnSelectionModeChange

class MemoAdapter(
    private var recyclerView: RecyclerView,
    private var memoList: ArrayList<MemoDataModel>,
    private var onClickListener: OnMemoClick,
    private var onSelectionModeChangeListener: OnSelectionModeChange,
    private var onItemSelectedCountChangeListener: OnItemSelectedCountChange
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isGrid: Boolean = true
    private val selectionMap = mutableMapOf<MemoDataModel, Boolean>()
    var isSelectionMode: Boolean = false

    companion object {
        const val VIEW_TYPE_LIST = 0
        const val VIEW_TYPE_GRID = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGrid) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 뷰 타입에 따라 서로 다른 레이아웃 바인딩
        return if (viewType == VIEW_TYPE_GRID) {
            GridViewHolder(
                GridViewItemMemoListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ListViewHolder(
                ListViewItemMemoListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount() = memoList.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = memoList[position]
        // 뷰 타입에 따라 바인딩
        if (holder is GridViewHolder) {
            holder.bind(item, position)
        } else if (holder is ListViewHolder){
            holder.bind(item, position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains("selectionModeChanged")) {
            // 선택 모드 변경 시, 체크박스 상태 및 가시성 업데이트
            val item = memoList[position]
            if (holder is ListViewHolder) {
                holder.selectedCheckBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
                holder.selectedCheckBox.isChecked = selectionMap[item] ?: false
            } else if (holder is GridViewHolder) {
                holder.selectedCheckBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
                holder.selectedCheckBox.isChecked = selectionMap[item] ?: false
            }
            return
        }

        // 기본 full bind
        super.onBindViewHolder(holder, position, payloads)
    }

    /**
     * 메모 데이터 갱신
     */
    fun updateData(newList: List<MemoDataModel>) {
        val list = newList.sortedWith(compareByDescending { it.editTimestamp })
        val listCountDiff = memoList.count() - list.count()

        memoList.clear()
        memoList.addAll(list)

        selectionMap.clear()
        for (memo in memoList) {
            selectionMap[memo] = false
        }

        viewUpdate(listCountDiff)
    }

    /**
     * 보기 방식 변경 (그리드 / 리스트)
     */
    fun setViewType(grid: Boolean) {
        isGrid = grid
        viewUpdate(0)
    }

    /**
     * 선택 모드 진입
     */
    fun goToSelectionMode() {
        if (!isSelectionMode) {
            isSelectionMode = true

            for (i in 0 until recyclerView.childCount) {
                val childView = recyclerView.getChildAt(i)
                val holder = recyclerView.getChildViewHolder(childView)

                if (holder is ListViewHolder) {
                    holder.selectedCheckBox.visibility = View.VISIBLE
                } else if (holder is GridViewHolder){
                    holder.selectedCheckBox.visibility = View.VISIBLE
                }
            }

            for (i in memoList.indices) {
                notifyItemChanged(i, "selectionModeChanged")
            }
            onSelectionModeChangeListener.onSelectionModeChange(true)
        }
    }

    /**
     * 선택 모드 종료
     */
    fun exitToSelectionMode() {
        if (isSelectionMode) {
            isSelectionMode = false
            for (memo in memoList) {
                selectionMap[memo] = false
            }

            for (i in 0 until recyclerView.childCount) {
                val childView = recyclerView.getChildAt(i)
                val holder = recyclerView.getChildViewHolder(childView)

                if (holder is ListViewHolder) {
                    holder.selectedCheckBox.isChecked = false
                    holder.selectedCheckBox.visibility = View.GONE
                } else if (holder is GridViewHolder){
                    holder.selectedCheckBox.isChecked = false
                    holder.selectedCheckBox.visibility = View.GONE
                }
            }

            for (i in memoList.indices) {
                notifyItemChanged(i, "selectionModeChanged")
            }
            onSelectionModeChangeListener.onSelectionModeChange(false)
        }
    }

    /**
     * 전체 선택 / 전체 선택 해제 내부 처리 함수
     */
    private fun changeAllSelections(isSelected: Boolean) {
        for (memo in memoList) {
            selectionMap[memo] = isSelected
        }

        for (i in 0 until recyclerView.childCount) {
            val childView = recyclerView.getChildAt(i)
            val holder = recyclerView.getChildViewHolder(childView)

            if (holder is ListViewHolder) {
                holder.selectedCheckBox.isChecked = isSelected
            } else if (holder is GridViewHolder){
                holder.selectedCheckBox.isChecked = isSelected
            }
        }

        for (i in memoList.indices) {
            notifyItemChanged(i, "selectionModeChanged")
        }
        onItemSelectedCountChangeListener.onItemSelectedCountChange(selectionMap.count { it.value })
    }

    fun selectAll() {
        if (isSelectionMode) {
            changeAllSelections(true)
        }
    }

    fun unSelectAll() {
        if (isSelectionMode) {
            changeAllSelections(false)
        }
    }

    /**
     * 선택된 메모 리스트 반환
     */
    fun getSelectedMemoList(): List<MemoDataModel>? {
        return if (isSelectionMode) {
            memoList.filter { selectionMap[it] == true }
        } else {
            null
        }
    }

    /**
     * 뷰 갱신 처리 (리스트 추가/삭제 반영)
     */
    private fun viewUpdate(listCountDiff: Int) {
        if(listCountDiff > 0) {
            for (i in (memoList.count() + listCountDiff) downTo memoList.count()) {
                notifyItemRemoved(i)
            }
        }

        for (i in memoList.indices) {
            notifyItemChanged(i)
        }
    }

    /**
     * 체크박스 상태 토글 + 선택 맵 동기화
     */
    private fun toggleCheckBoxChecked(memo: MemoDataModel, checkBox: CheckBox) {
        checkBox.isChecked = !checkBox.isChecked
        selectionMap[memo] = checkBox.isChecked
        onItemSelectedCountChangeListener.onItemSelectedCountChange(selectionMap.count { it.value })
    }

    /**
     * 아이템 터치 및 롱클릭 이벤트 처리 + 애니메이션 효과 적용
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun viewBinding(root: View, memoItemView: View, checkBox: CheckBox, item: MemoDataModel) {
        var isLongPressed = false

        /* 선택모드(selection mode)에 따라 선택 checkBox가 보여지거나 안보여지도록 해줌
           selectionMap에 따라 checkBox의 checked 상태를 checked 또는 unchecked
           checkBox의 checked 상태는 selectionMap에 따라 변경됨
         */
        checkBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
        checkBox.isChecked = selectionMap[item] ?: false

        val overlay = ColorDrawable(Color.BLACK).apply {
            alpha = 0 // 초기엔 안 보이게
        }
        memoItemView.foreground = overlay

        root.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isLongPressed = false
                    // 어두워짐 애니메이션
                    ObjectAnimator.ofInt(overlay, "alpha", 0, 80).apply {
                        duration = 100
                        start()
                    }

                    // 작아지는 애니메이션
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start()
                }

                MotionEvent.ACTION_UP -> {
                    if (!isLongPressed) {
                        if (!isSelectionMode) {
                            onClickListener.onMemoClick(item, MemoClickType.ClickBody)
                        } else {
                            toggleCheckBoxChecked(item, checkBox)
                        }
                    }

                    // 밝아짐 애니메이션
                    ObjectAnimator.ofInt(overlay, "alpha", 80, 0).apply {
                        duration = 100
                        start()
                    }

                    // 원래대로 돌아오는 애니메이션
                    v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()

                }

                MotionEvent.ACTION_CANCEL -> {
                    // 밝아짐 애니메이션
                    ObjectAnimator.ofInt(overlay, "alpha", 80, 0).apply {
                        duration = 100
                        start()
                    }

                    // 원래대로 돌아오는 애니메이션
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false
        }

        root.setOnLongClickListener {
            isLongPressed = true
            if (!isSelectionMode) {
                goToSelectionMode()
                toggleCheckBoxChecked(item, checkBox)
                onClickListener.onMemoClick(item, MemoClickType.LongClick)
            } else {
                toggleCheckBoxChecked(item, checkBox)
            }
            return@setOnLongClickListener true
        }
    }

    /**
     * 리스트 뷰의 ViewHolder
     * 보기방식 = 목록
     */
    inner class ListViewHolder(private val binding: ListViewItemMemoListBinding) : RecyclerView.ViewHolder(binding.root) {
        val selectedCheckBox = binding.selectedCheckBox
        fun bind(item: MemoDataModel, position: Int) {
            with(binding) {
                itemChapter = item
                viewBinding(root, cardView, selectedCheckBox, item)
            }
        }
    }

    /**
     * 그리드 뷰의 ViewHolder
     * 보기방식 = 큰 격자, 작은 격자
     */
    inner class GridViewHolder(private val binding: GridViewItemMemoListBinding) : RecyclerView.ViewHolder(binding.root) {
        val selectedCheckBox = binding.selectedCheckBox
        fun bind(item: MemoDataModel, position: Int) {
            with(binding) {
                itemChapter = item
                viewBinding(root, cardView, selectedCheckBox, item)
            }
        }
    }
}