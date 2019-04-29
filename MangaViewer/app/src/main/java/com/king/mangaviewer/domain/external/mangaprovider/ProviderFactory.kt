package com.king.mangaviewer.domain.external.mangaprovider

import com.king.mangaviewer.domain.repository.AppRepository
import com.king.mangaviewer.model.MangaWebSource
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Provider

interface ProviderFactory {
    fun getPattern(type: MangaWebSource): MangaProvider
}

class ProviderFactoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val providerMap: MutableMap<Class<out MangaProvider>, Provider<MangaProvider>>,
    private val appRepository: AppRepository

) : ProviderFactory {
    override fun getPattern(type: MangaWebSource): MangaProvider {
        try {
            val className = Class.forName(type.className)
            val provider = providerMap[className]!!.get()
            provider.okHttpClient = okHttpClient
            provider.mangaWebSource = appRepository.appViewModel.Setting.mangaWebSources.firstOrNull{ it.className == type.className}!!
            return provider
        } catch (e: InstantiationException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }
        throw Exception("Can not init manga provider: ${type.name}")
    }
}
