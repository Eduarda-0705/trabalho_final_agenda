package com.example.myapplication.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Login

@Serializable
data object Cadastro

@Serializable
data object Home

@Serializable
data object Programado

@Serializable
data class Lembretes(val listaId: Long = 0L)

@Serializable
data object NovoLembrete

@Serializable
data class EditarLembrete(val id: Long)

@Serializable
data object Listas

@Serializable
data object NovaLista

@Serializable
data class EditarLista(val id: Long)

@Serializable
data object Locais

@Serializable
data object NovoLocal

@Serializable
data class EditarLocal(val id: Long)

@Serializable
data object Usuarios

@Serializable
data object NovoUsuario

@Serializable
data class UsuarioEditar(val id: Long)
