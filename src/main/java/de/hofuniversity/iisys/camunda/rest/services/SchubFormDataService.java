package de.hofuniversity.iisys.camunda.rest.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.hofuniversity.iisys.camunda.rest.dto.FormDataEntryDTO;

@Path(SchubFormDataService.PATH)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface SchubFormDataService
{
    public static final String PATH = "/form-data";

    @GET
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public List<FormDataEntryDTO> getFormData(
        @Context UriInfo uriInfo,
        @QueryParam("definitionId") String defId,
        @QueryParam("taskId") String taskId);
}
