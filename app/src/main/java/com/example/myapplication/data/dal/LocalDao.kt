package com.example.myapplication.data.dal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.model.Local

internal class LocalDao(context: Context) {
    private val appContext = context.applicationContext

    private val colunas = arrayOf("id", "nome", "cep", "rua", "numero",
        "complemento", "bairro", "cidade", "uf", "observacao")

    private fun lerLocal(cursor: Cursor) = Local(cursor.getLong(0),
        cursor.getString(1),
        cursor.getString(2),
        cursor.getString(3),
        cursor.getString(4),
        cursor.getString(5),
        cursor.getString(6),
        cursor.getString(7),
        cursor.getString(8),
        cursor.getString(9))

    private fun montarDados(local: Local) = ContentValues().apply {
        put("nome", local.nome)
        put("cep", local.cep)
        put("rua", local.rua)
        put("numero", local.numero)
        put("complemento", local.complemento)
        put("bairro", local.bairro)
        put("cidade", local.cidade)
        put("uf", local.uf)
        put("observacao", local.observacao)
    }

    fun cadastrar(usuarioId: Long, local: Local){
        AppDatabase(appContext).use { db ->
            val dados = montarDados(local).apply {
                put("usuario_id", usuarioId)
            }
            db.writableDatabase
                .insertOrThrow("locais",
                    null,
                    dados)
        }
    }

    fun listar(usuarioId: Long): List<Local> = AppDatabase(appContext).use {
        db ->
        db.readableDatabase.query(
            "locais",
            colunas,
            "usuario_id = ?",
            arrayOf(usuarioId.toString()),
            null, null,
            "nome COLLATE NOCASE, id"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()){
                    add(lerLocal(cursor))
                }
            }
        }
    }

    fun buscarPorId(id: Long, usuarioId: Long): Local? = AppDatabase(appContext).use {
        db ->
        db.readableDatabase.query(
            "locais",
            colunas,
            "id = ? AND usuario_id = ?",
            arrayOf(id.toString(), usuarioId.toString()),
            null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) lerLocal(cursor)
            else null
        }
    }

    fun alterar(usuarioId: Long, local: Local){
        AppDatabase(appContext).use { db ->
            val alterados = db.writableDatabase.update(
                "locais",
                montarDados(local),
                "id = ? AND usuario_id = ?",
                arrayOf(local.id.toString(), usuarioId.toString()))
            require(alterados == 1) { "Erro ao alterar o local." }
        }
    }

    fun excluir(id: Long, usuarioId: Long){
        AppDatabase(appContext).use { db ->
            val excluidos = db.writableDatabase.delete(
                "locais", "id = ? AND usuario_id = ?",
                arrayOf(id.toString(), usuarioId.toString())
            )
            require(excluidos == 1) { "Local não pode ser excluído." }

            val semLocal = ContentValues().apply {
                putNull("local_id")
            }
            db.writableDatabase.update(
                "lembretes",
                semLocal,
                "local_id = ? AND usuario_id = ?",
                arrayOf(id.toString(), usuarioId.toString()))
        }
    }

}
