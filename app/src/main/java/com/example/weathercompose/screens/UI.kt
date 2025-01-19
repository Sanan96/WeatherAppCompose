package com.example.weathercompose.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weathercompose.R
import com.example.weathercompose.data.WeatherModel

@Composable
fun MainList(list: List<WeatherModel>, currentDay: MutableState<WeatherModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Используем функцию `itemsIndexed`, чтобы отобразить элементы из списка `dayList.value`.
        // Она перебирает список и предоставляет как индекс элемента, так и сам элемент.
        itemsIndexed(
            list // Передаем список значений, которые мы хотим отобразить в списке.
        ) { _, item -> // `_` игнорирует индекс, так как он здесь не используется. `item` — текущий элемент из списка.
            ListItem(
                item,
                currentDay
            )  // Для каждого элемента вызываем функцию `ListItem`, которая отображает содержимое элемента.
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListItem(item: WeatherModel, currentDay: MutableState<WeatherModel>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp)
            .clip(RoundedCornerShape(5.dp))
            .clickable {
                if (item.hours.isEmpty()) return@clickable
                currentDay.value = item
            },
        backgroundColor = colorResource(R.color.BlueTrans), elevation = 5.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(3.dp)) {
                Text(
                    text = item.time,
                    Modifier.padding(top = 5.dp),
                    fontSize = 17.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = item.condition,
                    Modifier.padding(bottom = 5.dp),
                    fontSize = 17.sp,
                    color = Color.White
                )
                Text(text = "Wind " + item.kph + " kph", fontSize = 15.sp, color = Color.DarkGray,
                    fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 3.dp))
            }

            Text(
                text = item.currentTemp.ifEmpty { "${item.maxTemp}°C/${item.minTemp}°C"} ,
                fontSize = 25.sp, fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            AsyncImage(
                model = "http:${item.icon}",
                contentDescription = "im1", modifier = Modifier.size(35.dp)
            )
        }
    }
}

@Composable
// Определяем функцию `DialogSearch`, которая создаёт диалоговое окно для поиска.
// Она принимает два параметра:
// 1. `dialogState`: объект `MutableState<Boolean>`, который управляет состоянием отображения диалога (открыт/закрыт).
// 2. `onSubmit`: функция обратного вызова, принимающая строку (введённый текст) и вызываемая при подтверждении.

fun DialogSearch(dialogState: MutableState<Boolean>, onSubmit: (String) -> Unit) {
    // Создаём переменную `dialogText` для хранения текущего текста, введённого пользователем в диалоговом окне.
    // Используем `remember`, чтобы сохранять состояние текста при перестроении интерфейса.
    val dialogText = remember {
        mutableStateOf("")
    }

    // Создаём диалоговое окно `AlertDialog`, которое предоставляется Jetpack Compose.
    // Параметр `onDismissRequest` определяет действие при закрытии окна (вне кнопок).
    AlertDialog(
        onDismissRequest = {
            // При закрытии диалога изменяем значение `dialogState` на `false`, чтобы скрыть диалог.
            dialogState.value = false
        },
        // Определяем кнопку подтверждения (OK).
        confirmButton = {
            TextButton(
                onClick = {
                    // При нажатии кнопки вызываем функцию `onSubmit`, передавая текущий текст `dialogText`.
                    onSubmit(dialogText.value)
                    // Закрываем диалог, изменяя состояние `dialogState`.
                    dialogState.value = false
                }
            ) {
                // Отображаем текст "OK" на кнопке подтверждения.
                Text(text = "OK")
            }
        },
        // Определяем кнопку отмены (Cancel).
        dismissButton = {
            TextButton(
                onClick = {
                    // При нажатии кнопки отмены закрываем диалог, изменяя состояние `dialogState`.
                    dialogState.value = false
                }
            ) {
                // Отображаем текст "Cancel" на кнопке отмены.
                Text(text = "Cancel")
            }
        },
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Введите название города:")
                TextField(value = dialogText.value, onValueChange = {
                dialogText.value = it
                })
            }
        }
    )
}