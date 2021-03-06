package org.codehaus.plexus.component.discovery;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractComponentDiscoverer
    implements ComponentDiscoverer
{
    private ComponentDiscovererManager manager;

    // ----------------------------------------------------------------------
    //  Abstract methods
    // ----------------------------------------------------------------------

    protected abstract String getComponentDescriptorLocation();

    protected abstract ComponentSetDescriptor createComponentDescriptors( Reader reader, String source )
        throws Exception;

    // ----------------------------------------------------------------------
    //  ComponentDiscoverer
    // ----------------------------------------------------------------------

    public void setManager( ComponentDiscovererManager manager )
    {
        this.manager = manager;
    }

    public List findComponents( Context context, ClassRealm classRealm )
    {
        List componentSetDescriptors = new ArrayList();

        try
        {
            for ( Enumeration e = classRealm.findResources( getComponentDescriptorLocation() ); e.hasMoreElements(); )
            {
                URL url = (URL) e.nextElement();

                InterpolationFilterReader input =
                    new InterpolationFilterReader( new InputStreamReader( url.openStream() ), new ContextMapAdapter( context ) );

                String descriptor = IOUtil.toString( input );

                ComponentSetDescriptor componentSetDescriptor = createComponentDescriptors( new StringReader( descriptor ), url.toString() );

                componentSetDescriptors.add( componentSetDescriptor );

                // Fire the event
                ComponentDiscoveryEvent event = new ComponentDiscoveryEvent( componentSetDescriptor );

                manager.fireComponentDiscoveryEvent( event );
            }
        }
        catch ( Exception e )
        {
            //classRealm.display();

            e.printStackTrace();
        }

        return componentSetDescriptors;
    }
}
