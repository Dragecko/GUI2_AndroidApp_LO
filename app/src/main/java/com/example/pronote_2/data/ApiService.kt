package com.example.pronote_2.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.net.UnknownHostException
import java.net.ConnectException

object ApiService {
    private const val BASE_URL = "http://10.0.2.2:3000/api/grades"
    
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }
    
    suspend fun getAllGrades(): Result<List<Grade>> {
        return try {
            val response: GradesListResponse = client.get(BASE_URL).body()
            if (response.success) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Erreur lors de la récupération des notes"))
            }
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Impossible de se connecter au serveur"))
        } catch (e: ConnectException) {
            Result.failure(Exception("Connexion refusée. Vérifiez l'URL du serveur."))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }
    
    suspend fun createGrade(grade: Grade): Result<Grade> {
        return try {
            val response: GradeResponse = client.post(BASE_URL) {
                contentType(ContentType.Application.Json)
                setBody(grade.copy(_id = null))
            }.body()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Erreur lors de la création"))
            }
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Impossible de se connecter au serveur. Vérifiez que le serveur est démarré."))
        } catch (e: ConnectException) {
            Result.failure(Exception("Connexion refusée. Vérifiez l'URL du serveur."))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }
    
    suspend fun updateGrade(id: String, grade: Grade): Result<Grade> {
        return try {
            val response: GradeResponse = client.put("$BASE_URL/$id") {
                contentType(ContentType.Application.Json)
                setBody(grade.copy(_id = null))
            }.body()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Erreur lors de la mise à jour"))
            }
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Impossible de se connecter au serveur. Vérifiez que le serveur est démarré."))
        } catch (e: ConnectException) {
            Result.failure(Exception("Connexion refusée. Vérifiez l'URL du serveur."))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }
    
    suspend fun deleteGrade(id: String): Result<Unit> {
        return try {
            val response: GradeResponse = client.delete("$BASE_URL/$id").body()
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Erreur lors de la suppression"))
            }
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Impossible de se connecter au serveur. Vérifiez que le serveur est démarré."))
        } catch (e: ConnectException) {
            Result.failure(Exception("Connexion refusée. Vérifiez l'URL du serveur."))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur: ${e.message ?: "Erreur inconnue"}"))
        }
    }
}
 