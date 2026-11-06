package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.myapplication.ui.theme.AzulCartao
import com.example.myapplication.ui.theme.AzulMarinho
import com.example.myapplication.ui.theme.CinzaCartao
import com.example.myapplication.ui.theme.Terracota
import com.example.myapplication.ui.theme.VermelhoCartao
import com.example.myapplication.ui.viewmodel.LembreteViewModel
import com.example.myapplication.ui.viewmodel.UsuarioViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    lembreteViewModel: LembreteViewModel,
    usuarioViewModel: UsuarioViewModel,
    irParaProgramado: () -> Unit,
    irParaLembretes: (Long) -> Unit,
    irParaListas: () -> Unit,
    irParaLocais: () -> Unit,
    irParaUsuarios: () -> Unit,
    irParaNovoLembrete: () -> Unit
) {
    LaunchedEffect(Unit) {
        lembreteViewModel.listar()
        usuarioViewModel.carregarUsuarioAtivo()
    }

    val lembretes = lembreteViewModel.lembretes

    val totalSinalizados = lembretes.count { it.sinalizado && !it.concluido }
    val totalProgramado = lembretes.count { it.data.isNotEmpty() || it.horario.isNotEmpty() }
    val totalTodos = lembretes.size
    val totalConcluidos = lembretes.count { it.concluido }

    val nomeUsuario = usuarioViewModel.usuario?.nome ?: ""

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Olá, $nomeUsuario!",
                style = MaterialTheme.typography.titleLarge,
                color = AzulMarinho,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            if (lembreteViewModel.carregando) CircularProgressIndicator(color = Terracota)
            lembreteViewModel.mensagem?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                CartaoResumo(
                    titulo = "Sinalizado",
                    quantidade = totalSinalizados,
                    icone = R.drawable.ic_flag,
                    corFundo = AzulCartao,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp),
                    onClick = { irParaLembretes(0L) }
                )
                CartaoResumo(
                    titulo = "Programado",
                    quantidade = totalProgramado,
                    icone = R.drawable.ic_calendar,
                    corFundo = VermelhoCartao,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp),
                    onClick = irParaProgramado
                )
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                CartaoResumo(
                    titulo = "Tudo",
                    quantidade = totalTodos,
                    icone = R.drawable.ic_inbox,
                    corFundo = CinzaCartao,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp),
                    onClick = { irParaLembretes(0L) }
                )
                CartaoResumo(
                    titulo = "Concluído",
                    quantidade = totalConcluidos,
                    icone = R.drawable.ic_check_circle,
                    corFundo = AzulMarinho,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp),
                    onClick = { irParaLembretes(0L) }
                )
            }

            Surface(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Minhas Listas",
                        style = MaterialTheme.typography.titleMedium,
                        color = Terracota
                    )
                    LinhaLista(
                        icone = R.drawable.ic_inbox,
                        texto = "Todos os lembretes",
                        valor = "$totalTodos",
                        onClick = { irParaLembretes(0L) }
                    )
                    lembreteViewModel.listas.forEach { lista ->
                        LinhaLista(
                            icone = R.drawable.ic_list,
                            texto = lista.nome,
                            valor = "${lembretes.count { it.listaId == lista.id }}",
                            onClick = { irParaLembretes(lista.id) }
                        )
                    }
                    LinhaLista(
                        icone = R.drawable.ic_note_add,
                        texto = "Gerenciar listas",
                        valor = "",
                        onClick = irParaListas
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cadastros",
                        style = MaterialTheme.typography.titleMedium,
                        color = Terracota
                    )
                    LinhaLista(
                        icone = R.drawable.ic_flag,
                        texto = "Locais",
                        valor = "${lembreteViewModel.locais.size}",
                        onClick = irParaLocais
                    )
                    LinhaLista(
                        icone = R.drawable.ic_user,
                        texto = "Usuários",
                        valor = "",
                        onClick = irParaUsuarios
                    )
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
private fun LinhaLista(
    icone: Int,
    texto: String,
    valor: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            color = Terracota
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = icone),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(16.dp)
                )
            }
        }
        Text(
            text = texto,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
            color = AzulMarinho
        )
        Text(text = valor, color = AzulMarinho)
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = AzulMarinho
        )
    }
}

@Composable
private fun CartaoResumo(
    titulo: String,
    quantidade: Int,
    icone: Int,
    corFundo: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        color = corFundo
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = icone),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Text(text = "$quantidade", color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
            Text(
                text = titulo,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
