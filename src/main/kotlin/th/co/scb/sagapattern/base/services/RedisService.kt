package th.co.scb.sagapattern.base.services

import th.co.scb.sagapattern.base.models.RequestModelBase
import th.co.scb.sagapattern.test.DASH

class RedisService {

    //Method to generate key
    fun generateKey(name: String, uuid: String): String {
        return name + DASH + uuid
    }

    //Method to store data into redis
    fun storeData(key: String, data: RequestModelBase) {
        //generate code to store data into redis

    }

    //Method to retrieve data from redis
    fun retrieveData(key: String): RequestModelBase {
        //generate code to retrieve data from redis
        return RequestModelBase("", "", emptySet())
    }

    //Method to delete data from redis
    fun deleteData(key: String) {
        //generate code to delete data from redis
    }

    //Method to check key in redis
    fun checkKey(key: String): Boolean {
        //generate code to check data in redis
        return true
    }

    //Method to update data in redis
    fun updateData(key: String, data: RequestModelBase) {
        //generate code to update data in redis
    }
}