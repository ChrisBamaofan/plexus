package org.codehaus.plexus.components.password;

/*
 * The MIT License
 *
 * Copyright (c) 2005, The Codehaus
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

import junit.framework.TestCase;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * Test the JKS Password Store.
 *
 * @author Brett Porter
 * @version $Id$
 */
public class JksPasswordStoreTest
    extends TestCase
{
	private static final String masterPassword = "foofoo";// since java5 should be at least 6 characters

    private static final String KEYSTORE_FILE_NAME = "target/test.jceks";
    private File keystoreFile;
    private JksPasswordStore passwordStore;
    private static final String KNOWN_ID = "id";
    private static final String KNOWN_PASSWORD = "newPassword";

    private String basedir;

    public void setUp()
        throws Exception
    {
        super.setUp();

        // TODO: use a resource URL instead
        basedir = System.getProperty( "basedir" );
        if( basedir == null )
        	basedir = ".";
        keystoreFile = new File( basedir, KEYSTORE_FILE_NAME );

        keystoreFile.delete();
        passwordStore = new JksPasswordStore( new File(KEYSTORE_FILE_NAME), masterPassword );
        passwordStore.create( KEYSTORE_FILE_NAME, masterPassword, false );
        passwordStore.setPassword( KNOWN_ID, KNOWN_PASSWORD );
    }
    
    protected void tearDown()
    throws Exception
	{
		super.tearDown();
        new File(KEYSTORE_FILE_NAME).delete();
	}
    //-----------------------------------------------------------------------------------
    /**
     * test empty store creation
     * 
     */
	public void testCreation()
        throws Exception
    {
		String tempName = basedir+"/" + KEYSTORE_FILE_NAME+".temp"; 
        new File(tempName).delete();
        
    	PasswordStore ps = new JksPasswordStore();
    	assertNotNull("Cannot create temporary keystore instance", ps );

    	ps.create( tempName, masterPassword, true );
    	assertTrue("Cannot create temporary keystore file", new File(tempName).exists() );

    	File storeFile = new File(tempName);
    	assertTrue("Newly created store file does not exist - "+tempName, storeFile.exists() );
    	
        storeFile.delete();
    }

    public void testGetPassword()
        throws Exception
    {
        assertEquals( "Password was not found", KNOWN_PASSWORD, passwordStore.getPassword( KNOWN_ID ) );
    }

    public void testGetPasswordDoesNotExist()
        throws Exception
    {
        assertNull( "Password should not be found", passwordStore.getPassword( "foo" ) );
    }

    public void testCheckPassword()
        throws Exception
    {
        assertTrue( "Password check failed on good password", passwordStore.checkPassword( KNOWN_ID, KNOWN_PASSWORD ) );
        assertFalse( "Password check succeeded on bad password", passwordStore.checkPassword( KNOWN_ID, "foo" ) );
        assertFalse( "Password check succeeded on bad password ID", passwordStore.checkPassword( "foo", "bar" ) );
    }

    public void testSetPassword()
        throws Exception
    {
        String password = "setPassword";
        String id = "newId";
        passwordStore.setPassword( id, password );
        assertEquals( "Password was not set correctly", password, passwordStore.getPassword( id ) );
    }

    public void testChangePassword()
        throws Exception
    {
        assertEquals( "Password was not set correctly", KNOWN_PASSWORD, passwordStore.getPassword( KNOWN_ID ) );
        passwordStore.setPassword( KNOWN_ID, KNOWN_PASSWORD, "foo" );
        assertEquals( "Password was not set correctly", "foo", passwordStore.getPassword( KNOWN_ID ) );
    }

    public void testRemovePassword()
    throws Exception
	{
	    assertEquals( "Password was not set correctly", KNOWN_PASSWORD, passwordStore.getPassword( KNOWN_ID ) );
	    passwordStore.removePassword( KNOWN_ID );
	    assertNull( "Password was not removed", passwordStore.getPassword( KNOWN_ID ) );
	}

}
