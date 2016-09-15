package de.hofuniversity.iisys.camunda.rest.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.rest.dto.VariableQueryParameterDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricProcessInstanceQueryDto;
import org.camunda.bpm.engine.rest.exception.RestException;
import org.camunda.bpm.engine.rest.spi.ProcessEngineProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.hofuniversity.iisys.camunda.rest.dto.HistoryEntryDTO;

public class SchubProcessHistoryServiceImpl implements SchubProcessHistoryService
{
    protected ObjectMapper fObjectMapper;
    protected ProcessEngine fProcessEngine;

    public SchubProcessHistoryServiceImpl()
    {
        fObjectMapper = new ObjectMapper();
        
        ProcessEngineProvider prov = getProcessEngineProvider();
        fProcessEngine = prov.getDefaultProcessEngine();
    }
    
    @Override
    public List<HistoryEntryDTO> getProcessHistory(UriInfo uriInfo,
        Integer firstResult, Integer maxResults)
    {
        List<HistoryEntryDTO> list =
            new ArrayList<HistoryEntryDTO>();
        
        //remove variables due to probable parsing errors from underscores
        List<String> variables = uriInfo.getQueryParameters().remove("variables");
        
        HistoricProcessInstanceQueryDto queryDto
            = new HistoricProcessInstanceQueryDto(fObjectMapper,
                uriInfo.getQueryParameters());

        //manually parse variable filters
        if(variables != null && !variables.isEmpty())
        {
            List<VariableQueryParameterDto> pVariables = new ArrayList<>();
            
            for(String varString : variables)
            {
                String[] split = varString.split("_");
                String value = split[split.length - 1];
                String op = split[split.length - 2];
                
                //fuse rest to form key with underscores in it
                String key = "";
                for(int i = 0; i < split.length - 2; ++i)
                {
                    key += "_" + split[i];
                }
                key = key.substring(1);

                VariableQueryParameterDto v = new VariableQueryParameterDto();
                v.setName(key);
                v.setOperator(op);
                v.setValue(value);
            }

            queryDto.setVariables(pVariables);
        }
        
        queryDto.setObjectMapper(fObjectMapper);
        HistoricProcessInstanceQuery query = queryDto.toQuery(fProcessEngine);
        
        
        if (firstResult == null)
        {
            firstResult = 0;
        }
        if(maxResults == null)
        {
            maxResults = Integer.MAX_VALUE;
        }
        
        
        
        List<HistoricProcessInstance> matching;
        matching = query.listPage(firstResult, maxResults);

        for (HistoricProcessInstance historicProcessInstance : matching)
        {
          HistoryEntryDTO dto = HistoryEntryDTO
              .read(historicProcessInstance);
          
          //get user name
          String userId = dto.getStartUserId();
          if(userId != null && !userId.isEmpty())
          {
              User user = fProcessEngine.getIdentityService()
                  .createUserQuery().userId(userId).singleResult();
              
              if(user != null)
              {
                  String userName = user.getFirstName() + " " + user.getLastName();
                  if(userName.isEmpty())
                  {
                      userName = userId;
                  }
                  dto.setStartUserName(userName);
              }
          }
          
          //get process definition name
          try
          {
              String defId = dto.getProcessDefinitionId();
              ProcessDefinition def = fProcessEngine.getRepositoryService()
                  .getProcessDefinition(defId);
              dto.setProcessDefinitionName(def.getName());
          }
          catch(Exception e)
          {
              e.printStackTrace();
          }
          
          list.add(dto);
        }
        
        
        
        
        return list;
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
