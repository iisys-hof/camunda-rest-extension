package de.hofuniversity.iisys.camunda.rest.dto;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;

public class ExtProcessDefDTO extends ProcessDefinitionDto
{
    private String linkedByType;
    private String linkedByValue;
    
    private final Map<String, Object> metadata;
    
    public ExtProcessDefDTO()
    {
        metadata = new HashMap<String, Object>();
    }
    
    public String getLinkedByType()
    {
        return linkedByType;
    }
    
    public void setLinkedByType(String type)
    {
        linkedByType = type;
    }
    
    public String getLinkedByValue()
    {
        return linkedByValue;
    }
    
    public void setLinkedByValue(String value)
    {
        linkedByValue = value;
    }
    
    public Map<String, Object> getMetadata()
    {
        return metadata;
    }
    
    public static ExtProcessDefDTO fromDefinition(ProcessDefinition definition)
    {
        ExtProcessDefDTO dto = new ExtProcessDefDTO();
        
        dto.id = definition.getId();
        dto.key = definition.getKey();
        dto.category = definition.getCategory();
        dto.description = definition.getDescription();
        dto.name = definition.getName();
        dto.version = definition.getVersion();
        dto.resource = definition.getResourceName();
        dto.deploymentId = definition.getDeploymentId();
        dto.diagram = definition.getDiagramResourceName();
        dto.suspended = definition.isSuspended();
        
        return dto;
    }
}
