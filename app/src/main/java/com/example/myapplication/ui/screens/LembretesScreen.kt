package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.AzulMarinho
import com.example.myapplication.ui.theme.CinzaClaro
import com.example.myapplication.ui.theme.CinzaTexto
import com.example.myapplication.ui.theme.Terracota
import com.example.myapplication.ui.viewmodel.LembreteViewModel

@Composable
fun LembretesScreen(
    modifier: Modifier = Modifier,
    listaId: Long,
    viewModel: LembreteViewModel,
    irParaNovoLembrete: () -> Unit,
    irParaEdicao: (Long) -> Unit,
    voltar: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.listar() }

    var excluirId by rememberSaveable { mutableStateOf<Long?>(null) }
    val contexto = LocalContext.current

    val exibidos = if (listaId == 0L) viewModel.lembretes
    else viewModel.lembretes.filter { it.listaId == listaId }
    val titulo = if (listaId == 0L) "Lembretes"
    else viewModel.listas.firstOrNull { it.id == listaId }?.nome ?: "Lembretes"

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Voltar",
                tint = Terracota,
                modifier = Modifier
                    .clickable { voltar() }
                    .padding(12.dp)
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                color = Terracota
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = viewModel::listar,
                        enabled = !viewModel.carregando
                    ) { Text(text = "Atualizar lista", color = AzulMarinho) }
                    viewModel.mensagem?.let { Text(text = it, color = AzulMarinho) }
                    if (viewModel.carregando) CircularProgressIndicator(color = Terracota)
                    if (!viewModel.carregando && exibidos.isEmpty()) {
                        Text(text = "Nenhum lembrete cadastrado.", color = CinzaTexto)
                    }
                }
            }

            items(exibidos, key = { it.id }) { lembrete ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (lembrete.concluido) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_check_circle),
                                    contentDescription = "Concluído",
                                    tint = Terracota,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.size(20.dp),
                                    color = CinzaClaro
                                ) {}
                            }
                            Text(
                                text = lembrete.titulo,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (lembrete.sinalizado) Terracota else AzulMarinho,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .weight(1f)
                            )
                            if (lembrete.sinalizado) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_flag),
                                    contentDescription = "Sinalizado",
                                    tint = Terracota,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        if (lembrete.notas.isNotEmpty()) {
                            Text(text = lembrete.notas, color = CinzaTexto)
                        }
                        if (lembrete.data.isNotEmpty() || lembrete.horario.isNotEmpty()) {
                            Text(
                                text = "${lembrete.data} ${lembrete.horario}".trim(),
                                color = CinzaTexto,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        val nomeLista = viewModel.listas.firstOrNull { it.id == lembrete.listaId }?.nome
                        if (nomeLista != null) {
                            Text(
                                text = "Lista: $nomeLista",
                                color = CinzaTexto,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        val local = viewModel.locais.firstOrNull { it.id == lembrete.localId }
                        if (local != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Local: ${local.nome}",
                                    color = CinzaTexto,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { abrirMapa(contexto, local.enderecoCompleto()) }) {
                                    Text(text = "Abrir no mapa", color = AzulMarinho)
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = { viewModel.alternarConcluido(lembrete) },
                                enabled = !viewModel.carregando
                            ) {
                                Text(
                                    text = if (lembrete.concluido) "Reabrir" else "Concluir",
                                    color = AzulMarinho
                                )
                            }
                            TextButton(
                                onClick = { irParaEdicao(lembrete.id) },
                                enabled = !viewModel.carregando
                            ) { Text(text = "Alterar", color = AzulMarinho) }
                            TextButton(
                                onClick = { excluirId = lembrete.id },
                                enabled = !viewModel.carregando
                            ) {
                                Text(
                                    text = "Excluir",
                                    color = Terracota,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = irParaNovoLembrete,
            colors = ButtonDefaults.buttonColors(containerColor = AzulMarinho),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(text = "+ NOVO LEMBRETE")
        }
    }

    val selecionado = viewModel.lembretes.firstOrNull { it.id == excluirId }
    if (selecionado != null) {
        AlertDialog(
            onDismissRequest = { excluirId = null },
            title = { Text("Excluir lembrete?") },
            text = { Text("Excluir \"${selecionado.titulo}\"? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        excluirId = null
                        viewModel.excluir(selecionado.id)
                    },
                    enabled = !viewModel.carregando
                ) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { excluirId = null }) { Text("Cancelar") } }
        )
    }
}
