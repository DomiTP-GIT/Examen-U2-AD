import java.sql.Date;
import java.util.ArrayList;

public class Batalla {
  private final String nom;
  private final Date data;
  private final ArrayList<Barco> barcos;

  public Batalla(String nom, Date data) {
    this.nom = nom;
    this.data = data;
    this.barcos = new ArrayList<>();
  }

  public void addBarco(Barco b) {
    barcos.add(b);
  }

  public void imprimir() {
    String barc = "";
    for (Barco b : barcos) {
      barc += "\t" + b.getNom() + "\n";
    }
    System.out.println("Batalla de " + this.nom + ", que va tindre lloc el " + this.data + ". Van participar " + barcos.size() + " Barcos:\n" + barc);
  }


}
