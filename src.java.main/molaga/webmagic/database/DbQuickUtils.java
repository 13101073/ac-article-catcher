package molaga.webmagic.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbQuickUtils {
	private static Logger logger = LoggerFactory.getLogger(DbQuickUtils.class);

	@Test
	public void getConnection() {
		String url = "jdbc:mysql://localhost:3306/acfun";
		String username = "zhou";
		String password = "zhou";
		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection con = DriverManager.getConnection(url, username, password);
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
