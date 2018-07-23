package servlet;

import java.util.ArrayList;
import java.util.List;

public class DatosCUPS {
	int N=100;
	String codigoCUPS = null;
	String fechaInicial = null;
	String fechaFinal = null;
	float consumoPico = 0;
	float consumoValle = 0;
	int[] histogramaConsumos = new int[N];
	List<String> fechas = new ArrayList<String>();
	List<String> hora = new ArrayList<String>();
	List<Float> energia = new ArrayList<Float>();
	List<Float> consumoMin = new ArrayList<Float>();
	List<Float> consumoMax = new ArrayList<Float>();
	
	DatosCUPS() {
		// inicializar arrays de consumos máximos y mínimos
		for(int i = 0; i < N; i++) {
			consumoMin.add((float) 999999999);
			consumoMax.add((float) -1);
		}
	}
}
