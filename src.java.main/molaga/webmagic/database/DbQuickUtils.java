package molaga.webmagic.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbQuickUtils {
	private static Logger logger = LoggerFactory.getLogger(DbQuickUtils.class);
	private static Properties props = new Properties();
	static String path;
	static {
		try {
			path = DbQuickUtils.class.getResource("/").toURI()
					.getPath()
					+ "jdbc.properties";
			props.load(new FileInputStream(path));

		} catch (URISyntaxException e) {
			logger.error("file path [{}] URISyntaxException .", path);
		} catch (FileNotFoundException e) {
			logger.error("jdbc.properties [{}] is not found.", path);
		} catch (IOException e) {
			logger.error("error occurs while reading jdbc.properties file [{}].", path);
		}

	}

	@Test
	public void getConnection() {
		String url = props.getProperty("jdbc.mysql.uri");
		String username = props.getProperty("jdbc.mysql.user");
		String password = props.getProperty("jdbc.mysql.passwd");
		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection con = DriverManager.getConnection(url, username,
					password);
			logger.debug("数据库联接成功...");
			Set<String> props = con.getClientInfo().stringPropertyNames();
			for (String s : props)
				logger.debug("\t{}", s);
			String sql = "select version()";
			ResultSet rs = con.prepareStatement(sql).executeQuery();
			while (rs.next())
				logger.info("MySQL版本号： {},", rs.getString(1));
		} catch (ClassNotFoundException e) {
			logger.error("找不到驱动程序类 ，加载驱动失败！");
		} catch (SQLException se) {
			logger.error("数据库连接失败！");
			se.printStackTrace();
		}
	}

}
