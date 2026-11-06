package com.example.myapplication.ui.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.AzulMarinho
import com.example.myapplication.ui.theme.CinzaTexto
import com.example.myapplication.ui.theme.Terracota
import com.example.myapplication.ui.viewmodel.UsuarioViewModel

@Composable
fun UsuariosScreen(
    modifier: Modifier = Modifier,
    viewModel: UsuarioViewModel,
    irParaCadastro: () -> Unit,
    irParaEdicao: (Long) -> Unit,
    voltar: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.listar() }

    val atual = viewModel.usuarioAtivoId
    if (atual == null) {
        Text("Faça login para continuar.")
        return
    }
    var excluirId by rememberSaveable { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Usuários",
                    style = MaterialTheme.typography.titleLarge,
                    color = Terracota
                )
                Button(
                    onClick = irParaCadastro,
                    enabled = !viewModel.carregando,
                    colors = ButtonDefaults.buttonColors(containerColor = AzulMarinho)
                ) { Text("Novo usuário") }
                TextButton(
                    onClick = viewModel::listar,
                    enabled = !viewModel.carregando
                ) { Text(text = "Atualizar lista", color = AzulMarinho) }
                viewModel.mensagem?.let { Text(text = it, color = AzulMarinho) }
                if (viewModel.carregando) CircularProgressIndicator(color = Terracota)
                if (!viewModel.carregando && viewModel.usuarios.isEmpty())
                    Text(text = "Nenhum usuário encontrado.", color = CinzaTexto)
            }
        }
        items(viewModel.usuarios, key = { it.id }) { usuario ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = usuario.nome,
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulMarinho
                    )
                    Text(text = usuario.email, color = CinzaTexto)
                    if (usuario.id == atual) Text(text = "Você — usuário conectado", color = Terracota)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = { irParaEdicao(usuario.id) },
                            enabled = !viewModel.carregando
                        ) { Text(text = "Alterar", color = AzulMarinho) }
                        TextButton(
                            onClick = { excluirId = usuario.id },
                            enabled = !viewModel.carregando && usuario.id != atual
                        ) { Text("Excluir") }
                    }
                }
            }
        }
        item { TextButton(onClick = voltar) { Text(text = "Voltar", color = AzulMarinho) } }
    }

    val selecionado = viewModel.usuarios.firstOrNull { it.id == excluirId }
    if (selecionado != null && selecionado.id != atual) {
        AlertDialog(
            onDismissRequest = { excluirId = null },
            title = { Text("Excluir usuário?") },
            text = { Text("Excluir \"${selecionado.nome}\" e todos os seus lembretes? Esta ação não pode ser desfeita.") },
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
