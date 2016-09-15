package de.hofuniversity.iisys.camunda.rest.services;

import java.util.List;

import de.hofuniversity.iisys.camunda.rest.dto.HistoryEntryDTO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path(SchubProcessHistoryService.PATH)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface SchubProcessHistoryService
{
    public static final String PATH = "/process-history";
    
    @GET
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public List<HistoryEntryDTO> getProcessHistory(
        @Context UriInfo uriInfo,
        @QueryParam("firstResult") Integer firstResult,
        @QueryParam("maxResults") Integer maxResults);
}
