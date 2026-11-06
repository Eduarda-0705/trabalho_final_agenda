package com.example.myapplication.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.auth.SessaoUsuario
import com.example.myapplication.data.dal.LocalDao
import com.example.myapplication.data.model.Local
import com.example.myapplication.data.remote.Endereco
import com.example.myapplication.data.remote.ViaCepApi
import com.example.myapplication.validation.LocalValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocalViewModel(application: Application): AndroidViewModel(application) {
    private val dao = LocalDao(application)
    var locais by mutableStateOf<List<Local>>(emptyList())
        private set
    var local by mutableStateOf<Local?>(null)
        private set
    var nome by mutableStateOf("")
        private set
    var cep by mutableStateOf("")
        private set
    var rua by mutableStateOf("")
        private set
    var numero by mutableStateOf("")
        private set
    var complemento by mutableStateOf("")
        private set
    var bairro by mutableStateOf("")
        private set
    var cidade by mutableStateOf("")
        private set
    var uf by mutableStateOf("")
        private set
    var observacao by mutableStateOf("")
        private set
    var carregando by mutableStateOf(false)
        private set
    var salvo by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set

    private fun executar(op: suspend (Long) -> Unit){
        if (carregando || salvo) return
        carregando = true
        mensagem = null
        viewModelScope.launch {
            try {
                val usuarioId = SessaoUsuario.exigirUsuario()
                op(usuarioId)
            } catch (e: CancellationException){
                throw e
            } catch (e: IllegalArgumentException){
                mensagem = e.message
            } catch (e: Exception){
                mensagem = "Um erro inesperado aconteceu."
            } finally {
                carregando = false
            }
        }
    }

    fun listar() = executar { usuarioId ->
        val todos = withContext(Dispatchers.IO) { dao.listar(usuarioId) }
        locais = todos
    }

    fun buscarPorId(id: Long){
        if (local?.id == id) return
        executar { usuarioId ->
            val encontrado = withContext(Dispatchers.IO) { dao.buscarPorId(id, usuarioId) }
            local = requireNotNull(encontrado) { "Local não encontrado." }
            nome = encontrado.nome
            cep = encontrado.cepFormatado()
            rua = encontrado.rua
            numero = encontrado.numero
            complemento = encontrado.complemento
            bairro = encontrado.bairro
            cidade = encontrado.cidade
            uf = encontrado.uf
            observacao = encontrado.observacao
        }
    }

    fun mudarNome(valor: String){
        if (!carregando && valor.length <= 60) nome = valor
    }

    fun mudarCep(valor: String){
        if (!carregando && valor.length <= 9) cep = valor
    }

    fun mudarRua(valor: String){
        if (!carregando && valor.length <= 120) rua = valor
    }

    fun mudarNumero(valor: String){
        if (!carregando && valor.length <= 10) numero = valor
    }

    fun mudarComplemento(valor: String){
        if (!carregando && valor.length <= 60) complemento = valor
    }

    fun mudarBairro(valor: String){
        if (!carregando && valor.length <= 80) bairro = valor
    }

    fun mudarCidade(valor: String){
        if (!carregando && valor.length <= 80) cidade = valor
    }

    fun mudarUf(valor: String){
        if (!carregando && valor.length <= 2) uf = valor.uppercase()
    }

    fun mudarObservacao(valor: String){
        if (!carregando && valor.length <= 300) observacao = valor
    }

    fun buscarCep(){
        if (carregando) return
        mensagem = null

        if (!Regex("[0-9]{5}-?[0-9]{3}").matches(cep)){
            mensagem = "Informe um CEP válido."
            return
        }
        val cepConsulta = cep.replace("-", "")
        carregando = true
        viewModelScope.launch {
            try {
                val resposta = ViaCepApi.service.buscar(cepConsulta)
                when {
                    resposta.erro == true -> mensagem = "CEP não encontrado."
                    resposta.cep.isNullOrBlank() -> mensagem = "Resposta inválida."
                    else -> preencherEndereco(resposta)
                }
            } catch (e: CancellationException){
                throw e
            } catch (e: Exception){
                mensagem = "Não foi possível consultar o CEP."
            } finally {
                carregando = false
            }
        }
    }

    private fun preencherEndereco(endereco: Endereco){
        rua = endereco.rua.orEmpty().take(120)
        complemento = endereco.complemento.orEmpty().take(60)
        bairro = endereco.bairro.orEmpty().take(80)
        cidade = endereco.cidade.orEmpty().take(80)
        uf = endereco.uf.orEmpty().take(2)
        mensagem = "Endereço encontrado. Confira e informe o número."
    }

    fun salvar() = executar { usuarioId ->
        val dados = Local(
            local?.id ?: 0L,
            nome.trim(),
            cep.replace("-", "").trim(),
            rua.trim(),
            numero.trim(),
            complemento.trim(),
            bairro.trim(),
            cidade.trim(),
            uf.trim().uppercase(),
            observacao.trim()
        )

        LocalValidator.validar(dados)
        withContext(Dispatchers.IO) {
            if (local == null){
                dao.cadastrar(usuarioId, dados)
            } else {
                dao.alterar(usuarioId, dados)
            }
        }
        salvo = true
    }

    fun excluir(id: Long) = executar { usuarioId ->
        withContext(Dispatchers.IO) { dao.excluir(id, usuarioId) }
        locais = locais.filterNot { it.id == id }
        mensagem = "Local excluído."
    }

    fun consumirSalvo(){
        salvo = false
    }
}
