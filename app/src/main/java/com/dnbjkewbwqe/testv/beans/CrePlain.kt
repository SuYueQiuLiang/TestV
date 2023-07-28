package com.dnbjkewbwqe.testv.beans


import androidx.annotation.Keep

@Keep
data class CrePlain(
    val cre_thful: Int,
    val cre_exotic: Int,
    val cre_cious: List<Foryu>,
    val cre_drab: List<Foryu>,
    val cre_easy: List<Foryu>,
    val cre_hesit: List<Foryu>,
    val cre_pen: List<Foryu>
) {
    @Keep
    data class Foryu(
        val cre_kind: String,
        val cre_mium: String,
        val cre_remi: String,
        val cre_violent: Int
    )
}