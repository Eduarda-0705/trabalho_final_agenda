package com.example.myapplication.validation

object LembreteValidator {
    fun validar(titulo: String, notas: String, data: String, horario: String){
        require(titulo.length in 1 .. 100){
            "Informe um título entre 1 e 100 caracteres."
        }
        require(notas.length <= 500){
            "As notas podem ter no máximo 500 caracteres."
        }
        require(data.length <= 10){
            "Informe a data no formato dd/mm/aaaa."
        }
        require(horario.length <= 5){
            "Informe a hora no formato hh:mm."
        }
    }
}
