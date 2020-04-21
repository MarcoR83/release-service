package com.mrsoft.releaseservice.domain

class System {
    private val serviceVersions = mutableMapOf<String, ServiceVersion>()

    fun getServices() : Collection<ServiceVersion> {
        return serviceVersions.values
    }

    fun mayUpdateServiceVersion(newServiceVersion : ServiceVersion) : Boolean {
        val serviceKey = newServiceVersion.serviceName.toLowerCase()
        val existingVersion = serviceVersions[serviceKey]?.serviceVersionNumber ?: 0
        return if (newServiceVersion.serviceVersionNumber != existingVersion) {
            serviceVersions[serviceKey] = newServiceVersion
            true
        } else false
    }
    fun clone() : System {
        val new = System()
        new.serviceVersions += this.serviceVersions
        return new
    }
}

sealed class SystemVersion {
    data class SpecificSystemVersion(val version : Int ) : SystemVersion()
    object CurrentSystemVersion : SystemVersion()
}

data class ServiceVersion(val serviceName : String, val serviceVersionNumber : Int)
