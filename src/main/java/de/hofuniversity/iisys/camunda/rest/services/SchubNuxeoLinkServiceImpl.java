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
import de.hofuniversity.iisys.camunda.rest.util.WorkflowLink;

public class SchubNuxeoLinkServiceImpl extends AbstractRestProcessEngineAware
    implements SchubNuxeoLinkService
{
    //TODO: configurability?
    private static final String PROC_ENGINE_NAME = "default";

    protected ObjectMapper fObjectMapper = new ObjectMapper();
    protected ProcessEngine fProcessEngine;
    
    public SchubNuxeoLinkServiceImpl()
    {
        super(PROC_ENGINE_NAME, new ObjectMapper());
        
        ProcessEngineProvider prov = getProcessEngineProvider();
        fProcessEngine = prov.getDefaultProcessEngine();
    }

    @Override
    public List<ExtProcessDefDTO> getLinked(UriInfo uriInfo, List<String> docType,
        String path, Integer firstResult, Integer maxResults)
    {
        List<ExtProcessDefDTO> list = new ArrayList<ExtProcessDefDTO>();
        
        ProcessDefinitionQueryDto queryDto = new ProcessDefinitionQueryDto(
            fObjectMapper, uriInfo.getQueryParameters());
        ProcessDefinitionQuery query = queryDto.toQuery(fProcessEngine);
        
        List<ProcessDefinition> matching = query.list();
        
        if(firstResult == null)
        {
            firstResult = 0;
        }
        if(maxResults == null)
        {
            maxResults = 0;
        }
        int index = -1;
        for(ProcessDefinition def : matching)
        {
            BpmnModelInstance model = fProcessEngine.getRepositoryService()
                .getBpmnModelInstance(def.getId());
            
            WorkflowLink link = isLinked(model, docType, path);
            //add only matching entries
            if(link != null)
            {
                //skip results as instructed
                ++index;
                if(index < firstResult)
                {
                    continue;
                }
                
                ExtProcessDefDTO defDto = ExtProcessDefDTO.fromDefinition(def);
                
                if(model != null)
                {
                    readMetadata(defDto, model);
                }
                
                //add information about link
                defDto.setLinkedByType(link.getType());
                defDto.setLinkedByValue(link.getValue());
                
                list.add(defDto);
                
                //limit number of results
                if(maxResults > 0
                    && list.size() == maxResults)
                {
                    break;
                }
            }
        }
        
        return list;
    }
    
    private WorkflowLink isLinked(BpmnModelInstance model, List<String> docType,
        String path)
    {
        WorkflowLink link = null;
        
        RootElement root = model.getDefinitions().getRootElements().iterator().next();
        
        ExtensionElements extensionElements = root.getExtensionElements();
        
        if (extensionElements == null)
        {
            extensionElements = model.newInstance(ExtensionElements.class);
            root.setExtensionElements(extensionElements);
        }
        
        for(ModelElementInstance ei : extensionElements.getElements())
        {
            String key = ei.getAttributeValue("key");
            String value = ei.getRawTextContent();
            

            //check for document type links
            if("type".equals(key)
                && docType != null && !docType.isEmpty()
                && docType.contains(value))
            {
                link = new WorkflowLink("type", value);
                break;
            }
            
            //check path links for subdirectories
            else if("path".equals(key)
                && path != null && !path.isEmpty()
                && path.startsWith(value))
            {
                link = new WorkflowLink("path", value);
                break;
            }
        }
        
        
        return link;
    }

    @Override
    public void createLink(String processDefinitionId,
        String docType, String path)
    {
        ProcessDefinition procDef = fProcessEngine.getRepositoryService()
            .getProcessDefinition(processDefinitionId);
        
        BpmnModelInstance model = fProcessEngine.getRepositoryService()
            .getBpmnModelInstance(processDefinitionId);
        
        //create link(s)
        if(docType != null && !docType.isEmpty())
        {
            link(procDef, model, "type", docType);
        }
        if(path != null && !path.isEmpty())
        {
            link(procDef, model, "path", path);
        }
    }
    
    private void link(ProcessDefinition procDef, BpmnModelInstance model,
        String link, String value)
    {
        boolean linked = false;
        
        RootElement root = model.getDefinitions().getRootElements().iterator().next();
        
        ExtensionElements extensionElements = root.getExtensionElements();
        
        if (extensionElements == null)
        {
            extensionElements = model.newInstance(ExtensionElements.class);
            root.setExtensionElements(extensionElements);
        }
        
        //check if there already is a link
        for(ModelElementInstance ei : extensionElements.getElements())
        {
            String key = ei.getAttributeValue("key");
            
            if(link.equals(key)
                && value.equals(ei.getRawTextContent()))
            {
                linked = true;
                break;
            }
            
        }
        
        //create new link
        if(!linked)
        {
            ModelElementInstance entry = extensionElements.addExtensionElement(
                "https://www.sc-hub.de/bpmn", "nuxeoLink");
            entry.setAttributeValue("key", link);
            entry.setTextContent(value);
            
            writeProcModel(procDef, model);
        }
    }

    @Override
    public void deleteLink(String processDefinitionId,
        String docType, String path)
    {
        ProcessDefinition procDef = fProcessEngine.getRepositoryService()
            .getProcessDefinition(processDefinitionId);
        
        BpmnModelInstance model = fProcessEngine.getRepositoryService()
            .getBpmnModelInstance(processDefinitionId);
        
        
        //delete link
        if(docType != null && !docType.isEmpty())
        {
            unlink(procDef, model, "type", docType);
        }
        if(path != null && !path.isEmpty())
        {
            unlink(procDef, model, "path", path);
        }
    }
    
    
    private void unlink(ProcessDefinition procDef, BpmnModelInstance model,
        String link, String value)
    {
        boolean unlinked = false;
        ModelElementInstance linkElem = null;
        
        RootElement root = model.getDefinitions().getRootElements().iterator().next();
        
        ExtensionElements extensionElements = root.getExtensionElements();
        
        if (extensionElements == null)
        {
            extensionElements = model.newInstance(ExtensionElements.class);
            root.setExtensionElements(extensionElements);
        }
        
        //check if there is a matching link
        for(ModelElementInstance ei : extensionElements.getElements())
        {
            String key = ei.getAttributeValue("key");
            
            if(link.equals(key)
                && value.equals(ei.getRawTextContent()))
            {
                linkElem = ei;
                break;
            }
        }
        
        if(linkElem != null)
        {
            extensionElements.removeChildElement(linkElem);
            unlinked = true;
        }
        
        if(unlinked)
        {
            writeProcModel(procDef, model);
        }
    }
    
    
    private void writeProcModel(ProcessDefinition procDef,
        BpmnModelInstance model)
    {
        DeploymentResourceImpl dri = new DeploymentResourceImpl(PROC_ENGINE_NAME,
            procDef.getDeploymentId(), relativeRootResourcePath, fObjectMapper);
        String name = dri.getDeployment().getName();
        
        DeploymentBuilder builder =
            fProcessEngine.getRepositoryService().createDeployment();
        builder.name(name);
        builder.addModelInstance(procDef.getResourceName(), model);
        builder.enableDuplicateFiltering(true);
        builder.deploy();
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
