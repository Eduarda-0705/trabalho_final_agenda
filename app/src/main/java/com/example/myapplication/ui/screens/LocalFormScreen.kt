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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.AzulMarinho
import com.example.myapplication.ui.theme.CinzaTexto
import com.example.myapplication.ui.theme.Terracota
import com.example.myapplication.ui.viewmodel.LocalViewModel

@Composable
fun LocalFormScreen(
    modifier: Modifier = Modifier,
    localId: Long? = null,
    viewModel: LocalViewModel,
    voltar: () -> Unit
) {
    LaunchedEffect(localId) {
        if (localId != null) viewModel.buscarPorId(localId)
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
                text = if (localId == null) "Novo Local" else "Alterar Local",
                style = MaterialTheme.typography.titleLarge,
                color = Terracota,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = viewModel::salvar,
                enabled = viewModel.nome.isNotBlank() && viewModel.cidade.isNotBlank() &&
                        !viewModel.carregando && !viewModel.salvo,
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
            if (viewModel.carregando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            viewModel.mensagem?.let {
                Text(text = it, color = AzulMarinho, modifier = Modifier.padding(top = 8.dp))
            }

            TituloSecao(texto = "Identificação")
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = viewModel.nome,
                        onValueChange = viewModel::mudarNome,
                        label = { Text("Nome do local") },
                        leadingIcon = {
                            Icon(painterResource(id = R.drawable.ic_flag), contentDescription = null)
                        },
                        singleLine = true,
                        enabled = !viewModel.carregando,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            TituloSecao(texto = "Endereço")
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = viewModel.cep,
                            onValueChange = viewModel::mudarCep,
                            label = { Text("CEP") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            enabled = !viewModel.carregando,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = viewModel::buscarCep,
                            enabled = !viewModel.carregando,
                            colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(text = "Buscar CEP")
                        }
                    }
                    Text(
                        text = "Digite o CEP e toque em Buscar CEP para preencher o endereço.",
                        color = CinzaTexto,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = viewModel.rua,
                        onValueChange = viewModel::mudarRua,
                        label = { Text("Rua") },
                        singleLine = true,
                        enabled = !viewModel.carregando,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row {
                        OutlinedTextField(
                            value = viewModel.numero,
                            onValueChange = viewModel::mudarNumero,
                            label = { Text("Número") },
                            singleLine = true,
                            enabled = !viewModel.carregando,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = viewModel.complemento,
                            onValueChange = viewModel::mudarComplemento,
                            label = { Text("Complemento") },
                            singleLine = true,
                            enabled = !viewModel.carregando,
                            modifier = Modifier
                                .weight(1.5f)
                                .padding(start = 8.dp)
                        )
                    }
                    OutlinedTextField(
                        value = viewModel.bairro,
                        onValueChange = viewModel::mudarBairro,
                        label = { Text("Bairro") },
                        singleLine = true,
                        enabled = !viewModel.carregando,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row {
                        OutlinedTextField(
                            value = viewModel.cidade,
                            onValueChange = viewModel::mudarCidade,
                            label = { Text("Cidade") },
                            singleLine = true,
                            enabled = !viewModel.carregando,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = viewModel.uf,
                            onValueChange = viewModel::mudarUf,
                            label = { Text("UF") },
                            singleLine = true,
                            enabled = !viewModel.carregando,
                            modifier = Modifier
                                .width(90.dp)
                                .padding(start = 8.dp)
                        )
                    }
                }
            }

            TituloSecao(texto = "Observação")
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = viewModel.observacao,
                        onValueChange = viewModel::mudarObservacao,
                        label = { Text("Observação (opcional)") },
                        enabled = !viewModel.carregando,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun TituloSecao(texto: String) {
    Text(
        text = texto,
        color = Terracota,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}
