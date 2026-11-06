package com.example.myapplication.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.example.myapplication.auth.SessaoUsuario
import com.example.myapplication.ui.screens.CadastroScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.LembretesScreen
import com.example.myapplication.ui.screens.ListaFormScreen
import com.example.myapplication.ui.screens.ListasScreen
import com.example.myapplication.ui.screens.LocaisScreen
import com.example.myapplication.ui.screens.LocalFormScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.NovoLembreteScreen
import com.example.myapplication.ui.screens.ProgramadoScreen
import com.example.myapplication.ui.screens.UsuarioEditarScreen
import com.example.myapplication.ui.screens.UsuariosScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val rotaAtual by navController.currentBackStackEntryAsState()
    val destino = rotaAtual?.destination
    val usuarioAtual = SessaoUsuario.usuarioId

    LaunchedEffect(destino, usuarioAtual) {
        val publica = destino?.hasRoute<Login>() == true ||
                destino?.hasRoute<Cadastro>() == true

        if (destino != null && !publica && usuarioAtual == null) {
            navController.navigate(Login) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Login
    ) {
        composable<Login> {
            LoginScreen(
                loginSucesso = {
                    navController.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                irCadastro = { navController.navigate(Cadastro) },
                authViewModel = viewModel()
            )
        }

        composable<Cadastro> {
            CadastroScreen(
                irLogin = { navController.popBackStack() },
                authViewModel = viewModel()
            )
        }

        composable<Home> {
            HomeScreen(
                modifier = modifier,
                lembreteViewModel = viewModel(),
                usuarioViewModel = viewModel(),
                irParaProgramado = { navController.navigate(Programado) },
                irParaLembretes = { navController.navigate(Lembretes(it)) },
                irParaListas = { navController.navigate(Listas) },
                irParaLocais = { navController.navigate(Locais) },
                irParaUsuarios = { navController.navigate(Usuarios) },
                irParaNovoLembrete = { navController.navigate(NovoLembrete) }
            )
        }

        composable<Programado> {
            ProgramadoScreen(
                modifier = modifier,
                viewModel = viewModel(),
                voltar = { navController.popBackStack() },
                irParaNovoLembrete = { navController.navigate(NovoLembrete) }
            )
        }

        composable<Lembretes> { entrada ->
            val rota = entrada.toRoute<Lembretes>()
            LembretesScreen(
                modifier = modifier,
                listaId = rota.listaId,
                viewModel = viewModel(),
                irParaNovoLembrete = { navController.navigate(NovoLembrete) },
                irParaEdicao = { navController.navigate(EditarLembrete(it)) },
                voltar = { navController.popBackStack() }
            )
        }

        composable<NovoLembrete>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(500)
                )
            }
        ) {
            NovoLembreteScreen(
                modifier = modifier,
                viewModel = viewModel(),
                voltar = { navController.popBackStack() }
            )
        }

        composable<EditarLembrete> { entrada ->
            val rota = entrada.toRoute<EditarLembrete>()
            NovoLembreteScreen(
                modifier = modifier,
                lembreteId = rota.id,
                viewModel = viewModel(),
                voltar = { navController.popBackStack() }
            )
        }

        composable<Listas> {
            ListasScreen(
                modifier = modifier,
                viewModel = viewModel(),
                irParaNovaLista = { navController.navigate(NovaLista) },
                irParaEdicao = { navController.navigate(EditarLista(it)) },
                irParaLembretes = { navController.navigate(Lembretes(it)) },
                voltar = { navController.popBackStack() }
            )
        }

        composable<NovaLista> {
            ListaFormScreen(
                modifier = modifier,
                viewModel = viewModel(),
                voltar = { navController.popBackStack() }
            )
        }

        composable<EditarLista> { entrada ->
            val rota = entrada.toRoute<EditarLista>()
            ListaFormScreen(
                modifier = modifier,
                listaId = rota.id,
                viewModel = viewModel(),
                voltar = { navController.popBackStack() }
            )
        }

        composable<Locais> {
            LocaisScreen(
                modifier = modifier,
                viewModel = viewModel(),
                irParaNovoLocal = { navController.navigate(NovoLocal) },
                irParaEdicao = { navController.navigate(EditarLocal(it)) },
                voltar = { navController.popBackStack() }
            )
        }

        composable<NovoLocal> {
            LocalFormScreen(
                modifier = modifier,
                viewModel = viewModel(),
                voltar = { navController.popBackStack() }
            )
        }

        composable<EditarLocal> { entrada ->
            val rota = entrada.toRoute<EditarLocal>()
            LocalFormScreen(
                modifier = modifier,
                localId = rota.id,
                viewModel = viewModel(),
                voltar = { navController.popBackStack() }
            )
        }

        composable<Usuarios> {
            UsuariosScreen(
                modifier = modifier,
                viewModel = viewModel(),
                irParaCadastro = { navController.navigate(NovoUsuario) },
                irParaEdicao = { navController.navigate(UsuarioEditar(it)) },
                voltar = { navController.popBackStack() }
            )
        }

        composable<NovoUsuario> {
            CadastroScreen(
                modifier = modifier,
                irLogin = { navController.popBackStack() },
                authViewModel = viewModel()
            )
        }

        composable<UsuarioEditar> { entrada ->
            val rota = entrada.toRoute<UsuarioEditar>()
            UsuarioEditarScreen(
                modifier = modifier,
                usuarioId = rota.id,
                viewModel = viewModel(),
                voltar = { navController.popBackStack() }
            )
        }
    }
}
