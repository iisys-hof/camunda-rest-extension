# camunda-rest-extension
Camunda BPM artifacts: https://app.camunda.com/nexus/content/groups/public/org/camunda/bpm/

Camunda Commons artifacts: https://app.camunda.com/nexus/content/groups/public/org/camunda/commons/

Building:

1. Import into Eclipse with Maven support
2. Add Libraries in a /lib/ directory to build path (camunda-engine , camunda-bpmn-model, camunda-xml-model, camunda-commons-typed-values)
3. Add class files in a /lib/ directory to build path (camunda-engine-rest - classes jar does not contain all required classes since they're only in the "assembly" directory now?)
4. Export library jar

Installation:

1. Go to $CAMUNDA_DIR/server/apache-tomcat-$VERSION/webapps/engine-rest/WEB-INF/
2. Backup web.xml
3. Put generated jar file in lib/
4. Edit web.xml, replacing "org.camunda.bpm.engine.rest.impl.application.DefaultApplication" by "de.hofuniversity.iisys.camunda.rest.ExtendedApplication" in the "Resteasy" filter

Usage:

    Process History:

      Supports the same parameters as the original historic process instances endpoint (URL).

      GET $CAMUNDA_URL/engine-rest/process-history/

    Start/Task Form data:

      Delivers field names, types, labels and values

      GET $CAMUNDA_URL/engine-rest/form-data?definitionId=$PROCESS_DEFINITION_ID

      GET $CAMUNDA_URL/engine-rest/form-data?taskId=$TASK_ID
   
    Get Linked Worfklows:

      For a document type and document path

      GET $CAMUNDA_URL/engine-rest/nuxeo-links?type=$TYPE&path=$PATH

    Create new Workflow link:

      Warning: deploys a new version of the workflow - possibly causing conflicts with deployed war files

      Link to subpath:

      POST $CAMUNDA_URL/engine-rest/nuxeo-links/link/$PROC_DEF_ID?path=$PATH

      Link to document type:

      POST $CAMUNDA_URL/engine-rest/nuxeo-links/link/$PROC_DEF_ID?type=$TYPE

    Delete existing Workflow link:

      Warning: deploys a new version of the workflow - possibly causing conflicts with deployed war files

      Subpath link:

      DELETE $CAMUNDA_URL/engine-rest/nuxeo-links/unlink/$PROC_DEF_ID?path=$PATH

      Document type link:

      DELETE $CAMUNDA_URL/engine-rest/nuxeo-links/unlink/$PROC_DEF_ID?type=$TYPE
      