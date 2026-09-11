package com.example.soccerapp.di


import com.example.soccerapp.data.repository.CommentRepository
import com.example.soccerapp.data.repository.NetworkCommentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommentRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        implementation: NetworkCommentRepository,
    ): CommentRepository
}