package com.mrsoft.releaseservice.infrastructure

import com.mrsoft.releaseservice.domain.ReleaseManagerDomainService
import com.mrsoft.releaseservice.persistence.InMemorySystemRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.mrsoft.releaseservice.api"])
class ReleaseServiceApplication {
	@Bean
	fun releaseManagerDomainService() : ReleaseManagerDomainService {
		return ReleaseManagerDomainService(InMemorySystemRepository())
	}
}

fun main(args: Array<String>) {
	runApplication<ReleaseServiceApplication>(*args)
}
