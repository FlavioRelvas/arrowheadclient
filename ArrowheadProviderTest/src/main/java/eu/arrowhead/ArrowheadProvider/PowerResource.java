/*
 * Copyright (c) 2018 AITIA International Inc.
 *
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */
package eu.arrowhead.ArrowheadProvider;

import eu.arrowhead.ArrowheadProvider.common.Utility;
import eu.arrowhead.ArrowheadProvider.common.model.MeasurementEntry;
import eu.arrowhead.ArrowheadProvider.common.model.TemperatureReadout;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Produces(MediaType.APPLICATION_JSON)
@Path("power")
public class PowerResource {

    @GET
    public Response getIt(@Context SecurityContext context, @QueryParam("token") String token, @QueryParam("signature") String signature) {
        MeasurementEntry entry = new MeasurementEntry("Power_PowerConsumption", 500.0, System.currentTimeMillis());
        if (context.isSecure()) {
            TemperatureReadout readout = new TemperatureReadout("PowerPlug_SecurePowerPlug", System.currentTimeMillis(), "watts", 1);
            readout.getE().add(entry);
            return Utility.verifyRequester(context, token, signature, readout);
        }
        TemperatureReadout readout = new TemperatureReadout("PowerPlug_InsecurePowerPlug", System.currentTimeMillis(), "watts", 1);
        readout.getE().add(entry);
        return Response.status(200).entity(readout).build();
    }

}
