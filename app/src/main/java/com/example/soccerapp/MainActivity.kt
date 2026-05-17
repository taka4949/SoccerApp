package com.example.soccerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class M1(
    val id: Int,
    val league: String,
    val t1: String,
    val t2: String,
    val s1: Int,
    val s2: Int,
    val time1: Int
)

data class C1(
    val u1: String,
    val msg1: String,
    val ts1: Long
)

class MainViewModel : ViewModel() {//画面遷移管理
    var screenState by mutableIntStateOf(0)
    var selectedLeague by mutableStateOf("")
    var selectedMatch by mutableStateOf<M1?>(null)

    val matches = mutableStateListOf(//サンプルデータ
        M1(1, "CL", "Real Madrid", "Man City", 0, 0, 10),
        M1(2, "CL", "Bayern", "Arsenal", 1, 0, 45),
        M1(3, "Premier", "Liverpool", "Chelsea", 2, 2, 70),
        M1(4, "J-League", "Urawa", "Gamba", 0, 1, 30)
    )
    val comments = mutableStateListOf<C1>()

    fun loadComments(matchId: Int) {
        comments.clear()
    }

    fun sendComment(matchId: Int, text: String) {
        comments.add(C1("User", text, System.currentTimeMillis()))
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel = remember { MainViewModel() }
                    MainNavigation(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(viewModel: MainViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(when(viewModel.screenState) {
                        0 -> "League Selection"
                        1 -> viewModel.selectedLeague
                        else -> if (viewModel.selectedMatch != null) "${viewModel.selectedMatch!!.t1} vs ${viewModel.selectedMatch!!.t2}" else ""
                    })
                },
                navigationIcon = {
                    if (viewModel.screenState > 0) {
                        IconButton(onClick = { viewModel.screenState-- }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                }
            )
        }
    ) { p ->
        Box(modifier = Modifier.padding(p)) {
            when (viewModel.screenState) {
                0 -> LeagueListScreen { league ->
                    viewModel.selectedLeague = league
                    viewModel.screenState = 1
                }
                1 -> {
                    val filtered = viewModel.matches.filter { it.league == viewModel.selectedLeague }
                    MatchListScreen(filtered) { match ->
                        viewModel.selectedMatch = match
                        viewModel.loadComments(match.id)
                        viewModel.screenState = 2
                    }
                }
                2 -> if (viewModel.selectedMatch != null) {
                    ThreadScreen(
                        m = viewModel.selectedMatch!!,
                        comments = viewModel.comments,
                        onSendComment = { text -> viewModel.sendComment(viewModel.selectedMatch!!.id, text) }
                    )
                }
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
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(text = league, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MatchListScreen(matchList: List<M1>, onSelect: (M1) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(matchList) { m ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelect(m) },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = m.t1, modifier = Modifier.weight(1f))
                    Text(text = "${m.s1} - ${m.s2}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text(text = m.t2, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }
            }
        }
    }
}

@Composable
fun ThreadScreen(m: M1, comments: List<C1>, onSendComment: (String) -> Unit) {
    var input1 by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
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

        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = input1,
                onValueChange = { input1 = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Comment...") }
            )
            IconButton(onClick = {
                if (input1.isNotBlank()) {
                    onSendComment(input1)
                    input1 = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = null)
            }
        }
    }
}