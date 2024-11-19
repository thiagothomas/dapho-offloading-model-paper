package unisinos.model.execution;

import unisinos.model.message.ServiceType;

public record ServiceExecution(ServiceType serviceName, double ranking)
{

}