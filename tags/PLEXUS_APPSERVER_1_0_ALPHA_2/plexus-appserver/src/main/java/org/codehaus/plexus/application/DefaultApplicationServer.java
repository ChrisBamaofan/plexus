package org.codehaus.plexus.application;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;

import org.codehaus.plexus.application.deploy.ApplicationDeployer;
import org.codehaus.plexus.application.service.ServiceDiscoverer;
import org.codehaus.plexus.application.supervisor.Supervisor;
import org.codehaus.plexus.application.supervisor.SupervisorListener;
import org.codehaus.plexus.application.supervisor.SupervisorException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;

/**
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultApplicationServer
    extends AbstractLogEnabled
    implements ApplicationServer, Startable
{
    private ApplicationDeployer applicationDeployer;

    private ServiceDiscoverer serviceDiscoverer;

    private Supervisor supervisor;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
        throws StartingException
    {
        // ----------------------------------------------------------------------
        // Register the deployers inside the directory supervisor so applications
        // and services will be deployed.
        // ----------------------------------------------------------------------

        File home = new File( System.getProperty( "plexus.home" ) );

        try
        {
            supervisor.addDirectory( new File( home, "services" ), new SupervisorListener()
            {
                public void onJarDiscovered( File jar )
                {
                    String name = jar.getName();

                    try
                    {
                        serviceDiscoverer.deploy( name.substring( 0, name.length() - 4 ), jar.getAbsolutePath() );
                    }
                    catch ( Exception e )
                    {
                        // TDOO: remove the user of  stderr
                        // TODO; use a more specific exception
                        System.err.println( "Error while deploying service '" + name + "'." );

                        e.printStackTrace();
                    }
                }
            } );
        }
        catch ( SupervisorException e )
        {
            throw new StartingException( "Error deploying services in the app server.", e );
        }

        try
        {
            supervisor.addDirectory( new File( home, "apps" ), new SupervisorListener()
            {
                public void onJarDiscovered( File jar )
                {
                    String name = jar.getName();

                    try
                    {
                        applicationDeployer.deploy( name.substring( 0, name.length() - 4 ), jar.toURL().toExternalForm() );
                    }
                    catch ( Exception e )
                    {
                        // TDOO: remove the user of  stderr
                        // TODO; use a more specific exception
                        System.err.println( "Error while deploying application '" + name + "'." );

                        e.printStackTrace();
                    }
                }
            } );

        }
        catch ( SupervisorException e )
        {
            throw new StartingException( "Error deploying applications in the app server.", e );
        }

        getLogger().info( "The application server has been initialized." );

        // ----------------------------------------------------------------------
        // Do the initial scan to deploy all services and applications
        // ----------------------------------------------------------------------

        // TODO: Start a thread that will use the supervisor to continuously scan

        try
        {
            supervisor.scan();
        }
        catch ( SupervisorException e )
        {
            // TODO; use a more specific exception
            throw new StartingException( "Error while scanning for new services and applications.", e );
        }
    }

    public void stop()
    {
    }
}
