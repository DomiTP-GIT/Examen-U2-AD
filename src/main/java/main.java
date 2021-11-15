import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class main {
  static Connexio conn = new Connexio();

  /**
   * Función principal del programa
   *
   * @param args Argumentos de ejecución
   */
  public static void main(String[] args) {
    boolean check = true;
    do {
      System.out.println("1.- BATALLA GUADALCANAL");
      System.out.println("2.- IMPRIMIR CLASES");
      System.out.println("3.- ACTUALIZAR CAÑONES");
      System.out.println("4.- MOSTRAR");
      System.out.println("5.- SALIR");
      int opc = Leer.leerEntero("Selecciona una opción: ");
      switch (opc) {
        case 1:
          Batalla b = loadBatalla("Guadalcanal"); // Carga una batalla
          b.imprimir(); // Imprime la batalla
          break;
        case 2:
          imprimirClasses(); // Imprime las clases
          break;
        case 3:
          actualitzaCanyons(); // Actualiza los cañones
          break;
        case 4:
          showDBTables(); // Muestra las bases de datos
          break;
        case 5:
          check = false; // Salir
          conn.closeConnection();
          break;
        default:
          System.out.println("Opción no válida");
      }
    } while (check);
  }

  /**
   * Mediante el nombre de una batalla, accede a la base de datos y recupera los datos de una
   * batalla y los barcos que participaron.
   *
   * @param nomBatalla Nombre de la batalla a buscar en la BD
   * @return batalla generada
   */
  public static Batalla loadBatalla(String nomBatalla) {
    Batalla b = null;
    String sql = "select * from participa where battle = '" + nomBatalla + "';"; // Sentencia para sacar los barcos que participan en una batalla.
    Statement st = null;
    try {
      st = conn.getMySQL().createStatement();

      ResultSet rs = st.executeQuery(sql);

      String battle = ""; // No es necesario, ya que solo hay una batalla con ese nombre, podría coger directamente el nombre que me pasa el usuario.
      ArrayList<String> ships = new ArrayList<>(); // Almacenar los nombres de los barcos de una batalla para luego poder obtener sus datos.

      while (rs.next()) {
        battle = rs.getString("battle"); // Siempre será la misma batalla.
        ships.add(rs.getString("ship")); // Cada batalla puede tener diferentes barcos.

      }

      rs.close();

      sql = "select * from battles where name = '" + battle + "'"; // Sentencia para obtener los datos de la batalla, el nombre ya lo tenemos pero necesitamos la fecha para crear el objeto.

      ResultSet infoBatalla = st.executeQuery(sql);

      while (infoBatalla.next()) {
        b = new Batalla(infoBatalla.getString("name"), infoBatalla.getDate("data")); // Crea una nueva batalla con los datos obtenidos de la consulta, como el nombre es clave primaria,
      }                                                                                    // solo me puede devolver una columna, así que no me preocupo por crear el objeto dentro del while.

      infoBatalla.close();

      for (String s : ships) { // Recorro todos los barcos para poder obtener su información.

        sql = "select * from ships where name = '" + s + "'"; // Sentencia para obtener todos los datos de cada barco

        ResultSet infoBarcos = st.executeQuery(sql);

        while (infoBarcos.next()) {
          b.addBarco(new Barco(infoBarcos.getString("name"), infoBarcos.getString("class"))); // Dentro del while, le añado a la batalla cada barco con addBarco y dentro creo un nuevo objeto con los datos de la consulta.
        }

        infoBarcos.close();

      }

      st.close();

      return b; // Devuelvo el objeto batalla que ahora contiene todos los datos de la batalla que se pide

    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    return null;
  }

  /**
   * Imprime las clases de barcos que hay
   */
  public static void imprimirClasses() {
    String sql = "select class, country, numGuns from classes order by 2 ASC, 3 DESC;"; // Consulta para obtener el nombre, pais y cañones, luego lo ordena ascendentemente por país
    Statement st = null;                                                                // y como secundario, lo ordena descendentemente por número de cañones.
    try {
      st = conn.getMySQL().createStatement();
      ResultSet rs = st.executeQuery(sql);

      System.out.println(String.format("%-25s %-25s %-25s", "Nom", "País", "Canyons")); // Muestra el título
      System.out.println("===============================================================");
      while (rs.next()) {
        System.out.println(String.format("%-25s %-25s %-25s", rs.getString("class"), rs.getString("country"), rs.getString("numGuns"))); // Imprime por pantalla los datos obtenidos de forma ordenada
      }

      rs.close();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }

  /**
   * Actualiza el número de cañones de una clase de barco
   */
  public static void actualitzaCanyons() {
    Statement st = null;
    ArrayList<String> classes = new ArrayList<>(); // Almacena el nombre de las clases para poder comprobar que la que nos pasa es correcta.
    int canyons = 0;
    try {
      st = conn.getMySQL().createStatement();

      String sql = "select * from classes;";

      ResultSet rs = st.executeQuery(sql);

      while (rs.next()) {
        classes.add(rs.getString("class").toUpperCase()); // Almaceno las clases
      }

      rs.close();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

    String nomClasse = Leer.leerTexto("Dime el nombre de la clase: "); // Le pido al usuario el nombre de la clase
    if (!classes.contains(nomClasse.toUpperCase())) { // Compruebo si no existe
      System.out.println("La clase " + nomClasse + " no existe.");
    } else { // Si existe, ejecuta el resto del programa
      String sql = "select numGuns from classes where class = '" + nomClasse + "';"; // Obtiene el número de cañones de esa clase.
      try {
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
          canyons = rs.getInt("numGuns"); // Almacena el número de cañones en una variable temporal
        }
        rs.close();

        boolean check = true;

        do {
          int nousCanyons = Leer.leerEntero("Dime el nuevo número de cañones para la clase " + nomClasse + ": "); // Pide en nuevo número de cañones y se fuerza a que sea INT.
          if (nousCanyons != canyons) { // Comprueba si el número de cañones es diferente, si lo es, sigue con las comprobaciones, si no, le indica al usuario su fallo.
            if (nousCanyons >= 0) { // Comprueba que el número que le ha pasado el usuario sea mayor a 0, si lo es, actualiza los datos, si no, le indica al usuario su fallo.
              check = false;
              sql = "UPDATE classes SET numGuns = " + nousCanyons + " WHERE (class = '" + nomClasse + "')"; // Actualiza el número de cañones de la clase.
              st.executeUpdate(sql);
              System.out.println("Se ha actualizado el número de cañones.");
            } else {
              System.out.println("El número de cañones no puede ser un número negativo.");
            }
          } else {
            System.out.println("El número de cañones no puede ser el mismo número.");
          }
        } while (check);
        st.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Muestra las tablas de la base de datos e indica el número de campos de cada una.
   */
  public static void showDBTables() {
    ArrayList<String> tables = new ArrayList<>(); // Almaceno los nombres de las tablas para luego contar sus campos.
    try {
      DatabaseMetaData metaData = conn.getMySQL().getMetaData();
      System.out.println(String.format("%-15s %-15s", "Tabla", "Numero de Camps"));
      System.out.println("=================================");
      ResultSet rsmd = metaData.getTables("ships", null, null, null);
      while (rsmd.next()) {
        tables.add(rsmd.getString(3)); // Añado el nombre de las tablas
      }

      for (String table : tables) {
        int contador = 0;
        ResultSet columnes = metaData.getColumns("ships", null, table, null);
        while (columnes.next()) {
          contador += 1; // Cuento sus columnas
        }
        System.out.println(String.format("%-15s %-15d", table, contador)); // Imprimo la tabla completa.
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }
}
