package com.app.xit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.xit.R
import com.app.xit.userprofile.DriverHistoryModel


class HistoryAdapter(private val list: List<DriverHistoryModel>)
    : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(inflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder(inflater.inflate(R.layout.history_row, parent, false)){

        private var tvTitle: TextView? = null
        private var tvBookingId: TextView? = null
        private var tvDate: TextView? = null
        private var tvBookingStatus: TextView? = null
        private var tvPickLoc: TextView? = null
        private var tvDropLoc: TextView? = null

        init {
            tvTitle = itemView.findViewById(R.id.tv_name)
            tvBookingId = itemView.findViewById(R.id.tv_booking_id)
            tvDate = itemView.findViewById(R.id.tv_date)
            tvBookingStatus = itemView.findViewById(R.id.tv_status)
            tvPickLoc = itemView.findViewById(R.id.tv_pick_loc)
            tvDropLoc = itemView.findViewById(R.id.tv_drop_loc)
        }

        fun bind(historyModel: DriverHistoryModel){
            tvTitle?.text = historyModel.p_contact_name
            tvBookingId?.text = historyModel.order_number
            tvDate?.text = historyModel.date_and_time
            tvBookingStatus?.text = historyModel.status
            tvPickLoc?.text = historyModel.p_address
            tvDropLoc?.text = historyModel.d_address
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HistoryViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val model: DriverHistoryModel = list[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int = list.size

}
