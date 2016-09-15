package de.hofuniversity.iisys.camunda.rest.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.StartFormData;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.rest.exception.RestException;
import org.camunda.bpm.engine.rest.spi.ProcessEngineProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.hofuniversity.iisys.camunda.rest.dto.FormDataEntryDTO;

public class SchubFormDataServiceImpl implements SchubFormDataService
{
    protected ObjectMapper fObjectMapper;
    protected ProcessEngine fProcessEngine;

    public SchubFormDataServiceImpl()
    {
        fObjectMapper = new ObjectMapper();
        
        ProcessEngineProvider prov = getProcessEngineProvider();
        fProcessEngine = prov.getDefaultProcessEngine();
    }

    @Override
    public List<FormDataEntryDTO> getFormData(UriInfo uriInfo, String defId,
        String taskId)
    {
        //TODO: support for locked fields?
        
        List<FormDataEntryDTO> list = new ArrayList<FormDataEntryDTO>();
        
        if(taskId != null && !taskId.isEmpty())
        {
            TaskFormData data = fProcessEngine.getFormService().getTaskFormData(taskId);

            for(FormField field : data.getFormFields())
            {
                FormDataEntryDTO dto = new FormDataEntryDTO(field);
                list.add(dto);
            }
        }
        
        if(defId != null && !defId.isEmpty())
        {
            StartFormData data = fProcessEngine.getFormService().getStartFormData(defId);
            
            for(FormField field : data.getFormFields())
            {
                FormDataEntryDTO dto = new FormDataEntryDTO(field);
                list.add(dto);
            }
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
