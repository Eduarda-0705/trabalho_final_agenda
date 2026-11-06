package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun NovoLembreteScreen(
    modifier: Modifier = Modifier,
    lembreteId: Long? = null,
    viewModel: LembreteViewModel,
    voltar: () -> Unit
) {
    LaunchedEffect(lembreteId) {
        if (lembreteId != null) viewModel.buscarPorId(lembreteId)
        else viewModel.carregarOpcoes()
    }
    LaunchedEffect(viewModel.salvo) {
        if (viewModel.salvo) {
            viewModel.consumirSalvo()
            voltar()
        }
    }

    Column(modifier = modifier.fillMaxSize().imePadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Cancelar",
                tint = CinzaTexto,
                modifier = Modifier
                    .clickable { voltar() }
                    .padding(12.dp)
                    .size(24.dp)
            )
            Text(
                text = if (lembreteId == null) "Novo Lembrete" else "Alterar Lembrete",
                style = MaterialTheme.typography.titleLarge,
                color = Terracota,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = viewModel::salvar,
                enabled = viewModel.titulo.isNotBlank() && !viewModel.carregando && !viewModel.salvo,
                colors = ButtonDefaults.buttonColors(containerColor = AzulMarinho),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = "Salvar")
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            viewModel.mensagem?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            if (viewModel.carregando) CircularProgressIndicator(color = Terracota)

            SecaoTitulo(texto = "Nota")
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = viewModel.titulo,
                        onValueChange = viewModel::mudarTitulo,
                        label = { Text("Título") },
                        leadingIcon = {
                            Icon(painterResource(id = R.drawable.ic_note_add), contentDescription = null)
                        },
                        singleLine = true,
                        enabled = !viewModel.carregando,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.notas,
                        onValueChange = viewModel::mudarNotas,
                        label = { Text("Notas") },
                        leadingIcon = {
                            Icon(painterResource(id = R.drawable.ic_notes), contentDescription = null)
                        },
                        enabled = !viewModel.carregando,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            SecaoTitulo(texto = "Detalhes")
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = viewModel.data,
                        onValueChange = viewModel::mudarData,
                        label = { Text("Data (dd/mm/aaaa)") },
                        leadingIcon = {
                            Icon(painterResource(id = R.drawable.ic_calendar), contentDescription = null)
                        },
                        singleLine = true,
                        enabled = !viewModel.carregando,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.horario,
                        onValueChange = viewModel::mudarHorario,
                        label = { Text("Hora (hh:mm)") },
                        leadingIcon = {
                            Icon(painterResource(id = R.drawable.ic_clock), contentDescription = null)
                        },
                        singleLine = true,
                        enabled = !viewModel.carregando,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Preencha data ou hora para o lembrete aparecer em Programado.",
                        color = CinzaTexto,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            SecaoTitulo(texto = "Lista")
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OpcaoSelecao(
                        texto = "Sem lista",
                        selecionada = viewModel.listaId == null,
                        onClick = { viewModel.mudarLista(null) }
                    )
                    viewModel.listas.forEach { lista ->
                        OpcaoSelecao(
                            texto = lista.nome,
                            selecionada = viewModel.listaId == lista.id,
                            onClick = { viewModel.mudarLista(lista.id) }
                        )
                    }
                }
            }

            SecaoTitulo(texto = "Local")
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OpcaoSelecao(
                        texto = "Sem local",
                        selecionada = viewModel.localId == null,
                        onClick = { viewModel.mudarLocal(null) }
                    )
                    viewModel.locais.forEach { local ->
                        OpcaoSelecao(
                            texto = local.nome,
                            selecionada = viewModel.localId == local.id,
                            onClick = { viewModel.mudarLocal(local.id) }
                        )
                    }
                }
            }

            SecaoTitulo(texto = "Opções")
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LinhaAlternar(
                        icone = R.drawable.ic_flag,
                        texto = "Sinalizar",
                        ativado = viewModel.sinalizado,
                        onClick = viewModel::alternarSinalizado
                    )
                }
            }
        }
    }
}

@Composable
private fun SecaoTitulo(texto: String) {
    Text(
        text = texto,
        color = Terracota,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun LinhaAlternar(icone: Int, texto: String, ativado: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icone),
            contentDescription = null,
            tint = Terracota,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = texto,
            color = AzulMarinho,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        )
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (ativado) AzulMarinho else CinzaTexto
            )
        ) {
            Text(text = if (ativado) "Ativado" else "Desativado")
        }
    }
}

@Composable
private fun OpcaoSelecao(texto: String, selecionada: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selecionada) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check_circle),
                contentDescription = null,
                tint = Terracota,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Surface(
                modifier = Modifier.size(20.dp),
                color = CinzaClaro
            ) {}
        }
        Text(
            text = texto,
            color = AzulMarinho,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
