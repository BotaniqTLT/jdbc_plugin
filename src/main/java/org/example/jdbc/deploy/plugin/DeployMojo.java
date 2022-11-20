package org.example.jdbc.deploy.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

@Mojo(name = "deploy")
public class DeployMojo extends AbstractMojo {


    @Parameter(property = "jdbcUrl")
    private String jdbcUrl;

    @Parameter(property = "jdbcLogin")
    private String jdbcLogin;

    @Parameter(property = "jdbcDriver")
    private String jdbcDriver;

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
        getLog().info(jdbcLogin + ":" + jdbcPassword + "@" + jdbcUrl);
        getLog().info("Version:" + moduleVersion);
        getLog().info("Module:" + moduleId);
        getLog().info("Driver:" +jdbcDriver);
        getLog().info("Jar:" + jarFile);

        Date date = new Date();
        File file = new File(jarFile);

        String sql = "SELECT id, jar_file_name from ciska_modules WHERE jar_file_name = ?";


        DriverManagerDataSource ds = new DriverManagerDataSource(jdbcUrl, jdbcLogin, jdbcPassword);

        ds.setDriverClassName(jdbcDriver);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        LobHandler lobHandler = new DefaultLobHandler();

        List<JarFile> selectQuery = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(JarFile.class), file.getName());

        try {
            FileInputStream imageIs = new FileInputStream(file);
            if (!file.getName().endsWith(".jar")) {
                throw new RuntimeException("File don`t jar extension");
            }

            if (selectQuery.size() == 0) {
                Integer id = jdbcTemplate.queryForObject("SELECT MAX(id)+1 FROM ciska_modules", Integer.class);
                if (id == null) {
                    id = 1;
                }
                jdbcTemplate.update("INSERT INTO ciska_modules (jar_file, jar_file_name, name, created_date, udated_date, jar_file_ext,id) " +
                                "VALUES (?,?,?,?,?,?,?)",
                        new Object[]{
                                new SqlLobValue(imageIs, (int) file.length(), lobHandler),
                                file.getName(),
                                file.getName(),
                                date,
                                date,
                                "jar",
                                id
                        },
                        new int[]{Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.DATE, Types.VARCHAR, Types.INTEGER});
            } else {
                int update = jdbcTemplate.update("UPDATE ciska_modules SET jar_file = ?, udated_date = ? WHERE jar_file_name = ? ",

                        new Object[]{
                                new SqlLobValue(imageIs, (int) file.length(), lobHandler),
                                date,
                                file.getName()

                        },
                        new int[]{Types.BLOB, Types.TIMESTAMP, Types.VARCHAR});

                if (update ==1) {
                   getLog().info("successful update");
                }else {
                    getLog().info("fail update");
                    throw new RuntimeException("fail update");
                }

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //action with jdbc

    }
}
