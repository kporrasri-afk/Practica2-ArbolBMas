import java.util.ArrayList;

// Árbol B+ de orden m: cada nodo tiene entre m/2 y m hijos (la raíz, entre 2 y m).
public class ArbolBMas {
    private final int orden;
    private NodoArbolBMas raiz;

    public ArbolBMas(int orden) {
        if (orden < 3) {
            throw new IllegalArgumentException("El orden del árbol debe ser al menos 3.");
        }
        this.orden = orden;
        raiz = new NodoArbolBMas(true);
    }

    public int getOrden() {
        return orden;
    }

    // Mínimo de claves para cualquier nodo que no sea la raíz: ceil(m/2) - 1
    private int minimoClaves() {
        return (orden + 1) / 2 - 1;
    }

    public boolean estaVacio() {
        return raiz.esHoja() && raiz.getClaves().isEmpty();
    }

    // Retorna true si la clave es nueva, false si ya existía y solo se actualizó su dato
    public boolean insertar(int clave, String dato) {
        boolean esNueva = buscarDato(clave) == null;

        insertarRecursivo(this.raiz, clave, dato);

        // Si la raíz se desborda, el árbol crece un nivel
        if (this.raiz.getClaves().size() == orden) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false);
            nuevaRaiz.getHijos().add(this.raiz);
            dividirHijo(nuevaRaiz, 0);
            this.raiz = nuevaRaiz;
        }
        return esNueva;
    }

    // Inserta en la hoja y divide los nodos desbordados al regresar de la recursión
    private void insertarRecursivo(NodoArbolBMas nodo, int clave, String dato) {
        int i = nodo.getClaves().size() - 1;

        if (nodo.esHoja()) {
            while (i >= 0 && clave < nodo.getClaves().get(i)) {
                i--;
            }
            // Clave repetida: se actualiza el dato
            if (i >= 0 && nodo.getClaves().get(i) == clave) {
                nodo.getDatos().set(i, dato);
                return;
            }
            nodo.getClaves().add(i + 1, clave);
            nodo.getDatos().add(i + 1, dato);
        } else {
            while (i >= 0 && clave < nodo.getClaves().get(i)) {
                i--;
            }
            i++;
            NodoArbolBMas hijo = nodo.getHijos().get(i);

            insertarRecursivo(hijo, clave, dato);

            // Desbordamiento del hijo
            if (hijo.getClaves().size() == orden) {
                dividirHijo(nodo, i);
            }
        }
    }

    // Divide un nodo lleno en dos y sube una clave separadora al padre
    private void dividirHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas nodoLleno = padre.getHijos().get(indice);
        NodoArbolBMas nuevoNodo = new NodoArbolBMas(nodoLleno.esHoja());
        int mitad = orden / 2;

        if (nodoLleno.esHoja()) {
            int total = nodoLleno.getClaves().size();
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad, total)));
            nuevoNodo.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(mitad, total)));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));
            nodoLleno.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(0, mitad)));

            // Se mantiene el enlace entre hojas
            nuevoNodo.setSiguiente(nodoLleno.getSiguiente());
            nodoLleno.setSiguiente(nuevoNodo);

            // En hojas, la clave separadora se copia (no sale de la hoja)
            int claveMedia = nuevoNodo.getClaves().getFirst();
            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        } else {
            // En nodos internos, la clave media sube y sale del nodo
            int claveMedia = nodoLleno.getClaves().get(mitad);
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad + 1, nodoLleno.getClaves().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            nuevoNodo.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(mitad + 1, nodoLleno.getHijos().size())));
            nodoLleno.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(0, mitad + 1)));

            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
    }

    public boolean buscar(int clave) {
        return buscarDato(clave) != null;
    }

    public String buscarDato(int clave) {
        NodoArbolBMas hoja = buscarHoja(clave);
        int posicion = hoja.getClaves().indexOf(clave);
        return posicion >= 0 ? hoja.getDatos().get(posicion) : null;
    }

    // Baja desde la raíz hasta la hoja donde está o debería estar la clave
    private NodoArbolBMas buscarHoja(int clave) {
        NodoArbolBMas actual = raiz;
        while (!actual.esHoja()) {
            actual = actual.getHijos().get(indiceHijo(actual, clave));
        }
        return actual;
    }

    // Índice del hijo por el que se debe bajar; las claves iguales al separador van a la derecha
    private int indiceHijo(NodoArbolBMas nodo, int clave) {
        int i = 0;
        while (i < nodo.getClaves().size() && clave >= nodo.getClaves().get(i)) {
            i++;
        }
        return i;
    }

    // Retorna true si la clave existía y se eliminó
    public boolean eliminar(int clave) {
        boolean eliminado = eliminarRecursivo(raiz, clave);

        // Si la raíz queda sin claves, su único hijo pasa a ser la raíz
        if (!raiz.esHoja() && raiz.getClaves().isEmpty()) {
            raiz = raiz.getHijos().getFirst();
        }
        return eliminado;
    }

    // Elimina en la hoja y corrige los nodos con pocas claves al regresar de la recursión
    private boolean eliminarRecursivo(NodoArbolBMas nodo, int clave) {
        if (nodo.esHoja()) {
            int posicion = nodo.getClaves().indexOf(clave);
            if (posicion < 0) {
                return false;
            }
            nodo.getClaves().remove(posicion);
            nodo.getDatos().remove(posicion);
            return true;
        }

        int i = indiceHijo(nodo, clave);
        NodoArbolBMas hijo = nodo.getHijos().get(i);
        boolean eliminado = eliminarRecursivo(hijo, clave);

        if (eliminado) {
            if (hijo.getClaves().size() < minimoClaves()) {
                corregirSubdesbordamiento(nodo, i);
            }
            // Los separadores deben seguir siendo la menor clave de cada subárbol derecho
            actualizarSeparadores(nodo);
        }
        return eliminado;
    }

    // Primero intenta pedir prestado a un hermano; si no se puede, fusiona
    private void corregirSubdesbordamiento(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas izquierdo = indice > 0 ? padre.getHijos().get(indice - 1) : null;
        NodoArbolBMas derecho = indice < padre.getHijos().size() - 1 ? padre.getHijos().get(indice + 1) : null;

        if (izquierdo != null && izquierdo.getClaves().size() > minimoClaves()) {
            prestarDeIzquierdo(padre, indice, izquierdo, hijo);
        } else if (derecho != null && derecho.getClaves().size() > minimoClaves()) {
            prestarDeDerecho(padre, indice, hijo, derecho);
        } else if (izquierdo != null) {
            fusionar(padre, indice - 1);
        } else {
            fusionar(padre, indice);
        }
    }

    private void prestarDeIzquierdo(NodoArbolBMas padre, int indice, NodoArbolBMas izquierdo, NodoArbolBMas hijo) {
        if (hijo.esHoja()) {
            hijo.getClaves().addFirst(izquierdo.getClaves().removeLast());
            hijo.getDatos().addFirst(izquierdo.getDatos().removeLast());
            padre.getClaves().set(indice - 1, hijo.getClaves().getFirst());
        } else {
            // El separador baja al hijo y la última clave del hermano sube al padre
            hijo.getClaves().addFirst(padre.getClaves().get(indice - 1));
            hijo.getHijos().addFirst(izquierdo.getHijos().removeLast());
            padre.getClaves().set(indice - 1, izquierdo.getClaves().removeLast());
        }
    }

    private void prestarDeDerecho(NodoArbolBMas padre, int indice, NodoArbolBMas hijo, NodoArbolBMas derecho) {
        if (hijo.esHoja()) {
            hijo.getClaves().addLast(derecho.getClaves().removeFirst());
            hijo.getDatos().addLast(derecho.getDatos().removeFirst());
            padre.getClaves().set(indice, derecho.getClaves().getFirst());
        } else {
            // El separador baja al hijo y la primera clave del hermano sube al padre
            hijo.getClaves().addLast(padre.getClaves().get(indice));
            hijo.getHijos().addLast(derecho.getHijos().removeFirst());
            padre.getClaves().set(indice, derecho.getClaves().removeFirst());
        }
    }

    // Une el hijo en "indice" con su hermano derecho
    private void fusionar(NodoArbolBMas padre, int indice) {
        NodoArbolBMas izquierdo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);

        if (izquierdo.esHoja()) {
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getDatos().addAll(derecho.getDatos());
            // La hoja resultante hereda el enlace de la hoja eliminada
            izquierdo.setSiguiente(derecho.getSiguiente());
        } else {
            // En nodos internos, el separador del padre baja a la unión
            izquierdo.getClaves().add(padre.getClaves().get(indice));
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getHijos().addAll(derecho.getHijos());
        }
        padre.getClaves().remove(indice);
        padre.getHijos().remove(indice + 1);
    }

    private void actualizarSeparadores(NodoArbolBMas nodo) {
        for (int j = 0; j < nodo.getClaves().size(); j++) {
            nodo.getClaves().set(j, menorClave(nodo.getHijos().get(j + 1)));
        }
    }

    private int menorClave(NodoArbolBMas nodo) {
        while (!nodo.esHoja()) {
            nodo = nodo.getHijos().getFirst();
        }
        return nodo.getClaves().getFirst();
    }

    // Imprime n elementos desde la clave indicada (o la siguiente mayor) y retorna cuántos imprimió
    public int recorrerRango(int claveInicial, int n) {
        NodoArbolBMas hoja = buscarHoja(claveInicial);

        int posicion = 0;
        while (posicion < hoja.getClaves().size() && hoja.getClaves().get(posicion) < claveInicial) {
            posicion++;
        }

        int impresos = 0;
        while (hoja != null && impresos < n) {
            if (posicion < hoja.getClaves().size()) {
                System.out.println("  [" + hoja.getClaves().get(posicion) + "] -> " + hoja.getDatos().get(posicion));
                impresos++;
                posicion++;
            } else {
                // Fin de la hoja: se pasa a la siguiente
                hoja = hoja.getSiguiente();
                posicion = 0;
            }
        }
        return impresos;
    }

    // Recorre todas las hojas enlazadas en orden
    public void imprimirDatos() {
        NodoArbolBMas hoja = raiz;
        while (!hoja.esHoja()) {
            hoja = hoja.getHijos().getFirst();
        }
        while (hoja != null) {
            for (int i = 0; i < hoja.getClaves().size(); i++) {
                System.out.println("  [" + hoja.getClaves().get(i) + "] -> " + hoja.getDatos().get(i));
            }
            hoja = hoja.getSiguiente();
        }
    }

    public void imprimirArbol() {
        imprimirNodo(raiz, "", true);
    }

    private void imprimirNodo(NodoArbolBMas nodo, String indentacion, boolean esUltimo) {
        System.out.println(indentacion + "+- " + (nodo.esHoja() ? "Hoja > " : "Interno > ") + nodo.getClaves());
        indentacion += esUltimo ? "   " : "|  ";
        for (int i = 0; i < nodo.getHijos().size(); i++) {
            imprimirNodo(nodo.getHijos().get(i), indentacion, i == (nodo.getHijos().size() - 1));
        }
    }
}
