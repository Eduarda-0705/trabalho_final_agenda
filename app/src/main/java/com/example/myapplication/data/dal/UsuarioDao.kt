package com.example.myapplication.data.dal

import android.content.ContentValues
import android.content.Context
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.model.Usuario

internal class SenhaSalva(val hash: ByteArray, val salt: ByteArray, val usuarioID: Long)

internal class UsuarioDao(context: Context) {
    private val appContext = context.applicationContext

    fun cadastrar(nome: String,
                  email: String,
                  senha: ByteArray,
                  salt: ByteArray
    ){
        AppDatabase(appContext).use { db ->
            val dados = ContentValues().apply {
                put("nome", nome)
                put("email", email)
                put("senha", senha)
                put("salt", salt)
            }
            db.writableDatabase
                .insertOrThrow("usuarios",
                    null,
                    dados)
        }
    }

    fun buscarSenha(email: String): SenhaSalva?{
        return AppDatabase(appContext).use { db ->
            db.readableDatabase.query(
                "usuarios",
                arrayOf("senha", "salt", "id"),
                "email = ?",
                arrayOf(email),
                null, null, null
            ).use { usuario ->
                if (usuario.moveToFirst()) SenhaSalva(
                    usuario.getBlob(0),
                    usuario.getBlob(1), usuario.getLong(2)
                ) else null
            }
        }
    }

    fun listar(): List<Usuario> = AppDatabase(appContext).use {
        db ->
        db.readableDatabase.query(
            "usuarios",
            arrayOf("id", "nome", "email"),
            null, null, null, null,
            "nome COLLATE NOCASE, id"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()){
                    add(Usuario(cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2)))
                }
            }
        }
    }

    fun buscarPorId(id: Long): Usuario? = AppDatabase(appContext).use {
        db ->
        db.readableDatabase.query(
            "usuarios",
            arrayOf("id", "nome", "email"),
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) Usuario(cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2))
            else null
        }
    }

    fun alterar(id: Long, nome: String,
                hash: ByteArray? = null,
                salt: ByteArray? = null){
        AppDatabase(appContext).use { db ->
            val dados = ContentValues().apply {
                put("nome", nome)
                if (hash != null) put("senha", hash)
                if (salt != null) put("salt", salt)
            }
            val alterados = db.writableDatabase.update(
                "usuarios",
                dados,
                "id = ?",
                arrayOf(id.toString()))
            require(alterados == 1) { "Erro ao alterar o usuário." }
        }
    }

    fun excluir(id: Long, usuarioAtualId: Long){
        require(id != usuarioAtualId) { "Você não pode excluir seu próprio usuário." }

        AppDatabase(appContext).use { db ->
            val excluidos = db.writableDatabase.delete(
                "usuarios", "id = ? AND id <> ?",
                arrayOf(id.toString(), usuarioAtualId.toString())
            )
            require(excluidos == 1) { "Usuário não pode ser excluído." }
            db.writableDatabase.delete(
                "lembretes", "usuario_id = ?",
                arrayOf(id.toString())
            )
        }
    }

}
