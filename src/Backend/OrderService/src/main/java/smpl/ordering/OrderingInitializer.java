package smpl.ordering;

import kieker.monitoring.core.controller.MonitoringController;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * This is the application starting point when the application is deployed in Tomcat on a server.
 */
public class OrderingInitializer
        extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(OrderingConfiguration.class);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException
    {
        super.onStartup(servletContext);

        if (servletContext != null)
        {
            servletContext.addListener(new ServletContextListener() {
                @Override
                public void contextInitialized(ServletContextEvent sce)
                {
                    // no-op
                }

                @Override
                public void contextDestroyed(ServletContextEvent sce)
                {
                    MonitoringController.getInstance().terminateMonitoring();
                }
            });

            String path = servletContext.getContextPath();
            if (path != null)
            {
                s_applicationPath = path;
            }
        }
    }

    public static String getApplicationPath()
    {
        return s_applicationPath;
    }

    private static String s_applicationPath = "";
}
