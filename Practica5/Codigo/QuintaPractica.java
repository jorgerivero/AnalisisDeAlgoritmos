/*
	IPN - ESCOM
	Análisis de Algoritmos
	Prof: Benjamín Luna Benoso
	Grupo: 3CV1
	Práctca 5:Algoritmos Greedy
	---------------------------------------------
    Creación: 9/Diciembre/2020
    Creado por: 
        + Omar Imanol Rivero Ronquillo
		+ Luis Eduardo Valle Martínez
		
	Funcionamiento
	>>>>>>>>>>>>>>
		El programa permite ejecutar los algoritmos para la compresión de
		archivos de texto plano mediante el algoritmo de Huffman.
		Y el algoritmo para encontrar el árbol de recubrimiento mínimo
		mediante el algoritmo de Kruskal.
		
		Es necesario indicar el algoritmo que se quiere evaluar mediante
		el ingreso de parámetros precedidos por un guión donde:
			-k : Algoritmo de Kruskal
			-h : Algoritmo de Huffman
			
	Compilación
	+++++++++++
		$ javac -cp .:../Bibliotecas/jfreechart-1.0.19.jar:../Bibliotecas/jcommon-1.0.23.jar QuintaPractica.java

	Ejecución Kruskal
	+++++++++++++++++
		$ java -cp .:../Bibliotecas/jfreechart-1.0.19.jar:../Bibliotecas/jcommon-1.0.23.jar QuintaPractica -k ../Archivos/Grafo3 ../Archivos/Grafo4 ../Archivos/Grafo5 ../Archivos/Grafo6 ../Archivos/Grafo7 ../Archivos/Grafo8 ../Archivos/Grafo9 ../Archivos/Grafo10 

	Ejecución Huffman
	+++++++++++++++++
		$ java -cp :../Bibliotecas/jfreechart-1.0.19.jar:../Bibliotecas/jcommon-1.0.23.jar QuintaPractica -h ../Archivos/Merge
*/

import java.util.*;

import java.lang.Math;

public class QuintaPractica {
	// Constantes de colores para impresión en consola
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_BLUE = "\u001B[34m";

	// Imprime un mensaje de error y termina la ejecución del programa
	public static void error(String mensaje) {
		System.out.printf(ANSI_YELLOW + "\nERROR >> %s" + ANSI_RESET + "\n", mensaje);
		System.exit(-1);
	}

	// Imprime un mensaje con cierta estructura en la consola
	public static void mensaje(String mensaje) {
		System.out.printf(ANSI_GREEN + "\n --- %s ---" + ANSI_RESET + "\n", mensaje);
	}

	public static void main(String[] args) {

		if (args.length > 0) { // Verifica que se hayan ingresado argumentos de ejecución
			int indiceRutasArchivos = 0;
			int argumentos = 0;
			/*
			 * Argumentos//decimal: 
			 * 	- k/Kruskal = 1
			 * 	- h/Huffman = 2
			 * 		-d/Descomprimir
			 */

			if (args.length == 1) // Solo se ingresa un argumento
				error("Debe ser ingresado al menos un argumento para especificar el algoritmo y/o la ruta de un archivo con los valores del tamaño de los arreglos");
			else { // Se debe identifican los argumentos ingresados
				if (args[0].indexOf('-') >= 0) {
					indiceRutasArchivos = 1;

					for (char argumento : args[0].toCharArray()) {
						switch (argumento) {
							case 'k':
								argumentos += 1;
								break;
							case 'h':
								argumentos += 2;
								break;
							case 'd':
								if((argumentos & 2) > 0)
									argumentos += 4;	
								else
									QuintaPractica.error("Para ingresar el argumento 'd' es necesario indicar antes el argumento 'h'");
								break;
						}
					}
				}
			}
			if(indiceRutasArchivos < args.length){ // Verificación de que se ingrese la ruta a los archivos con los árboles
				/*
					Ejecución de los algoritmos
				*/
				Graficas grafica = new Graficas(1, 1);

				// Ejecución de los algoritmo según los bits activos de los argumentos ingresados
				if ((argumentos & 1) > 0) { // Algoritmo Kruskal
					Grafo grafo;
					int indiceColeccion = -1;
					Kruskal kruskal = new Kruskal();

					System.out.println("\n Kruskal \n---------------");

					for(int indiceArchivo=indiceRutasArchivos;indiceArchivo<args.length;indiceArchivo++){ // Se obtienen los árboles de las rutas a los archivos en los argumentos
						grafo = Archivos.archivo(args[indiceArchivo]).getGrafo();
					
						kruskal.kruskal(grafo); 

						System.out.printf("P%d( %d ,%d )\n",indiceArchivo,grafo.numeroNodos(),kruskal.numeroOperaciones);

						indiceColeccion = grafica.agregarParOrdenado(
							"Kruskal", 
							(double)grafo.numeroNodos(),
							kruskal.numeroOperaciones, 
							indiceColeccion);
					}

					grafica.crearGrafica(
						"Graficación del número de nodos en el grafo vs instrucciones ejecutadas para el algoritmo Kruskal",
						"Valor(n)", 
						"NúmeroOperaciones", 
						indiceColeccion);
				}

				if ((argumentos & 2) > 0) { // Algoritmo Huffman
					int indiceColeccion = -1;
					int punto = 1;

					String path = "../Archivos/ArchivosHuffman";
					ArrayList<String> listaArchivos = Archivos.listaArchivos(path);
					

					for(String filename: listaArchivos){
						String textoOriginal = Archivos.archivo(filename).leerArchivo(path, filename);
						System.out.println("\n Huffman \n---------------");

						Huffman huff = new Huffman();
						int n = huff.charList(textoOriginal).size(); // abscisas
						huff.numOperaciones = 0; // ordenadas

						NodoHuffman raiz = huff.crearArbol(textoOriginal);

						String encode = huff.comprimir(textoOriginal);
						String decode = huff.descomprimir(raiz, encode);
						
						System.out.println("Texto original: ");
						System.out.println(textoOriginal);
						System.out.println("--------------------------------------------------");
						System.out.println("Arbol de Huffman: ");
						System.out.println("Char\t|\tHuffman");
						Huffman.impArbol(raiz, "");
						System.out.println("--------------------------------------------------");
						System.out.println("Texto comprimido:");
						System.out.println(encode);
						System.out.println("--------------------------------------------------");
						System.out.println("Texto descomprimido:");
						System.out.println(decode);
						System.out.println("--------------------------------------------------");
						System.out.println("Tasa de compresion: " + huff.tasaDeCompresion(decode, encode));
						
						System.out.println("--------------------------------------------------");
						System.out.printf(" P%d( %d , %d )\n",punto,n,huff.numOperaciones);
						System.out.println("__________________________________________________");

						punto ++;

						indiceColeccion = grafica.agregarParOrdenado(
							"huffman", 
							(double)n,
							huff.numOperaciones, 
							indiceColeccion);
					}

					grafica.crearGrafica(
						"Graficación del tamaño n de nodos en el árbol vs instrucciones ejecutadas para el algoritmo Huffman",
						"Valor(n)", 
						"NúmeroOperaciones", 
						indiceColeccion);
				}
				
				grafica.mostrarGrafica();
				
			}else
				QuintaPractica.error("Es necesario el ingreso de al menos 1 ruta a los archivos definitorios de una estructura árbol");
		} else
			QuintaPractica.error("Son necesarios argumentos para la ejecución del programa\n" + ANSI_GREEN
					+ "i.e: $ java QuintaPractica -[k,h] archivo_arbol_1 archivo_arbol_2 archivo_arbol_3 ...  archivo_arbol_n");
	}
}
