package dev.baseio.slackclone.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.baseio.slackclone.data.local.dao.SlackMessageDao
import dev.baseio.slackclone.data.local.model.DBSlackMessage
import dev.baseio.slackclone.data.mapper.EntityMapper
import dev.baseio.slackclone.domain.model.channel.SlackChannel
import dev.baseio.slackclone.domain.model.message.SlackMessage
import dev.baseio.slackclone.domain.repository.MessagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SlackMessagesRepositoryImpl @Inject constructor(
  private val slackMessageDao: SlackMessageDao,
  private val entityMapper: EntityMapper<SlackMessage, DBSlackMessage>
) :
  MessagesRepository {
  private val chatPager = Pager(PagingConfig(pageSize = 20)) {
    slackMessageDao.messagesByDate()
  }

  override fun fetchMessages(params: SlackChannel?): Flow<PagingData<SlackMessage>> {
    return chatPager.flow.map { messages ->
      messages.map { message ->
        entityMapper.mapToDomain(message)
      }
    }
  }
}