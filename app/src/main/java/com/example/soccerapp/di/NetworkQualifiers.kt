package com.example.soccerapp.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FootballNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CommentNetwork


//Hiltが識別できるようにするために。返り値に注目。返す値に被りがある場合は関数にも書く。