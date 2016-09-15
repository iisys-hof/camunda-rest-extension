package de.hofuniversity.iisys.camunda.rest;

import java.util.HashSet;
import java.util.Set;

import org.camunda.bpm.engine.rest.impl.CamundaRestResources;
import org.camunda.bpm.engine.rest.impl.application.DefaultApplication;

import de.hofuniversity.iisys.camunda.rest.services.SchubExtProcessDefinitionServiceImpl;
import de.hofuniversity.iisys.camunda.rest.services.SchubFormDataServiceImpl;
import de.hofuniversity.iisys.camunda.rest.services.SchubNuxeoLinkServiceImpl;
import de.hofuniversity.iisys.camunda.rest.services.SchubProcessHistoryServiceImpl;

public class ExtendedApplication extends DefaultApplication
{
    @Override
    public Set<Class<?>> getClasses()
    {
      Set<Class<?>> classes = new HashSet<Class<?>>();

      classes.addAll(CamundaRestResources.getResourceClasses());

      classes.add(SchubProcessHistoryServiceImpl.class);
      classes.add(SchubFormDataServiceImpl.class);
      
      classes.add(SchubExtProcessDefinitionServiceImpl.class);
      classes.add(SchubNuxeoLinkServiceImpl.class);
      
      classes.addAll(CamundaRestResources.getConfigurationClasses());

      return classes;
    }
}
