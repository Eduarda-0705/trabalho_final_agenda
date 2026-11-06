package com.example.myapplication.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.auth.SessaoUsuario
import com.example.myapplication.data.dal.ListaDao
import com.example.myapplication.data.model.Lista
import com.example.myapplication.validation.ListaValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaViewModel(application: Application): AndroidViewModel(application) {
    private val dao = ListaDao(application)
    var listas by mutableStateOf<List<Lista>>(emptyList())
        private set
    var lista by mutableStateOf<Lista?>(null)
        private set
    var nome by mutableStateOf("")
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
        val todas = withContext(Dispatchers.IO) { dao.listar(usuarioId) }
        listas = todas
    }

    fun buscarPorId(id: Long){
        if (lista?.id == id) return
        executar { usuarioId ->
            val encontrada = withContext(Dispatchers.IO) { dao.buscarPorId(id, usuarioId) }
            lista = requireNotNull(encontrada) { "Lista não encontrada." }
            nome = encontrada.nome
        }
    }

    fun mudarNome(valor: String){
        if (!carregando && valor.length <= 50) nome = valor
    }

    fun salvar() = executar { usuarioId ->
        val nomeLimpo = nome.trim()

        ListaValidator.validar(nomeLimpo)
        withContext(Dispatchers.IO) {
            val selecionada = lista
            if (selecionada == null){
                dao.cadastrar(usuarioId, nomeLimpo)
            } else {
                dao.alterar(selecionada.id, usuarioId, nomeLimpo)
            }
        }
        salvo = true
    }

    fun excluir(id: Long) = executar { usuarioId ->
        withContext(Dispatchers.IO) { dao.excluir(id, usuarioId) }
        listas = listas.filterNot { it.id == id }
        mensagem = "Lista excluída."
    }

    fun consumirSalvo(){
        salvo = false
    }
}
