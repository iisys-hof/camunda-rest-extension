package de.hofuniversity.iisys.camunda.rest.services;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.hofuniversity.iisys.camunda.rest.dto.ExtProcessDefDTO;

@Path(SchubNuxeoLinkService.PATH)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface SchubNuxeoLinkService
{
    public static final String PATH = "/nuxeo-links";

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public List<ExtProcessDefDTO> getLinked(
        @Context UriInfo uriInfo,
        @QueryParam("type") List<String> docType,
        @QueryParam("path") String path,
        @QueryParam("firstResult") Integer firstResult,
        @QueryParam("maxResults") Integer maxResults);
    
    @POST
    @Path("/link/{id}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public void createLink(
        @PathParam("id") String processDefinitionId,
        @QueryParam("type") String docType,
        @QueryParam("path") String path);

    @DELETE
    @Path("/unlink/{id}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public void deleteLink(
        @PathParam("id") String processDefinitionId,
        @QueryParam("type") String docType,
        @QueryParam("path") String path);
}
