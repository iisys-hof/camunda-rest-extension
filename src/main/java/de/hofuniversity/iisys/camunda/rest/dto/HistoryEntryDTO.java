package de.hofuniversity.iisys.camunda.rest.dto;

import java.util.Date;

import org.camunda.bpm.engine.history.HistoricProcessInstance;

public class HistoryEntryDTO
{
    //default fields
    private String id;
    private String businessKey;
    private String processDefinitionId;
    private Date startTime;
    private Date endTime;
    private Long durationInMillis;
    private String startUserId;
    private String startActivityId;
    private String deleteReason;
    private String superProcessInstanceId;
    private String caseInstanceId;
    
    //extra fields
    private String startUserName;
    private String processDefinitionName;
    
    //default fields
    public String getId()
    {
        return id;
    }

    public String getBusinessKey()
    {
        return businessKey;
    }

    public String getProcessDefinitionId()
    {
        return processDefinitionId;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public Long getDurationInMillis()
    {
        return durationInMillis;
    }

    public String getStartUserId()
    {
        return startUserId;
    }

    public String getStartActivityId()
    {
        return startActivityId;
    }

    public String getDeleteReason()
    {
        return deleteReason;
    }

    public String getSuperProcessInstanceId()
    {
        return superProcessInstanceId;
    }

    public String getCaseInstanceId()
    {
        return caseInstanceId;
    }
    
    //extra fields
    public String getStartUserName()
    {
        return startUserName;
    }
    
    public void setStartUserName(String startUserName)
    {
        this.startUserName = startUserName;
    }
    
    public String getProcessDefinitionName()
    {
        return processDefinitionName;
    }
    
    public void setProcessDefinitionName(String procDefName)
    {
        this.processDefinitionName = procDefName;
    }
    
    public static HistoryEntryDTO read(
        HistoricProcessInstance historicProcessInstance)
    {
        HistoryEntryDTO dto = new HistoryEntryDTO();
        
        dto.id = historicProcessInstance.getId();
        dto.businessKey = historicProcessInstance.getBusinessKey();
        dto.processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        dto.startTime = historicProcessInstance.getStartTime();
        dto.endTime = historicProcessInstance.getEndTime();
        dto.durationInMillis = historicProcessInstance.getDurationInMillis();
        dto.startUserId = historicProcessInstance.getStartUserId();
        dto.startActivityId = historicProcessInstance.getStartActivityId();
        dto.deleteReason = historicProcessInstance.getDeleteReason();
        dto.superProcessInstanceId = historicProcessInstance.getSuperProcessInstanceId();
        dto.caseInstanceId = historicProcessInstance.getCaseInstanceId();
        
        return dto;
    }
}
