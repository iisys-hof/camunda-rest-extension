package de.hofuniversity.iisys.camunda.rest.util;

public class WorkflowLink
{
    private String fType;
    private String fValue;
    
    public WorkflowLink()
    {
        this(null, null);
    }
    
    public WorkflowLink(String type, String value)
    {
        fType = type;
        fValue = value;
    }
    
    public String getType()
    {
        return fType;
    }
    
    public void setType(String type)
    {
        this.fType = type;
    }
    
    public String getValue()
    {
        return fValue;
    }
    
    public void setValue(String value)
    {
        this.fValue = value;
    }
}
