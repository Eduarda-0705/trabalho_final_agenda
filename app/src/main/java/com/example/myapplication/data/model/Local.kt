package com.example.myapplication.data.model

data class Local(
    val id: Long,
    val nome: String,
    val cep: String,
    val rua: String,
    val numero: String,
    val complemento: String,
    val bairro: String,
    val cidade: String,
    val uf: String,
    val observacao: String
) {
    fun cepFormatado(): String = "${cep.substring(0, 5)}-${cep.substring(5)}"

    fun enderecoCompleto(): String {
        var endereco = "$cidade - $uf, ${cepFormatado()}"
        if (bairro.isNotBlank()) endereco = "$bairro, $endereco"
        if (rua.isNotBlank()) {
            val ruaComNumero = if (numero.isBlank()) rua else "$rua, $numero"
            endereco = "$ruaComNumero - $endereco"
        }
        return endereco
    }
}
