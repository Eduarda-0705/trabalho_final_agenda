package com.example.myapplication.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun abrirMapa(contexto: Context, endereco: String) {
    val mapa = Intent(Intent.ACTION_VIEW,
        Uri.parse("geo:0,0?q=${Uri.encode(endereco)}"))
    try {
        contexto.startActivity(mapa)
    } catch (e: Exception) {
        Toast.makeText(contexto, "Não foi possível abrir o app de mapas.", Toast.LENGTH_LONG).show()
    }
}
