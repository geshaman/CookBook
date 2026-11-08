package ru.pnzgu.cookbook.data

import androidx.annotation.StringRes

data class Step(@StringRes val textRes: Int, val timerSeconds: Int? = null)
