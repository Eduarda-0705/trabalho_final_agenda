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
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.AzulMarinho
import com.example.myapplication.ui.theme.CinzaTexto
import com.example.myapplication.ui.theme.Terracota
import com.example.myapplication.ui.viewmodel.ListaViewModel

@Composable
fun ListaFormScreen(
    modifier: Modifier = Modifier,
    listaId: Long? = null,
    viewModel: ListaViewModel,
    voltar: () -> Unit
) {
    LaunchedEffect(listaId) {
        if (listaId != null) viewModel.buscarPorId(listaId)
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
                text = if (listaId == null) "Nova Lista" else "Alterar Lista",
                style = MaterialTheme.typography.titleLarge,
                color = Terracota,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = viewModel::salvar,
                enabled = viewModel.nome.isNotBlank() && !viewModel.carregando && !viewModel.salvo,
                colors = ButtonDefaults.buttonColors(containerColor = AzulMarinho),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = "Salvar")
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.mensagem?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            if (viewModel.carregando) CircularProgressIndicator(color = Terracota)

            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = viewModel.nome,
                        onValueChange = viewModel::mudarNome,
                        label = { Text("Nome da lista") },
                        leadingIcon = {
                            Icon(painterResource(id = R.drawable.ic_list), contentDescription = null)
                        },
                        singleLine = true,
                        enabled = !viewModel.carregando,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Text(
                text = "Exemplos: Faculdade, Trabalho, Mercado.",
                color = CinzaTexto,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
