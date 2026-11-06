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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.AzulMarinho
import com.example.myapplication.ui.theme.CinzaTexto
import com.example.myapplication.ui.theme.Terracota
import com.example.myapplication.ui.viewmodel.LocalViewModel

@Composable
fun LocaisScreen(
    modifier: Modifier = Modifier,
    viewModel: LocalViewModel,
    irParaNovoLocal: () -> Unit,
    irParaEdicao: (Long) -> Unit,
    voltar: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.listar() }

    var excluirId by rememberSaveable { mutableStateOf<Long?>(null) }
    val contexto = LocalContext.current

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
                text = "Locais",
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
                    if (!viewModel.carregando && viewModel.locais.isEmpty()) {
                        Text(text = "Nenhum local cadastrado.", color = CinzaTexto)
                    }
                }
            }

            items(viewModel.locais, key = { it.id }) { local ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = local.nome,
                            style = MaterialTheme.typography.titleMedium,
                            color = AzulMarinho
                        )
                        Text(
                            text = local.enderecoCompleto(),
                            color = CinzaTexto,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (local.complemento.isNotBlank()) {
                            Text(
                                text = "Complemento: ${local.complemento}",
                                color = CinzaTexto,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (local.observacao.isNotBlank()) {
                            Text(text = local.observacao, color = CinzaTexto)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(
                                onClick = { abrirMapa(contexto, local.enderecoCompleto()) }
                            ) { Text(text = "Abrir no mapa", color = AzulMarinho) }
                            TextButton(
                                onClick = { irParaEdicao(local.id) },
                                enabled = !viewModel.carregando
                            ) { Text(text = "Alterar", color = AzulMarinho) }
                            TextButton(
                                onClick = { excluirId = local.id },
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
            onClick = irParaNovoLocal,
            colors = ButtonDefaults.buttonColors(containerColor = AzulMarinho),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(text = "+ NOVO LOCAL")
        }
    }

    val selecionado = viewModel.locais.firstOrNull { it.id == excluirId }
    if (selecionado != null) {
        AlertDialog(
            onDismissRequest = { excluirId = null },
            title = { Text("Excluir local?") },
            text = { Text("Excluir \"${selecionado.nome}\"? Os lembretes ligados a este local não serão apagados, apenas ficarão sem local.") },
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
