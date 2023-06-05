//package com.example.switchactivity
//
//import io.ktor.client.HttpClient
//import io.ktor.client.engine.
//
//class ClientHelper {
//    companion object{
//        private var instance: ClientHelper? = null
//        fun createInstance ():ClientHelper {
//            if(instance == null){
//                instance = ClientHelper()
//                instance!!.initialize()
//            }
//            return instance!!
//        }
//
//    }
//    private var client:HttpClient? = null
//    fun initialize(){
//        client = HttpClient(OkHttp){
//
//        }
//    }
//}