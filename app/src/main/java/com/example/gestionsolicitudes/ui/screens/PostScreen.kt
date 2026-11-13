package com.example.gestionsolicitudes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestionsolicitudes.ui.components.PrettyCard
import com.example.gestionsolicitudes.ui.components.SoftChip
import com.example.gestionsolicitudes.ui.theme.CoralAccent
import com.example.gestionsolicitudes.ui.theme.SoftBackground
import com.example.gestionsolicitudes.ui.theme.TealContainer
import com.example.gestionsolicitudes.ui.theme.TealPrimary
import com.example.gestionsolicitudes.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    viewModel: PostViewModel,
    onIrAGestion: (() -> Unit)? = null
) {
    val posts = viewModel.postList.collectAsState().value
    val mensaje = viewModel.mensaje.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        containerColor = SoftBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Posts desde la API", fontWeight = FontWeight.SemiBold)
                        Text(
                            "JSONPlaceholder · Guía 14",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (onIrAGestion != null) {
                        TextButton(onClick = onIrAGestion) {
                            Text("Volver", color = TealPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TealContainer.copy(alpha = 0.65f)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.refrescar() },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(44.dp)
                ) { Text("GET") }
                Button(
                    onClick = { viewModel.crearPostDemo() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CoralAccent
                    ),
                    modifier = Modifier.height(44.dp)
                ) { Text("POST") }
            }

            if (posts.isEmpty()) {
                PrettyCard {
                    Text("Cargando posts...", fontWeight = FontWeight.Medium)
                    Text(
                        "En un momento aparecen los datos de la API.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(posts) { post ->
                    PrettyCard {
                        SoftChip("Usuario ${post.userId}", TealPrimary)
                        Text(
                            text = "Título: ${post.title}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = post.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.actualizarPost(post) },
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("PUT") }
                            TextButton(onClick = { viewModel.eliminarPost(post) }) {
                                Text("DELETE", color = CoralAccent)
                            }
                        }
                    }
                }
            }
        }
    }
}
