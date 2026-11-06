package com.example.myapplication.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepService {
    @GET("{cep}/json/")
    suspend fun buscar(@Path("cep") cep: String): Endereco
}

object ViaCepApi{
    val service: ViaCepService by lazy {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepService::class.java)
    }
}
