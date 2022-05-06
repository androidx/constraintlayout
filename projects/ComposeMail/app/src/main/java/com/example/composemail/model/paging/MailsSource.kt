package com.example.composemail.model.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.composemail.model.data.MailEntryInfo
import com.example.composemail.model.repo.MailRepository

class MailsSource(private val mailRepo: MailRepository) : PagingSource<Int, MailEntryInfo>() {
    override fun getRefreshKey(state: PagingState<Int, MailEntryInfo>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MailEntryInfo> {
        val nextPage = params.key ?: 0
        val nextMails = mailRepo.getNextSetOfConversations(params.loadSize)
        return LoadResult.Page(
            data = nextMails.conversations,
            prevKey = if (nextPage == 0) null else nextMails.page - 1,
            nextKey = nextMails.page + 1,
            itemsAfter = 1
        )
    }
}