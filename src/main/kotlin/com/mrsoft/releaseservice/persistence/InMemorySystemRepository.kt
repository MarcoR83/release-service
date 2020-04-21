package com.mrsoft.releaseservice.persistence

import com.mrsoft.releaseservice.domain.System
import com.mrsoft.releaseservice.domain.SystemVersion
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class InMemorySystemRepository : SystemRepository {

    //TODO: repository should use a virtual document store (interface) and store and retrieve systemVersions and the currentVersion from there
    private val systemVersions = ConcurrentHashMap<SystemVersion.SpecificSystemVersion, System>()
    private val currentVersion = AtomicReference(SystemVersion.SpecificSystemVersion(0))

    override fun readSystemVersion(version: SystemVersion): Result<Pair<SystemVersion.SpecificSystemVersion, System>> {
        return when(version) {
            is SystemVersion.SpecificSystemVersion -> {
                getSpecificSystemVersion(version)
            }
            is SystemVersion.CurrentSystemVersion -> {
                getSpecificSystemVersion(currentVersion.get())
            }
        }
    }

    private fun getSpecificSystemVersion(version : SystemVersion.SpecificSystemVersion) : Result<Pair<SystemVersion.SpecificSystemVersion, System>> {
        return systemVersions[version].let { if (it != null) Result.success(Pair(version, it.clone())) else Result.failure(DataNotFoundExecption()) }
    }

    override fun createSystemVersion(newSystem: System, expectedVersion: SystemVersion.SpecificSystemVersion) : Result<SystemVersion.SpecificSystemVersion> {
        if (expectedVersion != currentVersion.get()) {
            return Result.failure(StaleDataException())
        }
        val newVersion = SystemVersion.SpecificSystemVersion(expectedVersion.version + 1)

        if (systemVersions.putIfAbsent(newVersion, newSystem) != null) {
            return Result.failure(StaleDataException())
        }
        //TODO: what happens when creation of newVersion was successful  but update of currentVersion fails?
        // retry with next highest number (self-heal) vs manual intervention
        currentVersion.set(newVersion)
        return Result.success(newVersion)
    }
}