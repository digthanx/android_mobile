package com.teamforce.thanksapp.presentation.adapter.challenge

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.GetChallengeContendersResponse
import com.teamforce.thanksapp.databinding.ItemContenderBinding
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.viewSinglePhoto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ContendersAdapter(
    private val applyClickListener: (reportId: Int, state: Char) -> Unit,
    private val refuseClickListener: (reportId: Int, state: Char) -> Unit,

): ListAdapter<GetChallengeContendersResponse.Contender, ContendersAdapter.ContenderViewHolder>(
    DiffCallback
)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContenderViewHolder {
        val binding = ItemContenderBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ContenderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object{
        const val TAG = "ContendersAdapter"

        object DiffCallback : DiffUtil.ItemCallback<GetChallengeContendersResponse.Contender>() {
            override fun areItemsTheSame(oldItem: GetChallengeContendersResponse.Contender, newItem: GetChallengeContendersResponse.Contender): Boolean {
                return oldItem.report_id == newItem.report_id
            }

            override fun areContentsTheSame(oldItem: GetChallengeContendersResponse.Contender, newItem: GetChallengeContendersResponse.Contender): Boolean {
                return oldItem == newItem
            }

        }
    }

   inner class ContenderViewHolder(val binding: ItemContenderBinding)
        : RecyclerView.ViewHolder(binding.root)


    override fun onBindViewHolder(holder: ContenderViewHolder, position: Int) {
        with(holder){
            if(!currentList[position].participant_photo.isNullOrEmpty()){
                Glide.with(holder.binding.root.context)
                    .load("${Consts.BASE_URL}${currentList[position].participant_photo}".toUri())
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(holder.binding.userAvatar)
            }
            if(!currentList[position].report_photo.isNullOrEmpty()){
                Glide.with(holder.binding.root.context)
                    .load("${Consts.BASE_URL}${currentList[position].report_photo}".toUri())
                    .centerCrop()
                    .into(holder.binding.image)
            }else{
                binding.showAttachedImgCard.visibility = View.GONE
            }
            binding.reportText.text = currentList[position].report_text
            binding.userNameLabelTv.text = currentList[position].participant_name
            binding.userSurnameLabelTv.text = currentList[position].participant_surname
            convertDateToNecessaryFormat(holder, position)
            binding.applyBtn.setOnClickListener {
                applyClickListener.invoke(currentList[position].report_id, 'W')
            }
            binding.refuseBtn.setOnClickListener {
                Log.d("ContendersAdapter", "Размер списка в адаптере " + currentList.size.toString())
                Log.d("ContendersAdapter", "Кликнутая позиция в адаптере " + position)
                refuseClickListener.invoke(currentList[position].report_id, 'D')
            }
            binding.image.setOnClickListener { view ->
                currentList[position].report_photo?.let { photo ->
                    (view as ImageView).viewSinglePhoto(photo, binding.root.context)
                }
            }
            binding.userAvatar.setOnClickListener { view ->
                Log.d("ContendersAdapter", "Размер списка в адаптере " + currentList.size.toString())
                Log.d("ContendersAdapter", "Кликнутая позиция в адаптере " + position)
//                currentList[position].participant_photo?.let { photo ->
//                    (view as ShapeableImageView).viewSinglePhoto(photo, binding.root.context)
//                }
            }
        }


    }

    private fun convertDateToNecessaryFormat(holder: ContenderViewHolder, position: Int) {
        try {
            val zdt: ZonedDateTime =
                ZonedDateTime.parse(currentList[position].report_created_at, DateTimeFormatter.ISO_DATE_TIME)
            val dateTime: String? =
                LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
            var date = dateTime?.subSequence(0, 10)
            val time = dateTime?.subSequence(11, 16)
            val today: LocalDate = LocalDate.now()
            val yesterday: String = today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
            val todayString =
                LocalDate.parse(today.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))
            val yesterdayString =
                LocalDate.parse(yesterday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))
            if (date == todayString) {
                date = "Сегодня"
            } else if (date == yesterdayString) {
                date = "Вчера"
            } else {
                date = date.toString()
            }
            holder.binding.dateTv.text =
                String.format(holder.binding.root.context.getString(R.string.dateTime), date, time)
        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
        }
    }



}