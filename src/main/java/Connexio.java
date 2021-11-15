import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexio {
  private final String server = "localhost";
  private final String port = "3308";
  private final String dbname = "ships";
  private final String user = "examenAD02";
  private final String pass = "examenAD02";
  private Connection MySQL = null;

  public Connexio() {
    MySQL = this.connectMySQL();
  }

  /**
   * Conectar con una BD MySQL
   *
   * @return connexió
   */
  public Connection connectMySQL() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      String connectionUrl = "jdbc:mysql://" + this.server + ":" + this.port + "/" + this.dbname + "?useUnicode=true&characterEncoding=UTF-8";

      return DriverManager.getConnection(connectionUrl, this.user, this.pass);

    } catch (ClassNotFoundException | SQLException e) {
      System.out.println("No se ha podido conectar a la base de datos de MySQL.");
    }

    return null;

  }

  /**
   * Cierra la conexión de la BD de MySQL
   */
  public void closeConnection() {
    try {
      this.connectMySQL().close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  /**
   * Conecta con una base de datos SQLite
   *
   * @param dbname Nombre de la base de datos
   * @return connexió
   */
  public Connection connectSQLite(String dbname) {

    String url = "jdbc:sqlite:" + dbname;

    try {
      Connection conn = DriverManager.getConnection(url);

      return conn;
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  public Connection getMySQL() {
    return MySQL;
  }
}
