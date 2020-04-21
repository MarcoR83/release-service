package com.mrsoft.releaseservice.domain

import com.mrsoft.releaseservice.persistence.DataNotFoundExecption
import com.mrsoft.releaseservice.persistence.SystemRepository

class ReleaseManagerDomainService(private val systemRepository : SystemRepository) {

    fun getServices(systemVersion: SystemVersion) : Result<Collection<ServiceVersion>> {
        return systemRepository.readSystemVersion(systemVersion).map { it.second.getServices() }
    }

    fun deployServiceVersion(newServiceVersion: ServiceVersion) : Result<SystemVersion.SpecificSystemVersion> {
        return systemRepository.readSystemVersion(SystemVersion.CurrentSystemVersion).let {
            when {
                it.isFailure && it.exceptionOrNull() is DataNotFoundExecption -> Result.success(Pair(SystemVersion.SpecificSystemVersion(0), System()))
                else -> it
            }
        }.mapCatching { (currentSystemVersion, currentSystem) ->
            if (currentSystem.mayUpdateServiceVersion(newServiceVersion)) {
                systemRepository.createSystemVersion(currentSystem, currentSystemVersion).getOrThrow()
            } else currentSystemVersion
        }
    }
}