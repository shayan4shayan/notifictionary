package ir.shahinsoft.notifictionary.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.CategoryAdapter
import ir.shahinsoft.notifictionary.databinding.DialogPickBoardBinding
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.tasks.BoardsLoaderTask

class PickBoardDialog(context:Context, val callback : (Board)->Unit) : AlertDialog(context){
    lateinit var binding : DialogPickBoardBinding
    override fun show() {
        super.show()
        binding = DialogPickBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadBoards()
    }

    private fun loadBoards() {
        BoardsLoaderTask(context.getAppDatabase()){
            displayBoards(it)
        }.execute()
    }

    private fun displayBoards(boards:List<Board>) {
        binding.recycler.adapter = PickBoardAdapter(ArrayList(boards)) {
            callback(it)
            dismiss()
        }
    }
}

class PickBoardAdapter(val boards: List<Board>,val onBoardSelected: (Board)-> Unit) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){
    override fun getItemViewType(position: Int): Int {
        return ((position + 1) / 2) % 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        return CategoryAdapter.ViewHolder(
                if (viewType == 0) {
                    LayoutInflater.from(parent.context).inflate(R.layout.item_pick_category_primary, parent, false)
                } else {
                    LayoutInflater.from(parent.context).inflate(R.layout.item_pick_category_secondary, parent, false)
                }
        ).apply {
            itemView.setOnClickListener {
                onBoardSelected(boards[adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val board = boards[position]
        holder.title.text = board.name
        holder.leanredCount.text = "${board.learnedCount}"
        holder.totalCount.text = "${board.totalCount}"
        var progress = board.learnedCount.toFloat() / board.totalCount.toFloat()
        if (board.totalCount ==0){
            progress = 0f
        }
        if (progress < 0.33) {
            holder.image.setImageResource(R.drawable.ic_boards_learning)
        } else if (progress >= 0.33 && progress < 0.66) {
            holder.image.setImageResource(R.drawable.ic_boards_learned_half)
        } else {
            holder.image.setImageResource(R.drawable.ic_board_learned)
        }
    }

    override fun getItemCount() = boards.size

}