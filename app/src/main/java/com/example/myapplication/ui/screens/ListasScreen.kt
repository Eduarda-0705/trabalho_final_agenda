package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.AzulMarinho
import com.example.myapplication.ui.theme.CinzaTexto
import com.example.myapplication.ui.theme.Terracota
import com.example.myapplication.ui.viewmodel.ListaViewModel

@Composable
fun ListasScreen(
    modifier: Modifier = Modifier,
    viewModel: ListaViewModel,
    irParaNovaLista: () -> Unit,
    irParaEdicao: (Long) -> Unit,
    irParaLembretes: (Long) -> Unit,
    voltar: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.listar() }

    var excluirId by rememberSaveable { mutableStateOf<Long?>(null) }

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
                text = "Listas",
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
                    if (!viewModel.carregando && viewModel.listas.isEmpty()) {
                        Text(text = "Nenhuma lista cadastrada.", color = CinzaTexto)
                    }
                }
            }

            items(viewModel.listas, key = { it.id }) { lista ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = lista.nome,
                            style = MaterialTheme.typography.titleMedium,
                            color = AzulMarinho
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(
                                onClick = { irParaLembretes(lista.id) },
                                enabled = !viewModel.carregando
                            ) { Text(text = "Ver lembretes", color = AzulMarinho) }
                            TextButton(
                                onClick = { irParaEdicao(lista.id) },
                                enabled = !viewModel.carregando
                            ) { Text(text = "Alterar", color = AzulMarinho) }
                            TextButton(
                                onClick = { excluirId = lista.id },
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
            onClick = irParaNovaLista,
            colors = ButtonDefaults.buttonColors(containerColor = AzulMarinho),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(text = "+ NOVA LISTA")
        }
    }

    val selecionada = viewModel.listas.firstOrNull { it.id == excluirId }
    if (selecionada != null) {
        AlertDialog(
            onDismissRequest = { excluirId = null },
            title = { Text("Excluir lista?") },
            text = { Text("Excluir \"${selecionada.nome}\"? Os lembretes desta lista não serão apagados, apenas ficarão sem lista.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        excluirId = null
                        viewModel.excluir(selecionada.id)
                    },
                    enabled = !viewModel.carregando
                ) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { excluirId = null }) { Text("Cancelar") } }
        )
    }
}
