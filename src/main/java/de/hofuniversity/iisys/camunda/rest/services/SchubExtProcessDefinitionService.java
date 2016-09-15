package de.hofuniversity.iisys.camunda.rest.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.hofuniversity.iisys.camunda.rest.dto.ExtProcessDefDTO;

@Path(SchubExtProcessDefinitionService.PATH)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface SchubExtProcessDefinitionService
{
    public static final String PATH = "/ext-process-definition";

    @GET
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public List<ExtProcessDefDTO> getProcessDefinitions(
        @Context UriInfo uriInfo,
        @QueryParam("firstResult") Integer firstResult,
        @QueryParam("maxResults") Integer maxResults);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public ExtProcessDefDTO getProcessDefinitionById(
        @PathParam("id") String processDefinitionId);

    @GET
    @Path("/{id}/set/{key}/{value}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public ExtProcessDefDTO setMetaValue(
        @PathParam("id") String processDefinitionId,
        @PathParam("key") String key,
        @PathParam("value") String value);
}
