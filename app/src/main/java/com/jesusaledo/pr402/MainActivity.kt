package com.jesusaledo.pr402
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jesusaledo.pr402.ui.theme.PR402Theme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PR402Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Concesionario()
                }
            }
        }
    }
}
@Composable
fun Concesionario() {
    var vehiculos by remember { mutableStateOf(emptyArray<Vehiculo>())}
    var showDialog by remember { mutableStateOf(false) }
    var mostrarNumeros by remember { mutableStateOf(false) }
    var mostrarVehiculos by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            showDialog = true
        }) {
            Text("Crear Vehículo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            mostrarVehiculos = false
            mostrarNumeros = !mostrarNumeros
        }) {
            Text("Consultar Número de Vehículos por Tipo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            mostrarNumeros = false
            mostrarVehiculos = !mostrarVehiculos
        }) {
            Text("Mostrar Todos los Vehículos")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (mostrarNumeros) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
            ) {
                vehiculos.groupBy { it::class.simpleName }.forEach { (tipo, lista) ->
                    item {
                        Text("$tipo: ${lista.size}")
                    }
                }
            }
        }
        if (mostrarVehiculos) {
            LazyColumn {
                items(vehiculos.sortedBy { it.modelo }) { vehiculo ->
                    Text(vehiculo.toString())
                }
            }
        }
        if (showDialog) {
            CrearVehiculoDialog(
                onDialogClose = { showDialog = false },
                onCreate = { nuevoVehiculo ->
                    vehiculos += nuevoVehiculo
                }
            )
        }
    }
}

@Composable
fun CrearVehiculoDialog(onCreate: (Vehiculo) -> Unit, onDialogClose: () -> Unit) {
    var vehiculoType by remember { mutableStateOf("") }
    var ruedas by remember { mutableStateOf("") }
    var motor by remember { mutableStateOf("") }
    var asientos by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var pesoMaximo by remember { mutableStateOf("") }
    val context = LocalContext.current
    var showError by remember { mutableStateOf(false) }
    var nuevoVehiculo: Vehiculo = Comodin()

    AlertDialog(
        onDismissRequest = onDialogClose,
        confirmButton = {
            Button(onClick = {
                nuevoVehiculo = when (vehiculoType) {
                    "Coche" -> Coche()
                    "Moto" -> Moto()
                    "Patinete" -> Patinete()
                    "Furgoneta" -> Furgoneta()
                    "Trailer" -> Trailer()
                    else -> Comodin()
                }
                if (nuevoVehiculo !is Comodin) {
                    if (vehiculoType.isNotBlank() &&
                        ruedas.isNotBlank() &&
                        motor.isNotBlank() &&
                        asientos.isNotBlank() &&
                        color.isNotBlank() &&
                        modelo.isNotBlank() &&
                        (nuevoVehiculo !is Patinete || asientos.toInt() == 0) &&
                        (nuevoVehiculo !is Trailer || ruedas.toInt() >= 6) &&
                        (nuevoVehiculo !is Furgoneta || ruedas.toInt() <= 6)
                    ) {
                        nuevoVehiculo.ruedas = ruedas.toInt()
                        nuevoVehiculo.motor = motor.toInt()
                        nuevoVehiculo.asientos = asientos.toInt()
                        nuevoVehiculo.color = color
                        nuevoVehiculo.modelo = modelo

                        when (nuevoVehiculo) {
                            is Trailer -> {
                                (nuevoVehiculo as Trailer).pesoMaximo =
                                    pesoMaximo.takeIf { it.isNotBlank() }?.toIntOrNull()?.takeIf { it >= 0 } ?: 15000
                            }
                            is Furgoneta -> {
                                (nuevoVehiculo as Furgoneta).pesoMaximo =
                                    pesoMaximo.takeIf { it.isNotBlank() }?.toIntOrNull()?.takeIf { it >= 0 } ?: 5000
                            }
                        }
                        onCreate(nuevoVehiculo)
                        showError = false
                        onDialogClose()
                    } else {
                        showError = true
                    }
                } else {
                    showError = true
                }
            }) {
                Text("Crear")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDialogClose()
            }) {
                Text("Cancelar")
            }
        },
        title = { Text("Crear Vehículo") },
        text = {
            Column {
                if (showError) {
                    Text("Tipo de vehículo no válido. Introduzca uno de los siguientes: Coche, Moto, Patinete, Furgoneta, Trailer.\nSi faltan campos rellénelos con valores válidos")
                    Spacer(modifier = Modifier.height(16.dp))
                }
                OutlinedTextField(
                    value = vehiculoType,
                    onValueChange = { vehiculoType = it },
                    label = { Text("Tipo de Vehículo") }
                )
                OutlinedTextField(
                    value = ruedas.toString(),
                    onValueChange = { ruedas = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    label = { Text("Número de Ruedas") }
                )
                OutlinedTextField(
                    value = motor.toString(),
                    onValueChange = { motor = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    label = { Text("Caballos de potencia") }
                )
                OutlinedTextField(
                    value = asientos.toString(),
                    onValueChange = { asientos = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    label = { Text("Número de Asientos") }
                )
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color") }
                )
                OutlinedTextField(
                    value = modelo,
                    onValueChange = { modelo = it },
                    label = { Text("Modelo") }
                )
                if (vehiculoType.equals("Trailer") || vehiculoType.equals("Furgoneta")) {
                    OutlinedTextField(
                        value = pesoMaximo,
                        onValueChange = { pesoMaximo = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        label = { Text("Peso Máximo") }
                    )
                }
            }
        }
    )
}