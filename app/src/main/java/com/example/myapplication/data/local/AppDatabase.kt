package com.example.myapplication.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File

internal class AppDatabase(context: Context): SQLiteOpenHelper(
    context.applicationContext, File(context.noBackupFilesDir,
        "noted.db").absolutePath,
    null, 2
){
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE usuarios(
            id INTEGER PRIMARY KEY,
            nome TEXT NOT NULL CHECK(length(nome) BETWEEN 3 AND 100),
            email TEXT NOT NULL UNIQUE CHECK(length(email) BETWEEN 3 AND 254),
            senha BLOB NOT NULL,
            salt BLOB NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE listas(
            id INTEGER PRIMARY KEY,
            usuario_id INTEGER NOT NULL,
            nome TEXT NOT NULL CHECK(length(nome) BETWEEN 1 AND 50)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE locais(
            id INTEGER PRIMARY KEY,
            usuario_id INTEGER NOT NULL,
            nome TEXT NOT NULL CHECK(length(nome) BETWEEN 1 AND 60),
            cep TEXT NOT NULL CHECK(length(cep) = 8),
            rua TEXT NOT NULL CHECK(length(rua) <= 120),
            numero TEXT NOT NULL CHECK(length(numero) <= 10),
            complemento TEXT NOT NULL CHECK(length(complemento) <= 60),
            bairro TEXT NOT NULL CHECK(length(bairro) <= 80),
            cidade TEXT NOT NULL CHECK(length(cidade) BETWEEN 1 AND 80),
            uf TEXT NOT NULL CHECK(length(uf) = 2),
            observacao TEXT NOT NULL CHECK(length(observacao) <= 300)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE lembretes(
            id INTEGER PRIMARY KEY,
            usuario_id INTEGER NOT NULL,
            lista_id INTEGER,
            local_id INTEGER,
            titulo TEXT NOT NULL CHECK(length(titulo) BETWEEN 1 AND 100),
            notas TEXT NOT NULL CHECK(length(notas) <= 500),
            data TEXT NOT NULL CHECK(length(data) <= 10),
            horario TEXT NOT NULL CHECK(length(horario) <= 5),
            sinalizado INTEGER NOT NULL DEFAULT 0,
            concluido INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS lembretes")
        db.execSQL("DROP TABLE IF EXISTS locais")
        db.execSQL("DROP TABLE IF EXISTS listas")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

}
