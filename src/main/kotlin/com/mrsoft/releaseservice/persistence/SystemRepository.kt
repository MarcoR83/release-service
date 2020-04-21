package com.mrsoft.releaseservice.persistence

import com.mrsoft.releaseservice.domain.System
import com.mrsoft.releaseservice.domain.SystemVersion

class DataNotFoundExecption : Exception()
class StaleDataException : Exception()

interface SystemRepository {
    fun readSystemVersion(version : SystemVersion) : Result<Pair<SystemVersion.SpecificSystemVersion, System>>
    fun createSystemVersion(newSystem: System, expectedVersion: SystemVersion.SpecificSystemVersion) : Result<SystemVersion.SpecificSystemVersion>
}