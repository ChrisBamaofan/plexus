package org.codehaus.plexus.service.irc;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * IRCUser Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>07/23/2006</pre>
 */
public class IRCUserTest
    extends TestCase
{

    IRCUser user;

    public IRCUserTest( String name )
    {
        super( name );

        user = new DefaultIRCUser( "nick", "login", "host" );
    }

    public void testGetNick()
        throws Exception
    {
        assertEquals( user.getNick(), "nick" );
    }

    public void testGetLogin()
        throws Exception
    {
        assertEquals( user.getLogin(), "login" );
    }

    public void testGetHost()
        throws Exception
    {
        assertEquals( user.getHost(), "host" );
    }

    public static Test suite()
    {
        return new TestSuite( IRCUserTest.class );
    }
}
