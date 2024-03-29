package com.example.springJobsChannelsFlows.entity


import kotlinx.coroutines.flow.Flow
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

import java.time.LocalDate

@Document
data class DailyInspection(
    @Id
    var id:String? = null,
    var specId:Int? = 10101,
    val inspectionReports: MutableList<InspectionReport> = mutableListOf(),
    val currentDate:LocalDate? = LocalDate.of(2001,12,1)

    )

@Document
data class InspectionReport(
    @Id
    var id:String? = null,
    var specId:Int? = 10101,
    val currentDate:LocalDate? = LocalDate.of(2001,12,1),
    val trucksInLine: MutableList<Truck> = mutableListOf<Truck>(),
    val inspectors:MutableList<Inspector> = mutableListOf<Inspector>(),
    val trucksOutOfLine:MutableList<Truck> = mutableListOf<Truck>(),
    var notes: MutableList<String> = mutableListOf<String>()
){
    fun addTruckToLine(truck: Truck):Truck{
        try{
            trucksInLine.add(truck)
        }catch (ex:TruckException){
            ex.printStackTrace()
        }
        finally {
            return truck
        }

    }
    fun removeTruckFromLine(truck: Truck):Truck{
        if(truck == null) throw TruckException()
        trucksInLine.remove(truck)
        return truck
    }
}

@Document
data class Truck(
    @Id
    var id:String? = null,
    var specId:Int? = 10101,
    var licenseNo:String? = "Alabama-101",
    var driverName:String? = "fidel"
)

@Document
data class Inspector(
    @Id
    var id:String? = null,
    var specId:Int? = 10101,
    val name:String? = "Inspector Biden",
    val division:InspectorDivision? = InspectorDivision.BEGINNER
){
    constructor(specId: Int?, name: String?, division: InspectorDivision?)
            : this(null, specId, name, division)
}

enum class InspectorDivision {
    MGMT, BEGINNER, WEIGHT, CRIME, AGRICULTURE, GOODS
}

@Repository
interface TruckRepository : CoroutineCrudRepository<Truck, String> {
    suspend fun findBySpecId(specId: Int): Truck
}

@Repository
interface InspectorRepository : CoroutineCrudRepository<Inspector, String> {
    suspend fun findBySpecId(specId: Int): Inspector
}

@Repository
interface InspectionReportRepository: CoroutineCrudRepository<InspectionReport, String>{
    suspend fun findBySpecId(specId: Int): InspectionReport
    suspend fun findByCurrentDate(currentDate:LocalDate): Flow<InspectionReport>

}

@Repository
interface DailyAllReportRepository: CoroutineCrudRepository<DailyInspection, String>{
    suspend fun findBySpecId(specId: Int): DailyInspection
    suspend fun findByCurrentDate(currentDate:LocalDate): Flow<DailyInspection>
}