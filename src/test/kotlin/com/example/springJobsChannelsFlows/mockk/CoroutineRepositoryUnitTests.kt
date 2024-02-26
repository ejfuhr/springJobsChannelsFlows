package com.example.springJobsChannelsFlows.mockk

import com.mongodb.client.result.DeleteResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Mono

class CoroutineRepositoryUnitTests {

    val operations = mockk<ReactiveMongoOperations>(relaxed = true)
    lateinit var repositoryFactory: ReactiveMongoRepositoryFactory

    @BeforeEach
    fun before() {

        every { operations.getConverter() } returns MappingMongoConverter(NoOpDbRefResolver.INSTANCE, MongoMappingContext())
        repositoryFactory = ReactiveMongoRepositoryFactory(operations)
    }

    @Test // DATAMONGO-2601
    fun `should discard result of suspended query method without result`() {

        every { operations.remove(any(), any(), any()) } returns Mono.just(DeleteResult.acknowledged(1))

        val repository = repositoryFactory.getRepository(PersonRepository::class.java)

        runBlocking {
            repository.deleteAllByName("foo")
        }
    }

    interface PersonRepository : CoroutineCrudRepository<Person, Long> {

        suspend fun deleteAllByName(name: String)
    }

    data class Person(@Id var id: Long, var name: String)
}