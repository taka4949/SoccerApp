package com.example.soccerapp



import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}



//テスト中だけ Dispatchers.Main をテスト用に差し替える

//Dispatchers.Main を使うCoroutineをローカルUnit Testで動かすときに使うRule

//viewModelScope は基本的に Dispatchers.Main を使う↓（本来）

//MatchThreadViewModel
//    ↓
//viewModelScope.launch
//    ↓
//Dispatchers.Main
//    ↓
//AndroidのMain Thread


//テスト時（src/test）はAndroid端末ではなくPCのJVM
//Dispatchers.Main
//      ↓
//TestDispatcherに差し替える必要がある。