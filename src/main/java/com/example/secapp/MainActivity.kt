package com.example.secapp

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.content.Intent
import android.os.Build
import android.net.Uri
import android.content.pm.ApplicationInfo
import androidx.core.content.FileProvider
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.secapp.ui.theme.SecAppTheme
import androidx.compose.ui.graphics.ImageBitmap
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ListaDeAppsConPermisos()
                }
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun ListaDeAppsConPermisos() {
    val context = LocalContext.current
    val pm = context.packageManager
    var apps by remember { mutableStateOf(listOf<AppInfo>()) }
    var expandedApp by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val paquetes = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val lista = paquetes
            .map { appInfo ->
            val nombre = pm.getApplicationLabel(appInfo).toString()
            val icono = pm.getApplicationIcon(appInfo).toBitmap().asImageBitmap()
            val permisos = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getPackageInfo(
                        appInfo.packageName,
                        PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                    ).requestedPermissions?.toList() ?: emptyList()
                } else {
                    @Suppress("DEPRECATION")
                    pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS)
                        .requestedPermissions?.toList() ?: emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
            AppInfo(nombre, appInfo.packageName, icono, permisos)
        }.sortedBy { it.nombre }

        apps = lista
        // Guardar como JSON (sin los iconos)
        val jsonLista = lista.map { SimpleAppInfo(it.nombre, it.paquete, it.permisos) }
        val jsonString = Json.encodeToString(jsonLista)

        val areEqual = compareJsonFiles(context, jsonString)
            if (areEqual) {
                Toast.makeText(context, "Permisos idénticos", Toast.LENGTH_LONG).show()
                println("Los JSON son iguales.")
         } else {
                Toast.makeText(context, "Han cambiado los permisos", Toast.LENGTH_LONG).show()
                 println("Los JSON son diferentes.")
         }

        val file = File(context.filesDir, "apps_permisos.json")
        FileOutputStream(file).use {
            it.write(jsonString.toByteArray())
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Button(
            onClick = {
                val file = File(context.filesDir, "apps_permisos.json")
                if (file.exists()) {
                    val uri = FileProvider.getUriForFile(
                        context,
                        context.packageName + ".provider",
                        file
                    )
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/json")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Abrir archivo JSON con"))

                } else {
                    Toast.makeText(context, "Archivo no encontrado", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text("Ver JSON generado")
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(apps) { app ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            expandedApp = if (expandedApp == app.paquete) null else app.paquete
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row {
                            Image(bitmap = app.icono, contentDescription = null, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = app.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(text = app.paquete, style = MaterialTheme.typography.bodySmall)
                                Text(text = "Permisos: ${app.permisos.size}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    if (expandedApp == app.paquete) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(modifier = Modifier.padding(start = 56.dp)) {
                            if (app.permisos.isEmpty()) {
                                Text("No solicita permisos", style = MaterialTheme.typography.bodySmall)
                            } else {
                                app.permisos.forEach { permiso ->
                                    Text("\u2022 $permiso", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class AppInfo(
    val nombre: String,
    val paquete: String,
    val icono: ImageBitmap,
    val permisos: List<String>
)
@Serializable
data class SimpleAppInfo(
    val nombre: String,
    val paquete: String,
    val permisos: List<String>
)

fun compareJsonFiles(context: Context, jsonString: String): Boolean {
     val file = File(context.filesDir, "apps_permisos.json")
     if (!file.exists()) return false

     val fileContent = file.readText()

     return try {
         val json1: JsonElement = Json.parseToJsonElement(fileContent)
         val json2: JsonElement = Json.parseToJsonElement(jsonString)
         json1 == json2
         } catch (e: Exception) {
         e.printStackTrace()
         false
         }
}
