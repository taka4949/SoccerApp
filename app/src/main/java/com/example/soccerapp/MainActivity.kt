package com.example.soccerapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.*

// --- Data Models ---
data class M1(
    val id: Int,
    val league: String,
    val t1: String, val t2: String,
    var s1: Int, var s2: Int,
    var time1: Int
)

data class C1(
    val u1: String,
    val msg1: String,
    val ts1: Long
)

// --- Logic ---
class Logic1 {
    fun update1(m: M1): M1 {
        val r = Random()
        if (r.nextInt(100) == 1) m.s1++
        if (r.nextInt(120) == 1) m.s2++
        if (m.time1 < 90) m.time1++
        return m
    }
}

// --- Main UI ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainNavigation()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val logic = remember { Logic1() }

    // 画面遷移管理 (0: リーグ一覧, 1: 試合一覧, 2: スレッド)
    var screenState by remember { mutableIntStateOf(0) }
    var selectedLeague by remember { mutableStateOf("") }
    var selectedMatch by remember { mutableStateOf<M1?>(null) }

    // サンプルデータ
    val matches = remember {
        mutableStateListOf(
            M1(1, "CL", "Real Madrid", "Man City", 0, 0, 10),
            M1(2, "CL", "Bayern", "Arsenal", 1, 0, 45),
            M1(3, "Premier", "Liverpool", "Chelsea", 2, 2, 70),
            M1(4, "J-League", "Urawa", "Gamba", 0, 1, 30)
        )
    }

    // スコア自動更新
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            for (i in matches.indices) {
                matches[i] = logic.update1(matches[i].copy())
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(when(screenState) {
                        0 -> "League Selection"
                        1 -> selectedLeague
                        else -> "${selectedMatch?.t1} vs ${selectedMatch?.t2}"
                    })
                },
                navigationIcon = {
                    if (screenState > 0) {
                        IconButton(onClick = { screenState-- }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                }
            )
        }
    ) { p ->
        Box(modifier = Modifier.padding(p)) {
            when (screenState) {
                0 -> LeagueListScreen { league ->
                    selectedLeague = league
                    screenState = 1
                }
                1 -> MatchListScreen(matches.filter { it.league == selectedLeague }) { match ->
                    selectedMatch = match
                    screenState = 2
                }
                2 -> ThreadScreen(selectedMatch!!)
            }
        }
    }
}

@Composable
fun LeagueListScreen(onSelect: (String) -> Unit) {
    val leagues = listOf("CL", "Premier", "J-League")
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        leagues.forEach { league ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onSelect(league) },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(text = league, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MatchListScreen(list: List<M1>, onSelect: (M1) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        items(list) { m ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelect(m) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = m.t1, modifier = Modifier.weight(1f))
                    Text(text = "${m.s1} - ${m.s2}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text(text = m.t2, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                }
            }
        }
    }
}

@Composable
fun ThreadScreen(m: M1) {
    val comments = remember { mutableStateListOf<C1>() }
    var input1 by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // 固定スコア表示
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${m.time1}'", color = Color.Red, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = m.t1, fontWeight = FontWeight.Bold)
                    Text(text = " ${m.s1} - ${m.s2} ", fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Text(text = m.t2, fontWeight = FontWeight.Bold)
                }
            }
        }

        // コメント一覧 (9割以上のエリア)
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(8.dp)) {
            items(comments) { c ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(text = "Anonymous User", fontSize = 11.sp, color = Color.Gray)
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(text = c.msg1, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }

        // 入力エリア
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = input1,
                onValueChange = { input1 = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Comment...") }
            )
            IconButton(onClick = {
                if (input1.isNotBlank()) {
                    comments.add(C1("User", input1, System.currentTimeMillis()))
                    input1 = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = null)
            }
        }
    }
}