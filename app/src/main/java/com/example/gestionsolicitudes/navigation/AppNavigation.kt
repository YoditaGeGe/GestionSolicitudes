package com.example.gestionsolicitudes.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestionsolicitudes.ui.screen.AdminDetalleScreen
import com.example.gestionsolicitudes.ui.screen.DetalleSolicitudScreen
import com.example.gestionsolicitudes.ui.screen.InicioAdminScreen
import com.example.gestionsolicitudes.ui.screen.InicioClienteScreen
import com.example.gestionsolicitudes.ui.screen.InicioTecnicoScreen
import com.example.gestionsolicitudes.ui.screen.LoginScreen
import com.example.gestionsolicitudes.ui.screen.NuevaSolicitudScreen
import com.example.gestionsolicitudes.model.Perfil
import com.example.gestionsolicitudes.ui.screen.QrScannerRoute
import com.example.gestionsolicitudes.viewmodel.LoginViewModel
import com.example.gestionsolicitudes.viewmodel.SolicitudViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val solicitudViewModel: SolicitudViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController, loginViewModel) }
        composable("cliente") { InicioClienteScreen(navController, loginViewModel, solicitudViewModel) }
        composable("nueva") { NuevaSolicitudScreen(navController, loginViewModel, solicitudViewModel) }
        composable("detalle") { DetalleSolicitudScreen(navController, solicitudViewModel) }
        composable("admin") { InicioAdminScreen(navController, loginViewModel, solicitudViewModel) }
        composable("admin_detalle") { AdminDetalleScreen(navController, loginViewModel, solicitudViewModel) }
        composable("tecnico") { InicioTecnicoScreen(navController, loginViewModel) }
        composable("escanear_qr") {
            QrScannerRoute(
                onVolver = { navController.popBackStack() },
                onCodigoLeido = { texto ->
                    val abrio = solicitudViewModel.seleccionarSiExiste(texto)
                    if (abrio) {
                        val perfil = loginViewModel.estado.value.usuarioActual?.perfil
                        val ruta = if (perfil == Perfil.ADMINISTRADOR) "admin_detalle" else "detalle"
                        navController.navigate(ruta) {
                            popUpTo("escanear_qr") { inclusive = true }
                        }
                    }
                    abrio
                }
            )
        }
    }
}
