import java.util.Scanner;

// Interfaz de consola para el árbol B+
public class Main {
    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("        SIMULADOR DE ÁRBOL B+ (SOFT-10)        ");
        System.out.println("==============================================");

        // Se pide el orden del árbol al iniciar
        int orden = leerEntero("Ingrese el orden del árbol (mínimo 3, recomendado 4): ");
        while (orden < 3) {
            System.out.println("El orden debe ser 3 o mayor.");
            orden = leerEntero("Ingrese el orden del árbol: ");
        }

        ArbolBMas arbol = new ArbolBMas(orden);
        menu(arbol);

        System.out.println("¡Hasta luego!");
        ENTRADA.close();
    }

    // Menú principal; se repite hasta elegir 0
    private static void menu(ArbolBMas arbol) {
        int opcion;
        do {
            System.out.println();
            System.out.println("----------------- MENÚ (orden " + arbol.getOrden() + ") -----------------");
            System.out.println("1. Insertar un registro (clave y dato)");
            System.out.println("2. Buscar un registro por clave");
            System.out.println("3. Eliminar un registro por clave");
            System.out.println("4. Recorrer un rango (desde una clave, n elementos)");
            System.out.println("5. Mostrar la estructura del árbol");
            System.out.println("6. Mostrar todos los datos en orden");
            System.out.println("7. Cargar datos de ejemplo");
            System.out.println("0. Salir");
            opcion = leerEntero("Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1 -> opcionInsertar(arbol);
                case 2 -> opcionBuscar(arbol);
                case 3 -> opcionEliminar(arbol);
                case 4 -> opcionRecorrerRango(arbol);
                case 5 -> opcionMostrarArbol(arbol);
                case 6 -> opcionMostrarDatos(arbol);
                case 7 -> cargarDatosEjemplo(arbol);
                case 0 -> System.out.println("Saliendo del programa...");
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private static void opcionInsertar(ArbolBMas arbol) {
        int clave = leerEntero("Clave (número entero): ");
        String dato = leerTexto("Dato asociado: ");
        if (arbol.insertar(clave, dato)) {
            System.out.println("Registro [" + clave + "] insertado correctamente.");
        } else {
            System.out.println("La clave " + clave + " ya existía; su dato fue actualizado.");
        }
    }

    private static void opcionBuscar(ArbolBMas arbol) {
        int clave = leerEntero("Clave a buscar: ");
        String dato = arbol.buscarDato(clave);
        if (dato != null) {
            System.out.println("Encontrado: [" + clave + "] -> " + dato);
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void opcionEliminar(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío, no hay nada que eliminar.");
            return;
        }
        int clave = leerEntero("Clave a eliminar: ");
        if (arbol.eliminar(clave)) {
            System.out.println("Registro [" + clave + "] eliminado. Estructura resultante:");
            arbol.imprimirArbol();
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void opcionRecorrerRango(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío.");
            return;
        }
        int clave = leerEntero("Clave inicial: ");
        int n = leerEntero("Cantidad de elementos a mostrar: ");
        if (n <= 0) {
            System.out.println("La cantidad debe ser mayor que cero.");
            return;
        }
        System.out.println("Registros desde la clave " + clave + ":");
        int impresos = arbol.recorrerRango(clave, n);
        if (impresos == 0) {
            System.out.println("  (No hay registros con clave mayor o igual a " + clave + ")");
        } else if (impresos < n) {
            System.out.println("  (Solo había " + impresos + " registro(s) disponibles en ese rango)");
        }
    }

    private static void opcionMostrarArbol(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío.");
            return;
        }
        arbol.imprimirArbol();
    }

    private static void opcionMostrarDatos(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío.");
            return;
        }
        System.out.println("Datos en orden (recorriendo las hojas enlazadas):");
        arbol.imprimirDatos();
    }

    // Carga registros de prueba para la demostración
    private static void cargarDatosEjemplo(ArbolBMas arbol) {
        int[] claves = {10, 20, 5, 6, 15, 30, 25, 40, 45, 50, 35, 12};
        String[] datos = {"Ana", "Bruno", "Carla", "Diego", "Elena", "Fabián",
                "Gabriela", "Hugo", "Isabel", "Jorge", "Karla", "Luis"};
        for (int i = 0; i < claves.length; i++) {
            arbol.insertar(claves[i], datos[i]);
        }
        System.out.println("Se cargaron " + claves.length + " registros de ejemplo:");
        arbol.imprimirArbol();
    }

    // Repite la pregunta hasta recibir un entero válido
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = ENTRADA.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número entero válido.");
            }
        }
    }

    // Repite la pregunta hasta recibir un texto no vacío
    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = ENTRADA.nextLine().trim();
            if (!linea.isEmpty()) {
                return linea;
            }
            System.out.println("El dato no puede estar vacío.");
        }
    }
}
