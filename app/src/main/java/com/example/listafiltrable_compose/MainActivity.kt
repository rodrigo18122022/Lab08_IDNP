package com.example.listafiltrable_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listafiltrable_compose.ui.theme.ListaFiltrable_ComposeTheme
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton

val AzulCampus = Color(0xFF163A5F)
val TurquesaCampus = Color(0xFF2A9D8F)
val FondoCampus = Color(0xFFF5F7FA)
val TextoSecundario = Color(0xFF667085)
val BlancoTarjeta = Color(0xFFFFFFFF)


fun colorCategoria(categoria: String): Color {
    return when (categoria) {
        "Académico" -> Color(0xFF2F6BFF)
        "Servicios" -> Color(0xFF009688)
        "Deportes" -> Color(0xFFF57C00)
        "Eventos" -> Color(0xFF8E44AD)
        else -> AzulCampus
    }
}



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            ListaFiltrable_ComposeTheme {

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AplicacionEdificios()
                }
            }
        }
    }
}

data class Edificio(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val descripcion: String
)


fun prepararArchivoEdificios(context: Context) {
    val nombreArchivo = "edificios.txt"

    if (!context.getFileStreamPath(nombreArchivo).exists()) {
        val contenido = context.assets
            .open(nombreArchivo)
            .bufferedReader()
            .use { it.readText() }

        context.openFileOutput(
            nombreArchivo,
            Context.MODE_PRIVATE
        ).use { archivo ->
            archivo.write(contenido.toByteArray())
        }
    }
}


fun cargarEdificiosDesdeArchivo(
    context: Context
): List<Edificio> {

    prepararArchivoEdificios(context)

    return try {
        context.openFileInput("edificios.txt")
            .bufferedReader()
            .readLines()
            .mapNotNull { linea ->

                val datos = linea.split("|")

                if (datos.size == 4) {
                    Edificio(
                        id = datos[0].toInt(),
                        nombre = datos[1],
                        categoria = datos[2],
                        descripcion = datos[3]
                    )
                } else {
                    null
                }
            }

    } catch (e: Exception) {
        emptyList()
    }
}


@Composable
fun EdificiosScreen(
    onEdificioClick: (Edificio) -> Unit
) {

    val context = LocalContext.current

    val edificios = remember {
        cargarEdificiosDesdeArchivo(context)
    }

    var texto by remember {
        mutableStateOf("")
    }

    var categoriaSeleccionada by remember {
        mutableStateOf("Todas")
    }

    val categorias = remember(edificios) {
        listOf("Todas") +
                edificios
                    .map { it.categoria }
                    .distinct()
                    .sorted()
    }

    val edificiosFiltrados = edificios.filter { edificio ->

        val coincideNombre =
            edificio.nombre.contains(
                texto,
                ignoreCase = true
            )

        val coincideCategoria =
            categoriaSeleccionada == "Todas" ||
                    edificio.categoria == categoriaSeleccionada

        coincideNombre && coincideCategoria
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCampus)
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AzulCampus,
                            TurquesaCampus
                        )
                    )
                )
                .padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(58.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.18f)
                ) {

                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Campus",
                        tint = Color.White,
                        modifier = Modifier.padding(15.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(15.dp)
                )

                Column {

                    Text(
                        text = "Explora el campus",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Encuentra edificios y conoce sus servicios",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        OutlinedTextField(
            value = texto,
            onValueChange = {
                texto = it
            },
            label = {
                Text("Buscar edificio")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = AzulCampus
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TurquesaCampus,
                focusedLabelColor = TurquesaCampus,
                cursorColor = AzulCampus
            )
        )

        Spacer(
            modifier = Modifier.height(13.dp)
        )

        SelectorCategoria(
            categorias = categorias,
            categoriaSeleccionada = categoriaSeleccionada,
            onCategoriaSeleccionada = {
                categoriaSeleccionada = it
            }
        )

        Spacer(
            modifier = Modifier.height(13.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Edificios disponibles",
                color = AzulCampus,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = TurquesaCampus.copy(alpha = 0.12f)
            ) {

                Text(
                    text = edificiosFiltrados.size.toString(),
                    color = TurquesaCampus,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    )
                )
            }
        }

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            items(
                items = edificiosFiltrados,
                key = { it.id }
            ) { edificio ->

                TarjetaLugar(
                    edificio = edificio,
                    onClick = {
                        onEdificioClick(edificio)
                    }
                )
            }
        }
    }
}


@Composable
fun DetalleEdificioScreen(
    edificio: Edificio,
    onVolver: () -> Unit
) {

    val color = colorCategoria(
        edificio.categoria
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCampus)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AzulCampus,
                            color
                        )
                    )
                )
                .padding(
                    start = 12.dp,
                    end = 20.dp,
                    top = 18.dp,
                    bottom = 28.dp
                )
        ) {

            Column {

                IconButton(
                    onClick = onVolver
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Surface(
                    modifier = Modifier.size(74.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White.copy(alpha = 0.18f)
                ) {

                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = edificio.nombre,
                        tint = Color.White,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Text(
                    text = edificio.nombre,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color.White.copy(alpha = 0.18f)
                ) {

                    Text(
                        text = edificio.categoria,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(
                            horizontal = 13.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(
                text = "Información del lugar",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AzulCampus
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Descripción",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )

                    Text(
                        text = edificio.descripcion,
                        fontSize = 16.sp,
                        color = TextoSecundario
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Text(
                        text = "Categoría",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = edificio.categoria,
                        fontSize = 16.sp,
                        color = AzulCampus
                    )
                }
            }
        }
    }
}

@Composable
fun AplicacionEdificios() {

    var edificioSeleccionado by remember {
        mutableStateOf<Edificio?>(null)
    }

    if (edificioSeleccionado == null) {

        EdificiosScreen(
            onEdificioClick = { edificio ->
                edificioSeleccionado = edificio
            }
        )

    } else {

        DetalleEdificioScreen(
            edificio = edificioSeleccionado!!,
            onVolver = {
                edificioSeleccionado = null
            }
        )
    }
}


@Composable
fun SelectorCategoria(
    categorias: List<String>,
    categoriaSeleccionada: String,
    onCategoriaSeleccionada: (String) -> Unit
) {
    var expandido by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Filtrar por categoría",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextoSecundario
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedButton(
                onClick = {
                    expandido = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = categoriaSeleccionada,
                    modifier = Modifier.weight(1f),
                    color = AzulCampus,
                    fontWeight = FontWeight.Medium
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Mostrar categorías",
                    tint = AzulCampus
                )
            }

            DropdownMenu(
                expanded = expandido,
                onDismissRequest = {
                    expandido = false
                }
            ) {

                categorias.forEach { categoria ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = categoria,
                                fontWeight = if (
                                    categoria == categoriaSeleccionada
                                ) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                }
                            )
                        },
                        onClick = {
                            onCategoriaSeleccionada(categoria)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}






@Composable
fun TarjetaLugar(
    edificio: Edificio,
    onClick: () -> Unit
) {
    val color = colorCategoria(edificio.categoria)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(62.dp),
                shape = RoundedCornerShape(18.dp),
                color = color.copy(alpha = 0.13f)
            ) {

                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = edificio.nombre,
                    tint = color,
                    modifier = Modifier.padding(17.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(15.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = edificio.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulCampus
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = color.copy(alpha = 0.12f)
                ) {

                    Text(
                        text = edificio.categoria,
                        color = color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                    )
                }

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = edificio.descripcion,
                    color = TextoSecundario,
                    fontSize = 13.sp,
                    maxLines = 2
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = "Ver información →",
                    color = TurquesaCampus,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun AplicacionEdificiosPreview() {

    ListaFiltrable_ComposeTheme {
        AplicacionEdificios()
    }
}