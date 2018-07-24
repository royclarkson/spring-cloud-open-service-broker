/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.service;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;

/**
 *
 * @author Roy Clarkson
 */
public class ServiceInstanceEventService implements ServiceInstanceService {

	private final ServiceInstanceService service;

	public ServiceInstanceEventService(ServiceInstanceService serviceInstanceService) {
		this.service = serviceInstanceService;
	}

	@Override
	public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {
		return service.getBeforeCreateFlow()
				.then(service.createServiceInstance(request))
				.onErrorResume(e -> service.getErrorCreateFlow()
						.then(Mono.error(e)))
				.flatMap(response -> service.getAfterCreateFlow()
						.then(Mono.just(response)));
	}

	@Override
	public Mono<GetServiceInstanceResponse> getServiceInstance(GetServiceInstanceRequest request) {
		return service.getServiceInstance(request);
	}

	@Override
	public Mono<GetLastServiceOperationResponse> getLastOperation(GetLastServiceOperationRequest request) {
		return service.getLastOperation(request);
	}

	@Override
	public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {
		return service.getBeforeDeleteFlow()
				.then(service.deleteServiceInstance(request))
				.onErrorResume(e -> service.getErrorDeleteFlow()
						.then(Mono.error(e)))
				.flatMap(response -> service.getAfterDeleteFlow()
						.then(Mono.just(response)));
	}

	@Override
	public Mono<UpdateServiceInstanceResponse> updateServiceInstance(UpdateServiceInstanceRequest request) {
		return service.getBeforeUpdateFlow()
				.then(service.updateServiceInstance(request))
				.onErrorResume(e -> service.getErrorUpdateFlow()
						.then(Mono.error(e)))
				.flatMap(response -> service.getAfterUpdateFlow()
						.then(Mono.just(response)));
	}
}