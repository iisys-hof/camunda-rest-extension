package de.hofuniversity.iisys.camunda.rest.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionQueryDto;
import org.camunda.bpm.engine.rest.exception.RestException;
import org.camunda.bpm.engine.rest.impl.AbstractRestProcessEngineAware;
import org.camunda.bpm.engine.rest.spi.ProcessEngineProvider;
import org.camunda.bpm.engine.rest.sub.repository.impl.DeploymentResourceImpl;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.RootElement;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.hofuniversity.iisys.camunda.rest.dto.ExtProcessDefDTO;

public class SchubExtProcessDefinitionServiceImpl extends AbstractRestProcessEngineAware
    implements SchubExtProcessDefinitionService
{
    //TODO: configurability?
    private static final String PROC_ENGINE_NAME = "default";
    
    protected ObjectMapper fObjectMapper = new ObjectMapper();
    protected ProcessEngine fProcessEngine;

    public SchubExtProcessDefinitionServiceImpl()
    {
        super(PROC_ENGINE_NAME, new ObjectMapper());
        
        ProcessEngineProvider prov = getProcessEngineProvider();
        fProcessEngine = prov.getProcessEngine(PROC_ENGINE_NAME);
    }
    
    
    @Override
    public List<ExtProcessDefDTO> getProcessDefinitions(
        UriInfo uriInfo, Integer firstResult, Integer maxResults)
    {
        List<ExtProcessDefDTO> list = new ArrayList<ExtProcessDefDTO>();
        
        ProcessDefinitionQueryDto queryDto = new ProcessDefinitionQueryDto(
            fObjectMapper, uriInfo.getQueryParameters());
        ProcessDefinitionQuery query = queryDto.toQuery(fProcessEngine);
        
        List<ProcessDefinition> matching = null;
        
        if (firstResult == null)
        {
            firstResult = 0;
        }
        if (maxResults == null)
        {
            maxResults = Integer.MAX_VALUE;
        }
        matching = query.listPage(firstResult, maxResults);
        
        
        for (ProcessDefinition definition : matching)
        {
            ExtProcessDefDTO def = ExtProcessDefDTO.fromDefinition(definition);
            
            BpmnModelInstance model = fProcessEngine.getRepositoryService()
                .getBpmnModelInstance(definition.getId());
            
            if(model != null)
            {
                readMetadata(def, model);
            }
            
            list.add(def);
        }
        
        return list;
    }

    @Override
    public ExtProcessDefDTO
        getProcessDefinitionById(String processDefinitionId)
    {
        ExtProcessDefDTO def = null;
        
        
        ProcessDefinition procDef = fProcessEngine.getRepositoryService()
            .getProcessDefinition(processDefinitionId);
        if(procDef != null)
        {
            def = ExtProcessDefDTO.fromDefinition(procDef);
        }
        
        BpmnModelInstance model = fProcessEngine.getRepositoryService()
            .getBpmnModelInstance(processDefinitionId);
        
        
        if(model != null && def != null)
        {
            readMetadata(def, model);
        }

        return def;
    }
    
    private void readMetadata(ExtProcessDefDTO def, BpmnModelInstance model)
    {
        Map<String, Object> metadata = def.getMetadata();
        
        for(RootElement element : model.getDefinitions().getRootElements())
        {
            ExtensionElements extensionElements = element.getExtensionElements();
            
            if (extensionElements == null)
            {
                extensionElements = model.newInstance(ExtensionElements.class);
                element.setExtensionElements(extensionElements);
            }
            
            for(ModelElementInstance ei : extensionElements.getElements())
            {
                String key = ei.getAttributeValue("key");
                String value = ei.getRawTextContent();
                
                metadata.put(key, value);
            }
        }
    }

    



    @Override
    public ExtProcessDefDTO setMetaValue(String processDefinitionId,
        String key, String value)
    {
        ExtProcessDefDTO def = null;
        
        ProcessDefinition procDef = fProcessEngine.getRepositoryService()
            .getProcessDefinition(processDefinitionId);
        if(procDef != null)
        {
            def = ExtProcessDefDTO.fromDefinition(procDef);
        }
        
        BpmnModelInstance model = fProcessEngine.getRepositoryService()
            .getBpmnModelInstance(processDefinitionId);
        
        if(model != null && def != null)
        {
            for(RootElement element : model.getDefinitions().getRootElements())
            {
                ExtensionElements extensionElements = element.getExtensionElements();
                
                if (extensionElements == null)
                {
                    extensionElements = model.newInstance(ExtensionElements.class);
                    element.setExtensionElements(extensionElements);
                }
                
                ModelElementInstance entry = null;
                
                for(ModelElementInstance ei : extensionElements.getElements())
                {
                    if(key.equals(ei.getAttributeValue("key")))
                    {
                        entry = ei;
                        break;
                    }
                }
                
                if(entry == null)
                {
                    entry = extensionElements.addExtensionElement(
                        "https://www.sc-hub.de/bpmn", "metadataElement");
                    entry.setAttributeValue("key", key);
                    entry.setTextContent(value);
                }
                else
                {
                    entry.setTextContent(value);
                }
                
//                NuxeoAssociations nuxeoAssocs = null;
//                Collection<NuxeoAssociations> assocs = null;
//                
//                try
//                {
//                    assocs = extensionElements
//                        .getChildElementsByType(NuxeoAssociations.class);
//                }
//                catch(Exception e)
//                {
//                    
//                }
//                
//                if(assocs == null || assocs.isEmpty())
//                {
//                    nuxeoAssocs = model.newInstance(NuxeoAssociations.class);
//                    
//                    if("type".equals(key))
//                    {
//                        nuxeoAssocs.getTypeAssocs().add(value);
//                    }
//                    else if("type".equals(key))
//                    {
//                        nuxeoAssocs.getPathAssocs().add(value);
//                    }
//                    
//                    extensionElements.getElements().add(nuxeoAssocs);
//                }
//                else
//                {
//                    nuxeoAssocs = assocs.iterator().next();
//                    
//                    if("type".equals(key))
//                    {
//                        nuxeoAssocs.getTypeAssocs().add(value);
//                    }
//                    else if("type".equals(key))
//                    {
//                        nuxeoAssocs.getPathAssocs().add(value);
//                    }
//                }
            }
            
            readMetadata(def, model);
        }
        
        DeploymentResourceImpl dri = new DeploymentResourceImpl(PROC_ENGINE_NAME,
            procDef.getDeploymentId(), relativeRootResourcePath, fObjectMapper);
        String name = dri.getDeployment().getName();
        
        
        DeploymentBuilder builder = fProcessEngine.getRepositoryService().createDeployment();
        builder.name(name);
        builder.addModelInstance(procDef.getResourceName(), model);
        builder.enableDuplicateFiltering(true);
        builder.deploy();
        
        return def;
    }
    
    
    
    
    
    protected ProcessEngineProvider getProcessEngineProvider()
    {
        ServiceLoader<ProcessEngineProvider> serviceLoader =
            ServiceLoader.load(ProcessEngineProvider.class);
        Iterator<ProcessEngineProvider> iterator = serviceLoader.iterator();
    
        if(iterator.hasNext())
        {
          ProcessEngineProvider provider = iterator.next();
          return provider;
        }
        else
        {
          throw new RestException(Status.INTERNAL_SERVER_ERROR,
              "No process engine provider found");
        }
    }
}
