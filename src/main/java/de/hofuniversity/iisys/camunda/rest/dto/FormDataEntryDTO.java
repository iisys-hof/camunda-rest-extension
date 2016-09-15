package de.hofuniversity.iisys.camunda.rest.dto;

import org.camunda.bpm.engine.form.FormField;

public class FormDataEntryDTO
{
    private String id;
    private String label;
    private String type;
    private Object value;
    
    public FormDataEntryDTO() {}
    
    public FormDataEntryDTO(FormField field)
    {
        id = field.getId();
        label = field.getLabel();
        type = field.getTypeName();
        value = field.getValue().getValue();
    }
    
    public String getId()
    {
        return id;
    }
    
    public String getLabel()
    {
        return label;
    }
    
    public String getType()
    {
        return type;
    }
    
    public Object getValue()
    {
        return value;
    }
}
