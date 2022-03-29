package com.example.currencyconverterapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.currencyconverterapp.R

@BindingAdapter("loadImage")
fun loadLoadingImage(imageView: ImageView, useless: Int){
    Glide.with(imageView.context)
        .load(R.drawable.loader_icon)
        .into(imageView)
}