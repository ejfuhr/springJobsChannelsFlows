package com.example.springJobsChannelsFlows.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Document
data class VariousNotes(
    @Id
    var id:String? = null,
    var specId:Int? = 10101,
    var notes: MutableList<String> = mutableListOf<String>()
)

@Repository
interface VariousNotesRepository : CoroutineCrudRepository<VariousNotes, String> {

    suspend fun findBySpecId(specId: Int): VariousNotes
}