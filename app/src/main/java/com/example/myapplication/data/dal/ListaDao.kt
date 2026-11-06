package com.example.myapplication.data.dal

import android.content.ContentValues
import android.content.Context
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.model.Lista

internal class ListaDao(context: Context) {
    private val appContext = context.applicationContext

    fun cadastrar(usuarioId: Long, nome: String){
        AppDatabase(appContext).use { db ->
            val dados = ContentValues().apply {
                put("usuario_id", usuarioId)
                put("nome", nome)
            }
            db.writableDatabase
                .insertOrThrow("listas",
                    null,
                    dados)
        }
    }

    fun listar(usuarioId: Long): List<Lista> = AppDatabase(appContext).use {
        db ->
        db.readableDatabase.query(
            "listas",
            arrayOf("id", "nome"),
            "usuario_id = ?",
            arrayOf(usuarioId.toString()),
            null, null,
            "nome COLLATE NOCASE, id"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()){
                    add(Lista(cursor.getLong(0),
                        cursor.getString(1)))
                }
            }
        }
    }

    fun buscarPorId(id: Long, usuarioId: Long): Lista? = AppDatabase(appContext).use {
        db ->
        db.readableDatabase.query(
            "listas",
            arrayOf("id", "nome"),
            "id = ? AND usuario_id = ?",
            arrayOf(id.toString(), usuarioId.toString()),
            null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) Lista(cursor.getLong(0),
                cursor.getString(1))
            else null
        }
    }

    fun alterar(id: Long, usuarioId: Long, nome: String){
        AppDatabase(appContext).use { db ->
            val dados = ContentValues().apply {
                put("nome", nome)
            }
            val alterados = db.writableDatabase.update(
                "listas",
                dados,
                "id = ? AND usuario_id = ?",
                arrayOf(id.toString(), usuarioId.toString()))
            require(alterados == 1) { "Erro ao alterar a lista." }
        }
    }

    fun excluir(id: Long, usuarioId: Long){
        AppDatabase(appContext).use { db ->
            val excluidos = db.writableDatabase.delete(
                "listas", "id = ? AND usuario_id = ?",
                arrayOf(id.toString(), usuarioId.toString())
            )
            require(excluidos == 1) { "Lista não pode ser excluída." }

            val semLista = ContentValues().apply {
                putNull("lista_id")
            }
            db.writableDatabase.update(
                "lembretes",
                semLista,
                "lista_id = ? AND usuario_id = ?",
                arrayOf(id.toString(), usuarioId.toString()))
        }
    }

}
