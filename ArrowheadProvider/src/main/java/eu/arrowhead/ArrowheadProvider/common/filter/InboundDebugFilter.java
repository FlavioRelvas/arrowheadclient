package eu.arrowhead.ArrowheadProvider.common.filter;

import eu.arrowhead.ArrowheadProvider.ProviderMain;
import eu.arrowhead.ArrowheadProvider.common.Utility;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER)
public class InboundDebugFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    if (ProviderMain.DEBUG_MODE) {
      System.out.println("New " + requestContext.getMethod() + " request at: " + requestContext.getUriInfo().getRequestUri().toString());
      BufferedReader br = new BufferedReader(new InputStreamReader(requestContext.getEntityStream(), "utf-8"));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append("\n");
      }
      br.close();
      String prettyJson = Utility.toPrettyJson(sb.toString(), null);
      System.out.println(prettyJson);
      InputStream in = new ByteArrayInputStream(prettyJson.getBytes("UTF-8"));
      requestContext.setEntityStream(in);
    }
  }
}
