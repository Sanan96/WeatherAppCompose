package com.example.weathercompose

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request.Method
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weathercompose.data.WeatherModel
import com.example.weathercompose.screens.DialogSearch
import com.example.weathercompose.screens.MainCard
import com.example.weathercompose.screens.TabLayout
import org.json.JSONObject

const val API_KEY = "5043620a905c458f8ce174243241905"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val dayList = remember {
                mutableStateOf(listOf<WeatherModel>())
            }

            val dialogState = remember {
                mutableStateOf(false)
            }

            val currentDay = remember {
                mutableStateOf(
                    WeatherModel(
                        "",
                        "",
                        "0.0",
                        "",
                        "",
                        "0.0",
                        "0.0",
                        "",
                        ""
                    )
                )
            }
            // Проверяем, находится ли `dialogState.value` в состоянии `true`.
// Если `true`, это означает, что диалог должен быть отображён.
            if (dialogState.value) {
                // Вызываем функцию `DialogSearch`, которая создаёт диалоговое окно для ввода текста.
                // Передаём:
                // 1. `dialogState`: объект состояния, управляющий видимостью диалога.
                //    Это позволяет скрывать диалог, изменяя его значение на `false`.
                // 2. `onSubmit`: задаём функцию, которая выполнится, когда пользователь нажмёт "OK" в диалоге.
                //    В данном случае передаётся лямбда, которая:
                //    - Получает введённый текст `it` (текст, введённый пользователем).
                //    - Вызывает функцию `getData` с параметрами:
                //      - `it`: введённый текст.
                //      - `this`: текущий контекст активности или компонента.
                //      - `dayList`: список данных о погоде.
                //      - `currentDay`: состояние текущего дня для обновления интерфейса.
                DialogSearch(dialogState, onSubmit = {
                    getData(it, this, dayList, currentDay)
                })
            }



            getData("Baku", this, dayList, currentDay)
            Image(
                painter = painterResource(R.drawable.sky), contentDescription = "im1",
                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds
            )

            Column {
                MainCard(currentDay, onClickSynth = {
                    getData("Baku", this@MainActivity, dayList, currentDay)
                }, onClickSearch = {
                    dialogState.value = true
                }
                )
                TabLayout(dayList, currentDay)
            }

        }
    }
}

fun getData(
    city: String, context: Context, dayList: MutableState<List<WeatherModel>>,
    currentDay: MutableState<WeatherModel>
) {
    val url = "http://api.weatherapi.com/v1/forecast.json" +
            "?key=$API_KEY" +
            "&q=$city" +
            "&days=3" +
            "&aqi=no" +
            "&alerts=no\n"

    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(Method.GET, url, { response ->
        val list = getWeatherByDays(response)
        dayList.value = list
        currentDay.value = list[0]
    }, {
        Log.d("MyLog", "error $it")
    })
    queue.add(stringRequest)
}

// Функция принимает строку `response`, которая содержит JSON-ответ с данными о погоде.
// Возвращает список объектов типа `WeatherModel`, представляющих прогноз погоды по дням.
private fun getWeatherByDays(response: String): List<WeatherModel> {
    // Проверяем, является ли строка `response` пустой.
    // Если пустая, возвращаем пустой список, чтобы избежать ошибок обработки.
    if (response.isEmpty()) return listOf()

    // Создаём пустой изменяемый список типа `ArrayList` для хранения объектов `WeatherModel`.
    val list = ArrayList<WeatherModel>()

    // Преобразуем строку `response` в JSON-объект для дальнейшего извлечения данных.
    val mainObject = JSONObject(response)

    // Извлекаем имя города из объекта `location` в JSON.
    // Поле `name` содержит название города, для которого предоставлен прогноз.
    val city = mainObject.getJSONObject("location").getString("name")

    // Извлекаем массив прогнозов на несколько дней из объекта `forecast`.
    // Поле `forecastday` содержит массив с прогнозами погоды по дням.
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                currentTemp = "",
                condition = item.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                item.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                item.getJSONArray("hour").toString(),
                item.getJSONObject("day").getString("maxwind_mph")
            )
        )
    }
    list[0] = list[0].copy(
        // Копируем объект из списка `list` по индексу 0, создавая его новую версию с изменёнными значениями.
        // Метод `copy` позволяет создать новый объект на основе существующего с изменёнными указанными параметрами.
        time = mainObject.getJSONObject("current").getString("last_updated"),
        // Устанавливаем значение параметра `time` для нового объекта.
        // Значение извлекается из JSON-объекта `mainObject`. Мы переходим к вложенному объекту "current",
        // из которого получаем строку "last_updated" — вероятно, это время последнего обновления данных.
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
            .toFloat().toInt().toString()
        // Устанавливаем значение параметра `currentTemp` для нового объекта.
        // Значение берётся из объекта "current" в JSON, из строки "temp_c" — это температура в градусах Цельсия.
        // Значение преобразуется в `Float`, затем округляется до целого числа (`Int`), и обратно в строку (`String`).
    )
    return list
// Возвращаем обновлённый список `list`.
// В данном случае, изменён только объект с индексом 0, остальные объекты остаются неизменными.
}

