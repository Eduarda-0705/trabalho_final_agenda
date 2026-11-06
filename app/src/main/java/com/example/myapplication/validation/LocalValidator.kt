package com.example.myapplication.validation

import com.example.myapplication.data.model.Local

object LocalValidator {
    fun validar(local: Local){
        require(local.nome.length in 1 .. 60){
            "Informe um nome de local entre 1 e 60 caracteres."
        }
        require(Regex("[0-9]{8}").matches(local.cep)){
            "Informe um CEP válido."
        }
        require(local.cidade.length in 1 .. 80){
            "Busque o CEP ou informe a cidade."
        }
        require(Regex("[A-Z]{2}").matches(local.uf)){
            "Informe a UF com 2 letras."
        }
        require(local.rua.length <= 120 && local.numero.length <= 10 &&
                local.complemento.length <= 60 && local.bairro.length <= 80){
            "Endereço com tamanho inválido."
        }
        require(local.observacao.length <= 300){
            "A observação pode ter no máximo 300 caracteres."
        }
    }
}
