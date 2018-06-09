/* (c) 2014-2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.response;

import java.util.List;
import org.geoserver.config.GeoServer;
import org.geoserver.ows.OWS11ServiceExceptionHandler;
import org.geoserver.ows.Request;
import org.geoserver.platform.ServiceException;
import org.geoserver.wfs.json.JSONType;

/**
 * Handles a WFS 2.0 service exception by producing an exception report.
 *
 * @author Brad Hards, Sigma Bravo
 *     <p>Based on WfsExceptionHandler by Justin Deoliveira - The Open Planning Project and Carlo
 *     Cancellieri - GeoSolutions.
 */
public class Wfs2ExceptionHandler extends OWS11ServiceExceptionHandler {

    GeoServer gs;

    /**
     * Constructor.
     *
     * @param services services on offer.
     * @param gs server context
     */
    public Wfs2ExceptionHandler(List services, GeoServer gs) {
        super(services);
        this.gs = gs;
    }

    /** Encodes a ogc:ServiceExceptionReport to output. */
    @Override
    public void handleServiceException(ServiceException exception, Request request) {

        boolean verbose = gs.getSettings().isVerboseExceptions();
        String charset = gs.getSettings().getCharset();
        // first of all check what kind of exception handling we must perform
        final String exceptions;
        try {
            exceptions = (String) request.getKvp().get("EXCEPTIONS");
            if (exceptions == null) {
                // use default
                super.handleServiceException(exception, request);
                return;
            }
        } catch (Exception e) {
            super.handleServiceException(exception, request);
            return;
        }
        if (JSONType.isJsonMimeType(exceptions)) {
            // use Json format
            JSONType.handleJsonException(LOGGER, exception, request, charset, verbose, false);
        } else if (JSONType.useJsonp(exceptions)) {
            // use JsonP format
            JSONType.handleJsonException(LOGGER, exception, request, charset, verbose, true);
        } else {
            super.handleServiceException(exception, request);
        }
    }
}
