package org.codehaus.plexus.xsiter.deployer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.velocity.app.Velocity;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;
import org.codehaus.plexus.xsiter.deployer.model.DeployedProject;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;
import org.codehaus.plexus.xsiter.vhost.VirtualHostConfiguration;
import org.codehaus.plexus.xsiter.vhost.VirtualHostManager;

/**
 * Concrete implementation of a {@link Deployer} Role.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id: DefaultDeployer.java,v 1.1 2006/09/14 05:11:37 rahul
 *          Exp $
 * @plexus.component
 */
public class DefaultDeployer
    extends AbstractDeployer

{

    /**
     * Property that specifies the default deployment goals 
     * to be used by the Deployer. 
     */
    private static final String PROP_DEPLOYER_DEFAULT_GOALS = "deployer.default.goals";

    /**
     * Date Formatter.
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat( "dd-MM-yyyy, HH:mm:ss" );

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#addProject(DeployableProject)
     */
    public void addProject( DeployableProject project )
        throws Exception
    {
        if ( new File( workingDirectory, project.getLabel() ).exists() )
            throw new Exception( "Workspace with ID: '" + project.getLabel() + "' already exists." );
        createDeploymentWorkspace( project );
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#removeProject(DeployableProject)
     */
    public void removeProject( DeployableProject project )
        throws Exception
    {
        File targetDir = new File( workingDirectory, project.getLabel() );
        if ( targetDir.exists() )
        {
            FileUtils.deleteDirectory( targetDir );
        }
        else
        {
            getLogger().info( "No Deployment workspace found for Project '" + project.getLabel() + "'" );
        }
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#getDeploymentWorkspace(DeployableProject)
     */
    public DeploymentWorkspace getDeploymentWorkspace( DeployableProject project )
        throws Exception
    {
        return loadWorkspaceFromDescriptor( project.getLabel() );
    }

    /**
     * @return the workingDirectory
     */
    public String getWorkingDirectory()
    {
        return workingDirectory;
    }

    /**
     * @param workingDirectory the workingDirectory to set
     */
    public void setWorkingDirectory( String workingDirectory )
    {
        this.workingDirectory = workingDirectory;
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#addVirtualHost(DeployableProject)
     */
    public void addVirtualHost( DeployableProject project )
        throws Exception
    {
        if ( !isValidWorkspace( project.getLabel() ) )
        {
            throw new Exception( "Invalid workspace ID: '" + project.getLabel() + "'" );
        }

        createDeploymentWorkspaceIfRequired( project );
        // Updated Implementation, use configuration from pom.xml
        MavenProject mavenProject = getMavenProjectForCheckedoutProject( project );
        Properties props = mavenProject.getProperties();

        String vhostsConfig = (String) props.get( VirtualHostManager.ELT_POM_VHOSTS_CONFIG );
        getLogger().info( "Creating Virtual Host Configuration from XML snippet:\n" + vhostsConfig );
        StringReader sr = new StringReader( vhostsConfig );
        List list = virtualHostManager.loadVirtualHostConfigurations( sr );
        boolean appendVhost = false;
        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            VirtualHostConfiguration config = (VirtualHostConfiguration) it.next();
            DeploymentWorkspace workspace = loadWorkspaceFromDescriptor( project.getLabel() );
            File workspaceWorkingDir = new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() );
            String scmTag = null != project.getScmTag() ? project.getScmTag() : "HEAD";
            File checkoutDir = new File( workspaceWorkingDir, scmTag );
            getLogger().info( "Adding Virtual Host for config ID: " + config.getId() );
            // Adjust the Velocity 'file.resource.loader.path' property to the
            // checkout directory, so the Vhosts template could be found.
            Velocity.setProperty( Velocity.FILE_RESOURCE_LOADER_PATH, checkoutDir.getAbsolutePath() );
            // Adjust other directory paths on the VirtualHostConfiguration
            // instance
            resetVirtualHostDirectory( config, workspace );
            virtualHostManager.addVirtualHost( config, appendVhost );
            // subsequent vhost configs to be appended
            if ( !appendVhost )
                appendVhost = true;
        }
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#checkoutProject(DeployableProject)
     */
    public void checkoutProject( DeployableProject project )
        throws Exception
    {
        if ( !isValidWorkspace( project.getLabel() ) )
        {
            throw new Exception( "Invalid workspace ID: '" + project.getLabel() + "'" );
        }
        DeploymentWorkspace workspace = createDeploymentWorkspaceIfRequired( project );
        getLogger().info( "Checking out Project: " + project.getLabel() + ", Version: " + project.getScmTag() );

        // TODO: refactor/add a method that creates a Deployable Project
        // instance from a Workspace descriptor

        // sanity for SCM info, source from workspace
        project.setScmURL( workspace.getScmURL() );
        project.setScmUsername( workspace.getScmUsername() );
        project.setScmPassword( workspace.getScmPassword() );

        try
        {
            ScmRepository repository = getScmRepository( project );
            ScmResult result;
            synchronized ( this )
            {
                File checkoutDir = new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() );
                try
                {
                    if ( checkoutDir.exists() )
                        FileUtils.cleanDirectory( checkoutDir );
                }
                catch ( IOException e )
                {
                    throw new Exception( "Could not clean directory : " + workspace.getWorkingDirectory(), e );
                }

                // check out to a tagged directory
                String scmTag = null != project.getScmTag() ? project.getScmTag() : "HEAD";
                ScmFileSet fileSet = new ScmFileSet( new File( checkoutDir, scmTag ) );
                result = scmManager.getProviderByRepository( repository ).checkOut( repository, fileSet,
                                                                                    project.getScmTag() );
                getLogger().info( result.getCommandOutput() );
                getLogger().info( result.getProviderMessage() );
            }

            if ( !result.isSuccess() )
            {
                getLogger().warn( "Command output: " + result.getCommandOutput() );
                getLogger().warn( "Provider message: " + result.getProviderMessage() );
            }
            else
            {
                getLogger().info( "Project checked out successfully." );
            }

            // return result;
        }
        catch ( Exception e )
        {
            throw new Exception( "Cannot checkout sources.", e );
        }
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#deployProject(DeployableProject)
     */
    public DeployedProject deployProject( DeployableProject project )
        throws Exception
    {
        if ( !isValidWorkspace( project.getLabel() ) )
        {
            throw new Exception( "Invalid workspace ID: '" + project.getLabel() + "'" );
        }
        MavenProject mavenProject = getMavenProjectForCheckedoutProject( project );
        String defaultDeploymentGoals = (String) mavenProject.getProperties().get( PROP_DEPLOYER_DEFAULT_GOALS );
        getLogger().info( "Using default deployment goals: " + defaultDeploymentGoals );
        // Deploy the checked out Project to Appserver/Webserver
        return deployProject( project, defaultDeploymentGoals );
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#deployProject(DeployableProject,
     *      java.lang.String)
     */
    public DeployedProject deployProject( DeployableProject project, String goals )
        throws Exception
    {
        if ( !isValidWorkspace( project.getLabel() ) )
        {
            throw new Exception( "Invalid workspace ID: '" + project.getLabel() + "'" );
        }
        createDeploymentWorkspaceIfRequired( project );
        // Deploy the checked out Project to Appserver/Webserver
        if ( isCargoPluginConfigurationInPOM( project ) )
        {
            buildProject( project, goals );
        }
        else
        {
            getLogger()
                .error( "Unable to deploy project to an Application server. (Missing Cargo Maven Plugin configuration)" );
            throw new Exception(
                                 "Unable to deploy project to an Application server. (Missing Cargo Maven Plugin configuration)" );
        }
        // TODO Add more stuff to deployed project
        return new DeployedProject();
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#buildProject(DeployableProject,
     *      java.lang.String)
     */
    public void buildProject( DeployableProject project, String goals )
        throws Exception
    {
        if ( !isValidWorkspace( project.getLabel() ) )
        {
            throw new Exception( "Invalid workspace ID: '" + project.getLabel() + "'" );
        }
        DeploymentWorkspace workspace = createDeploymentWorkspaceIfRequired( project );
        checkoutProjectIfRequired( project );
        File workspaceWorkingDir = new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() );
        String scmTag = null != project.getScmTag() ? project.getScmTag() : "HEAD";
        File checkoutDir = new File( workspaceWorkingDir, scmTag );

        if ( !checkoutDir.exists() )
            throw new Exception( "No check out directory exists, nothing to build" );

        // XXX: It would be nice to have an attempt to check that 'mvn' command
        // was on $PATH

        Commandline cmd = new Commandline();
        cmd.setExecutable( MAVEN_EXECUTABLE );
        List argsList = string2List( goals, ' ' );
        for ( Iterator it = argsList.iterator(); it.hasNext(); )
        {
            String arg = (String) it.next();
            cmd.createArgument().setValue( arg );
        }
        cmd.setWorkingDirectory( checkoutDir.getAbsolutePath() );
        getLogger().debug( "Command to run is: " + cmd.toString() );
        int exitValue = -1;
        try
        {
            exitValue = CommandLineUtils.executeCommandLine( cmd, new DefaultConsumer(), new DefaultConsumer() );
            getLogger().info( "Command returned exit value: " + exitValue );
            if ( exitValue != 0 )
            {
                getLogger().error( "Unable to execute command: '" + goals + "'" );
                throw new Exception( "Unable to execute command: '" + goals + "'" );
            }

            // For some reason Plexus-Utils being pulled from transitive deps 
            // complains about getPid() method not present
            //getLogger().debug( "Process ID : " + cmd.getPid() );
            //FileWriter fw = new FileWriter( new File( workspaceWorkingDir, "pid.txt" ), true );
            //fw.append( "# PID for goal(s): '" + goals + "' run on " + sdf.format( new Date() ) );
            //fw.write( "\n" + cmd.getPid() );
            //fw.close();

        }
        catch ( CommandLineException e )
        {
            getLogger().error( "Unable to execute command: '" + cmd.toString() + "'", e );
        }
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#updateProject(org.codehaus.plexus.xsiter.deployer.model.DeployableProject)
     */
    public void updateProject( DeployableProject project )
        throws Exception
    {
        if ( !isValidWorkspace( project.getLabel() ) )
        {
            throw new Exception( "Invalid workspace ID: '" + project.getLabel() + "'" );
        }
        DeploymentWorkspace workspace = createDeploymentWorkspaceIfRequired( project );
        String scmTag = null != project.getScmTag() ? project.getScmTag() : "HEAD";
        getLogger().info( "Updating Project: " + project.getLabel() + ", Version: " + scmTag );

        // TODO: refactor/add a method that creates a Deployable Project
        // instance from a Workspace descriptor

        // sanity for SCM info, source from workspace
        project.setScmURL( workspace.getScmURL() );
        project.setScmUsername( workspace.getScmUsername() );
        project.setScmPassword( workspace.getScmPassword() );

        try
        {
            ScmRepository repository = getScmRepository( project );
            ScmResult result;
            synchronized ( this )
            {
                File checkoutDir = new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() );
                try
                {
                    if ( checkoutDir.exists() )
                        FileUtils.cleanDirectory( checkoutDir );
                }
                catch ( IOException e )
                {
                    throw new Exception( "Could not clean directory : " + workspace.getWorkingDirectory(), e );
                }

                // check out to a tagged directory
                ScmFileSet fileSet = new ScmFileSet( new File( checkoutDir, scmTag ) );
                result = scmManager.getProviderByRepository( repository ).update( repository, fileSet, scmTag );
            }

            if ( !result.isSuccess() )
            {
                getLogger().warn( "Command output: " + result.getCommandOutput() );
                getLogger().warn( "Provider message: " + result.getProviderMessage() );
            }
            else
            {
                getLogger().info( "Project checked out successfully." );
            }

            // return result;
        }
        catch ( Exception e )
        {
            throw new Exception( "Cannot checkout sources.", e );
        }
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#getAllDeploymentWorkspaces()
     */
    public List getAllDeploymentWorkspaces()
        throws Exception
    {
        List list = new ArrayList();

        File workingDir = new File( workingDirectory );
        File[] files = workingDir.listFiles();
        for ( int i = 0; i < files.length; i++ )
        {
            File dir = files[i];
            if ( isWorkspaceDescriptorExistent( dir ) )
                list.add( loadWorkspaceFromDescriptor( dir.getName() ) );
            else if ( getLogger().isWarnEnabled() )
                getLogger().warn( "No workspace descriptor found under directory: " + dir.getName() );
        }

        return list;
    }

    /**
     * Tests if a directory under the Deployer working directory is a Deployment workspace.
     * 
     * @param dir Directory to test if its a deployment workspace.
     * @return <code>true</code> if the deployment workspace descriptor is found under specified directory.
     */
    private boolean isWorkspaceDescriptorExistent( File dir )
    {
        return new File( dir, DESCRIPTOR_WORKSPACE_XML ).exists();
    }

    /**
     * Tests if a workspace directory exists for the specified workspace/project Id.
     * @param pid workspace id to test for existence.
     * @return <code>true</code> if the workspace directory exists
     */
    private boolean isWorkspaceDirectoryExistent( String pid )
    {
        return new File( workingDirectory, pid ).exists();
    }

    /**
     * Tests if:<br>
     * <ul>
     * <li>a Deployment workspace directory exists, and </li>
     * <li>a Deployment descriptor exists</li>
     * </ul>
     * @param pid workspace Id to test for validity.
     * @return <code>true</code> if all of the above conditions are met, else <code>false</code>.
     */
    private boolean isValidWorkspace( String pid )
    {
        return ( isWorkspaceDirectoryExistent( pid ) && isWorkspaceDescriptorExistent( new File( workingDirectory, pid ) ) );
    }

}
