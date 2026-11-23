import java.util.ArrayList;
import java.util.List;

// Nodo del árbol B+: los internos guardan claves e hijos; las hojas, claves y datos.
public class NodoArbolBMas {
    private boolean esHoja;
    private List<Integer> claves;
    // Vacía en las hojas
    private List<NodoArbolBMas> hijos;
    // Null en los nodos internos
    private List<String> datos;
    // Enlace a la hoja siguiente; null en los nodos internos
    private NodoArbolBMas siguiente;

    public NodoArbolBMas(boolean esHoja) {
        this.esHoja = esHoja;
        this.claves = new ArrayList<>();
        this.hijos = new ArrayList<>();
        this.datos = esHoja ? new ArrayList<>() : null;
        this.siguiente = null;
    }

    public boolean esHoja() {
        return esHoja;
    }

    public List<Integer> getClaves() {
        return claves;
    }

    public List<NodoArbolBMas> getHijos() {
        return hijos;
    }

    public List<String> getDatos() {
        return datos;
    }

    public NodoArbolBMas getSiguiente() {
        return siguiente;
    }

    public void setClaves(List<Integer> claves) {
        this.claves = claves;
    }

    public void setHijos(List<NodoArbolBMas> hijos) {
        this.hijos = hijos;
    }

    // Solo aplica a hojas
    public void setDatos(List<String> datos) {
        if (esHoja) {
            this.datos = datos;
        }
    }

    // Solo aplica a hojas
    public void setSiguiente(NodoArbolBMas siguiente) {
        if (esHoja) {
            this.siguiente = siguiente;
        }
    }
}
