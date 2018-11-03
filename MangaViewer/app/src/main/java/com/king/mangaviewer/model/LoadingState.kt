package com.king.mangaviewer.model

sealed class LoadingState {
    object Loading : LoadingState()
    object Idle : LoadingState()
}