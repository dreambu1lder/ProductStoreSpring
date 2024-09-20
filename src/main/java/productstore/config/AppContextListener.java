package productstore.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import productstore.config.exception.DataSourceInitializationException;
import productstore.db.DataBaseUtil;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DataBaseUtil.initializeDefaultDataSource();
        } catch (Exception e) {
            throw new DataSourceInitializationException("Initialization of the main database DataSource failed.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DataBaseUtil.closeDataSource();
    }
}
