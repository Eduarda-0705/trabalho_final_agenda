package com.example.myapplication.data.model

data class Lembrete(
    val id: Long,
    val titulo: String,
    val notas: String,
    val data: String,
    val horario: String,
    val sinalizado: Boolean,
    val concluido: Boolean,
    val listaId: Long?,
    val localId: Long?
)
