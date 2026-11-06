package com.example.myapplication.validation

object ListaValidator {
    fun validar(nome: String){
        require(nome.length in 1 .. 50){
            "Informe um nome de lista entre 1 e 50 caracteres."
        }
    }
}
