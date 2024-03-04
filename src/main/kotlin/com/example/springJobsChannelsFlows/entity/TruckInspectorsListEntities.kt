package com.example.springJobsChannelsFlows.entity

import kotlinx.coroutines.flow.Flow
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Document
data class TruckInspectorList (
    @Id
    var id:String? = null,
    val specId: Int = 10101,
    val currentDate: LocalDate? = LocalDate.of(2001,12,1),
    val inspectorsList:MutableList<Inspector> = mutableListOf(),
    val truckList:MutableList<Truck> = mutableListOf(),
    val status:InspectionStatus? = InspectionStatus.PASSED
)

enum class InspectionStatus{
    PASSED, TICKETED, CRIMINAL
}

@Repository
interface TruckInspectorsRepository : CoroutineCrudRepository<TruckInspectorList, String> {
    suspend fun findBySpecId(specId: Int):TruckInspectorList
    suspend fun findByCurrentDate(currentDate:LocalDate): Flow<TruckInspectorList>
}