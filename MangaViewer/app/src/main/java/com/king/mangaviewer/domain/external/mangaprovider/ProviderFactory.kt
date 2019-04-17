package com.king.mangaviewer.domain.external.mangaprovider

import com.king.mangaviewer.model.MangaWebSource
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Provider

interface ProviderFactory {
    fun getPattern(type: MangaWebSource): MangaProvider
}

class ProviderFactoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val providerMap: MutableMap<Class<out MangaProvider>, Provider<MangaProvider>>
) : ProviderFactory {
    override fun getPattern(type: MangaWebSource): MangaProvider {
        try {
            val provider = providerMap[Class.forName(type.className)]!!.get()
            provider.okHttpClient = okHttpClient
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
