package com.king.mangaviewer.di.annotation

import com.king.mangaviewer.domain.external.mangaprovider.MangaProvider
import dagger.MapKey
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class MangaProviderKey(val value: KClass<out MangaProvider>)