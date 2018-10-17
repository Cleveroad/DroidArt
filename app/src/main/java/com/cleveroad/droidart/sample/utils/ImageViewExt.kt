package com.cleveroad.droidart.sample.utils

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImage(path: String) = Glide.with(context).load(path).into(this)!!