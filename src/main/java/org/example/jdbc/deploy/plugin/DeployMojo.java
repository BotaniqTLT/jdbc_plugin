package org.example.jdbc.deploy.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Mojo(name = "deploy")
public class DeployMojo extends AbstractMojo {

    @Parameter(property = "jdbcUrl")
    private String jdbcUrl;

    @Parameter(property = "jdbcLogin")
    private String jdbcLogin;

    @Parameter(property = "jdbcPassword")
    private String jdbcPassword;

    @Parameter(property = "moduleId")
    private String moduleId;

    @Parameter(property = "moduleVersion")
    private String moduleVersion;

    @Parameter(property = "jarFile")
    private String jarFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Deploy must be here");
        getLog().info(jdbcLogin+":"+jdbcPassword+"@"+jdbcUrl);
        getLog().info("Version:"+moduleVersion);
        getLog().info("Module:"+moduleId);
        getLog().info("Jar:"+jarFile);

        DriverManagerDataSource ds = new DriverManagerDataSource(jdbcUrl, jdbcLogin, jdbcPassword);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

        //action with jdbc

    }
}
