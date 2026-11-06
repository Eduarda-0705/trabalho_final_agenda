package com.example.myapplication.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.auth.SessaoUsuario
import com.example.myapplication.data.dal.LembreteDao
import com.example.myapplication.data.dal.ListaDao
import com.example.myapplication.data.dal.LocalDao
import com.example.myapplication.data.model.Lembrete
import com.example.myapplication.data.model.Lista
import com.example.myapplication.data.model.Local
import com.example.myapplication.validation.LembreteValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LembreteViewModel(application: Application): AndroidViewModel(application) {
    private val dao = LembreteDao(application)
    private val listaDao = ListaDao(application)
    private val localDao = LocalDao(application)
    var lembretes by mutableStateOf<List<Lembrete>>(emptyList())
        private set
    var listas by mutableStateOf<List<Lista>>(emptyList())
        private set
    var locais by mutableStateOf<List<Local>>(emptyList())
        private set
    var lembrete by mutableStateOf<Lembrete?>(null)
        private set
    var titulo by mutableStateOf("")
        private set
    var notas by mutableStateOf("")
        private set
    var data by mutableStateOf("")
        private set
    var horario by mutableStateOf("")
        private set
    var sinalizado by mutableStateOf(false)
        private set
    var listaId by mutableStateOf<Long?>(null)
        private set
    var localId by mutableStateOf<Long?>(null)
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

    private suspend fun carregarListasELocais(usuarioId: Long){
        val todasListas = withContext(Dispatchers.IO) { listaDao.listar(usuarioId) }
        val todosLocais = withContext(Dispatchers.IO) { localDao.listar(usuarioId) }
        listas = todasListas
        locais = todosLocais
    }

    fun listar() = executar { usuarioId ->
        val lista = withContext(Dispatchers.IO) { dao.listar(usuarioId) }
        carregarListasELocais(usuarioId)
        lembretes = lista
    }

    fun carregarOpcoes() = executar { usuarioId ->
        carregarListasELocais(usuarioId)
    }

    fun buscarPorId(id: Long){
        if (lembrete?.id == id) return
        executar { usuarioId ->
            val encontrado = withContext(Dispatchers.IO) { dao.buscarPorId(id, usuarioId) }
            carregarListasELocais(usuarioId)
            lembrete = requireNotNull(encontrado) { "Lembrete não encontrado." }
            titulo = encontrado.titulo
            notas = encontrado.notas
            data = encontrado.data
            horario = encontrado.horario
            sinalizado = encontrado.sinalizado
            listaId = encontrado.listaId
            localId = encontrado.localId
        }
    }

    fun mudarTitulo(valor: String){
        if (!carregando && valor.length <= 100) titulo = valor
    }

    fun mudarNotas(valor: String){
        if (!carregando && valor.length <= 500) notas = valor
    }

    fun mudarData(valor: String){
        if (!carregando && valor.length <= 10) data = valor
    }

    fun mudarHorario(valor: String){
        if (!carregando && valor.length <= 5) horario = valor
    }

    fun alternarSinalizado(){
        if (!carregando) sinalizado = !sinalizado
    }

    fun mudarLista(id: Long?){
        if (!carregando) listaId = id
    }

    fun mudarLocal(id: Long?){
        if (!carregando) localId = id
    }

    fun salvar() = executar { usuarioId ->
        val tituloLimpo = titulo.trim()
        val notasLimpas = notas.trim()
        val dataLimpa = data.trim()
        val horarioLimpo = horario.trim()

        LembreteValidator.validar(tituloLimpo, notasLimpas, dataLimpa, horarioLimpo)
        val listaValida = listaId == null || listas.firstOrNull { it.id == listaId } != null
        val localValido = localId == null || locais.firstOrNull { it.id == localId } != null
        require(listaValida && localValido) { "Escolha uma lista e um local válidos." }
        withContext(Dispatchers.IO) {
            val selecionado = lembrete
            if (selecionado == null){
                dao.cadastrar(usuarioId, tituloLimpo, notasLimpas,
                    dataLimpa, horarioLimpo, sinalizado, listaId, localId)
            } else {
                dao.alterar(selecionado.id, usuarioId, tituloLimpo, notasLimpas,
                    dataLimpa, horarioLimpo, sinalizado, listaId, localId)
            }
        }
        salvo = true
    }

    fun alternarConcluido(item: Lembrete) = executar { usuarioId ->
        val novoValor = !item.concluido
        withContext(Dispatchers.IO) { dao.alterarConcluido(item.id, usuarioId, novoValor) }
        lembretes = lembretes.map {
            if (it.id == item.id) it.copy(concluido = novoValor) else it
        }
    }

    fun excluir(id: Long) = executar { usuarioId ->
        withContext(Dispatchers.IO) { dao.excluir(id, usuarioId) }
        lembretes = lembretes.filterNot { it.id == id }
        mensagem = "Lembrete excluído."
    }

    fun consumirSalvo(){
        salvo = false
    }
}
