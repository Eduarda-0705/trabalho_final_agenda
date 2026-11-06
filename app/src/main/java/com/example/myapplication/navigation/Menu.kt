package com.example.myapplication.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.auth.SessaoUsuario
import com.example.myapplication.ui.theme.AzulMarinho
import com.example.myapplication.ui.theme.Terracota
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu() {
    val navController = rememberNavController()
    val entradaAtual by navController.currentBackStackEntryAsState()
    val destino = entradaAtual?.destination

    val mostrarMenu = destino != null &&
            !destino.hasRoute<Login>() &&
            !destino.hasRoute<Cadastro>()

    val itens = listOf(
        Triple("Início", Home, destino?.hasRoute<Home>() ?: false),
        Triple("Lembretes", Lembretes(), destino?.hasRoute<Lembretes>() ?: false),
        Triple("Programado", Programado, destino?.hasRoute<Programado>() ?: false),
        Triple("Listas", Listas, destino?.hasRoute<Listas>() ?: false),
        Triple("Locais", Locais, destino?.hasRoute<Locais>() ?: false),
        Triple("Usuários", Usuarios, destino?.hasRoute<Usuarios>() ?: false)
    )

    val menu = rememberDrawerState(DrawerValue.Closed)
    val escopo = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = menu,
        gesturesEnabled = mostrarMenu,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(250.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Noted",
                        style = MaterialTheme.typography.titleLarge,
                        color = Terracota,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                    itens.forEach { (nome, rota, selecionado) ->
                        NavigationDrawerItem(
                            label = { Text(nome) },
                            selected = selecionado,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            onClick = {
                                escopo.launch {
                                    menu.close()
                                    if (!selecionado) {
                                        navController.navigate(rota) {
                                            popUpTo(Home) { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (mostrarMenu) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Noted",
                                color = Terracota,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            BotaoMenu { escopo.launch { menu.open() } }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        actions = {
                            IconButton(onClick = {
                                SessaoUsuario.encerrar()
                                navController.navigate(Login) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_logout),
                                    contentDescription = "Sair",
                                    tint = Terracota
                                )
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            AppNavigation(
                navController = navController,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        }
    }
}

@Composable
private fun BotaoMenu(abrirMenu: () -> Unit){
    IconButton(onClick = abrirMenu) {
        Text(
            text = "☰",
            fontSize = 24.sp,
            color = AzulMarinho
        )
    }
}
