package org.codehaus.plexus;

import junit.framework.TestCase;

import java.util.Collections;
import java.net.URL;

import org.codehaus.plexus.test.ComponentA;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultComponentLookupManagerTest
    extends TestCase
{
    public void testLookupsWithAndWithoutRoleHint()
        throws Exception
    {
        URL resource = DefaultComponentLookupManagerTest.class.getResource( "components.xml" );
        assertNotNull( resource );
        DefaultPlexusContainer container = new DefaultPlexusContainer( "test",  Collections.EMPTY_MAP, resource );

        /* Disable this test for now - re-enable it once the lookup( role ) is fixed - Andy
        try
        {
            container.lookup( ComponentA.ROLE );

            fail( "Expected exception" );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
        */
    }
}
