package com.king.mangaviewer.domain.external.mangaprovider

import com.king.mangaviewer.domain.data.local.DownloadedManga
import com.king.mangaviewer.domain.data.local.DownloadedMangaMenu
import com.king.mangaviewer.domain.repository.DownloadedMangaRepository
import com.king.mangaviewer.model.MangaMenuItem
import io.reactivex.Single
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)

class DownloadedMangaProviderTest : MangaProviderTestBase() {

    private val downloadedMangaRepository: DownloadedMangaRepository = mock(
        DownloadedMangaRepository::class.java)

    @Before
    override fun setup() {
        super.setup()
        val mockMangaList = mutableListOf<DownloadedManga>()
        mockMangaList.add(DownloadedManga("", "", "", "", "", "",
            DownloadedMangaMenu("", "", "", "", "", "", "")))
        `when`(downloadedMangaRepository.getMangaMenuList()).thenReturn(Single.just(mockMangaList))

    }

    override fun getProvider(): MangaProvider {
        return DownloadedMangaProvider(downloadedMangaRepository)
    }

    override fun getMangaMenu(): MangaMenuItem {
        TODO(
            "not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMangaPageUrl(): String {
        TODO(
            "not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}