package com.example.weathercompose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weathercompose.R
import com.example.weathercompose.data.WeatherModel
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


@Composable
// Определяем функцию `MainCard`, которая отвечает за создание главной карточки интерфейса.
// Она принимает три параметра:
// 1. `currentDay`: объект типа `MutableState<WeatherModel>`, представляющий текущее состояние погодной модели.
//    Это используется для отслеживания и обновления данных о текущем дне в UI.
// 2. `onClickSynth`: функция без аргументов, которая вызывается при определённом действии (например, нажатии на кнопку синтеза).
// 3. `onClickSearch`: функция без аргументов, которая вызывается при определённом действии (например, нажатии на кнопку поиска).
fun MainCard(
    currentDay: MutableState<WeatherModel>,
    onClickSynth: () -> Unit,
    onClickSearch: () -> Unit
)
  {

    Column(
        modifier = Modifier
            .padding(5.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.BlueTrans)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp, start = 15.dp),
                        text = currentDay.value.time, style = TextStyle(fontSize = 15.sp),
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https://" + currentDay.value.icon,
                        contentDescription = "im2", modifier = Modifier.size(35.dp)
                    )
                }
                Text(
                    text = currentDay.value.city, style = TextStyle(fontSize = 26.sp),
                    color = Color.White
                )

                Text(
                    text = if (currentDay.value.currentTemp.isNotEmpty())
                        currentDay.value.currentTemp.toFloat().toInt().toString() + "°C" else
                   " ${currentDay.value.maxTemp.toFloat().toInt()}°C/${currentDay.value.minTemp
                       .toFloat().toInt()}°C",
                    style = TextStyle(fontSize = 65.sp),
                    color = Color.White
                )

                Text(
                    text = currentDay.value.condition, style = TextStyle(fontSize = 20.sp),
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                    onClickSearch.invoke()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.search_24),
                            contentDescription = "im3", tint = Color.White
                        )
                    }
                    Column (horizontalAlignment = Alignment.CenterHorizontally) { Text(
                        text = "${
                            currentDay.value.maxTemp.toFloat().toInt()
                        }°C/${currentDay.value.minTemp.toFloat().toInt()}°C",
                        style = TextStyle(fontSize = 20.sp),
                        color = Color.White
                    )
                    Text(text = "Wind " + currentDay.value.kph + " kph", color = Color.White,
                        fontSize = 15.sp, fontWeight = FontWeight.Bold )
                    }


                    IconButton(onClick = {
                    onClickSynth.invoke()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.sync_24),
                            contentDescription = "im4", tint = Color.White
                        )
                    }
                }
            }
        }

    }
}



@Composable
fun TabLayout(dayList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>) {
    // Создаем список вкладок, который содержит названия двух вкладок: "HOURS" и "DAYS".
    // Эти названия будут отображаться в интерфейсе.
    val tabList = listOf("HOURS", "DAYS")

    // Инициализируем состояние Pager.
    // `rememberPagerState(0)` означает, что начальная страница (вкладка) будет с индексом 0, то есть первая вкладка.
    val pagerState = rememberPagerState(0)

    // Получаем индекс текущей выбранной вкладки из состояния Pager.
    // Этот индекс будет использоваться для определения, какая вкладка активна.
    val tabIndex = pagerState.currentPage

    // Создаем область действия (scope) для корутин.
    // Она позволяет запускать асинхронные задачи, такие как анимации прокрутки.
    val coroutineScope = rememberCoroutineScope()

    // Создаем вертикальный контейнер (Column), который будет содержать строку вкладок (TabRow)
    // и содержимое вкладок, отображаемое в HorizontalPager.
    Column(
        modifier = Modifier
            // Применяем закругление углов для всей колонки.
            .clip(RoundedCornerShape(5.dp))
            // Добавляем отступы вокруг компонента.
            .padding(start = 5.dp, end = 5.dp, top = 3.dp)
    ) {
        // Создаем строку вкладок (TabRow), где каждая вкладка соответствует элементу из `tabList`.
        TabRow(
            selectedTabIndex = tabIndex, // Указываем текущую активную вкладку, чтобы TabRow мог выделить её.
            // Настраиваем индикатор, который подчеркивает активную вкладку.
            indicator = { tabPositions ->
                // Индикатор смещается на позицию выбранной вкладки.
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[tabIndex])
                )
            },
            containerColor = colorResource(R.color.BlueTrans), // Задаем цвет фона строки вкладок.
            contentColor = Color.White // Устанавливаем белый цвет текста вкладок.
        ) {
            // Перебираем элементы списка вкладок `tabList` с их индексами.
            tabList.forEachIndexed { index, s ->
                // Создаем отдельную вкладку (Tab) для каждого элемента списка.
                Tab(
                    selected = tabIndex == index, // Указываем, выбрана ли текущая вкладка (true/false).
                    onClick = {
                        // При клике на вкладку запускаем анимацию прокрутки до её страницы.
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        // Отображаем текст для текущей вкладки (например, "HOURS" или "DAYS").
                        Text(text = s)
                    }
                )
            }
        }

        // Создаем горизонтальный пейджер (HorizontalPager), который будет отображать содержимое для каждой вкладки.
        com.google.accompanist.pager.HorizontalPager(
            count = tabList.size, // Указываем общее количество страниц (вкладок).
            state = pagerState, // Связываем состояние пейджера с `pagerState`, чтобы отслеживать текущую страницу.
            modifier = Modifier.weight(1.0f) // Заставляем пейджер занимать всё оставшееся пространство.
        ) { index ->
            val list = when (index){
                0 -> getWeathersByHours(currentDay.value.hours)
                1 -> dayList.value
                else -> dayList.value
            }
            MainList(list, currentDay)
        }

    }
}

private fun getWeathersByHours(hours: String): List<WeatherModel> {
    if (hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in 0 until hoursArray.length()) {
        val item = hoursArray[i] as JSONObject

        list.add(
            WeatherModel(
                "",
                item.getString("time"), item.getString("temp_c")
                    .toFloat().toInt().toString() + "°C",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "", "", "",
                item.getString("wind_kph")
            )
        )
    }
    return list
}

