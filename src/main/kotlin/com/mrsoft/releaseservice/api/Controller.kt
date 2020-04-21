package com.mrsoft.releaseservice.api

import com.mrsoft.releaseservice.domain.ReleaseManagerDomainService
import com.mrsoft.releaseservice.domain.ServiceVersion
import com.mrsoft.releaseservice.domain.SystemVersion
import com.mrsoft.releaseservice.persistence.DataNotFoundExecption
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

data class ServiceVersionDto(val serviceName : String, val serviceVersionNumber : Int)
data class DeployRequest(val serviceName : String, val serviceVersionNumber : Int)
data class DeployResponse(val systemVersion: Int)

@RestController
class Controller(private val releaseManager: ReleaseManagerDomainService) {

    @GetMapping("/services")
    fun getServices(@RequestParam(value = "systemVersion") systemVersion: Int?) : List<ServiceVersionDto> {

        val getServicesResult =
                releaseManager.getServices(systemVersion
                        ?.let { SystemVersion.SpecificSystemVersion(it) }
                        ?: SystemVersion.CurrentSystemVersion)

        if (getServicesResult.exceptionOrNull() is DataNotFoundExecption) throw ResponseStatusException(HttpStatus.NOT_FOUND, null, getServicesResult.exceptionOrNull())

        return getServicesResult.getOrThrow().map { ServiceVersionDto(it.serviceName, it.serviceVersionNumber) }
    }

    @PostMapping("/deploy")
    fun doDeploy(@RequestBody deployRequest : DeployRequest) : DeployResponse {
        return releaseManager
                .deployServiceVersion(ServiceVersion(deployRequest.serviceName, deployRequest.serviceVersionNumber))
                .getOrThrow()
                .let { DeployResponse(it.version) }

    }
}
