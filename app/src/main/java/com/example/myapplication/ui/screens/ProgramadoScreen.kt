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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.myapplication.data.model.Lembrete
import com.example.myapplication.ui.theme.AzulMarinho
import com.example.myapplication.ui.theme.CinzaClaro
import com.example.myapplication.ui.theme.CinzaTexto
import com.example.myapplication.ui.theme.Terracota
import com.example.myapplication.ui.viewmodel.LembreteViewModel

@Composable
fun ProgramadoScreen(
    modifier: Modifier = Modifier,
    viewModel: LembreteViewModel,
    voltar: () -> Unit,
    irParaNovoLembrete: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.listar() }

    val programados = viewModel.lembretes.filter { it.data.isNotEmpty() || it.horario.isNotEmpty() }
    val pendentes = programados.filter { !it.concluido }
    val concluidos = programados.filter { it.concluido }

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
                text = "Programado",
                style = MaterialTheme.typography.titleLarge,
                color = Terracota
            )
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            color = Color.White
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        viewModel.mensagem?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                        if (viewModel.carregando) CircularProgressIndicator(color = Terracota)
                        if (!viewModel.carregando && programados.isEmpty()) {
                            Text(text = "Nenhum lembrete programado.", color = CinzaTexto)
                        }
                    }
                }

                if (pendentes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Pendentes",
                            color = Terracota,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(pendentes, key = { it.id }) { lembrete ->
                        LinhaLembrete(
                            lembrete = lembrete,
                            onClick = { viewModel.alternarConcluido(lembrete) }
                        )
                    }
                }

                if (concluidos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Concluídos",
                            color = AzulMarinho,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                    }
                    items(concluidos, key = { it.id }) { lembrete ->
                        LinhaLembrete(
                            lembrete = lembrete,
                            onClick = { viewModel.alternarConcluido(lembrete) }
                        )
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
}

@Composable
private fun LinhaLembrete(lembrete: Lembrete, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (lembrete.concluido) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check_circle),
                contentDescription = null,
                tint = Terracota,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Surface(
                modifier = Modifier.size(20.dp),
                color = CinzaClaro
            ) {}
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = lembrete.titulo,
                color = if (lembrete.sinalizado) Terracota else AzulMarinho,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${lembrete.data} ${lembrete.horario}".trim(),
                color = CinzaTexto,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
