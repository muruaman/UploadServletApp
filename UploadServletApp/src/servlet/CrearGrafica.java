package servlet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;


/**
 * Clase java diseñada exclusivamente para generar graficas mediante la libreria
 * jfreechart Estos graficos seran guardados en formato .jpg en la ruta
 * especificada.
 * 
 * @author Josu Murua
 * @version 1 @
 * 
 */
public class CrearGrafica {

	// Datos del fichero .cvs
	private DatosCUPS datos;

	// Constructor por defecto
	public CrearGrafica() {
		// TODO Auto-generated constructor stub
	}

	// Constructor con datos del .csv
	public CrearGrafica(DatosCUPS datos) {
		this.datos = datos;
	}

	/**
	 * Metodo que crea un histograma y lo guarda en formato .jpg
	 * 
	 */
	void crearGrafica() {

		// Objeto de la libreria jfreechart que contendra los dato para generar los
		// graficos
		
		final XYSeries series = new XYSeries("RamdomData");
		Date dat;

		try {
			// Añadimos los valores al dataset
			for (int i = 0; i < datos.energia.size(); i++) {
				String dateTime = datos.fechas.get(i) + " " + datos.hora.get(i) + ":00";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				dat = sdf.parse(datos.fechas.get(i) + " " + datos.hora.get(i) + ":00");

				series.add(dat.getTime(), datos.energia.get(i));
			}

			XYSeriesCollection dataset = new XYSeriesCollection(series);

			// Genera el grafico
			JFreeChart chart = ChartFactory.createXYLineChart("Consumo", "Dias", "Potencia", dataset,
					PlotOrientation.VERTICAL, false, true, false);
			XYPlot plot = (XYPlot) chart.getPlot();

			DateAxis xAxis = new DateAxis("Date");
			plot.setBackgroundPaint(Color.WHITE);
			plot.setOutlinePaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);
			plot.setDomainAxis(xAxis);

			int seriesCount = plot.getSeriesCount();
			  for (int i = 0; i < seriesCount; i++) {
			    plot.getRenderer().setSeriesStroke(i, new BasicStroke(2));
			}

			// Guarda el grafico
			ChartUtilities.saveChartAsJPEG(
					new File("C:\\Users\\josum\\eclipse-workspace\\images\\mensual.jpg"),
					chart, 1400, 700);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Image creating error");
		}
	}

	/**
	 * Metodo que crea un histograma y lo guarda en formato .jpg
	 * 
	 */
	void crearHistograma() {

		// Objeto de la libreria jfreechart que contendra los dato para generar los
		// graficos
		final XYSeries series = new XYSeries("RamdomData");

		try {
			// Añadimos los valores al dataset
			for (int i = 0; i < 100; i++) {

				series.add(i*115, datos.histogramaConsumos[i]);
				
			}

			XYSeriesCollection dataset = new XYSeriesCollection(series);

			// Genera el grafico
			JFreeChart chart = ChartFactory.createXYBarChart("Histograma", "Potencia", false,
					"Numero datos registrados", dataset, PlotOrientation.VERTICAL, false, false, false);
			XYPlot plot = (XYPlot) chart.getPlot();
			
			plot.setBackgroundPaint(Color.WHITE);
			plot.setOutlinePaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);
			XYBarRenderer br=(XYBarRenderer) plot.getRenderer();
			br.setShadowVisible(false);
			
			int seriesCount = plot.getSeriesCount();
			  for (int i = 0; i < seriesCount; i++) {
			    br.setSeriesStroke(i, new BasicStroke(4));
			    br.setSeriesPaint(i, Color.green);	
			}
			// Guarda el grafico
			ChartUtilities.saveChartAsJPEG(
					new File(
							"C:\\Users\\josum\\eclipse-workspace\\images\\histograma.jpg"),
					chart, 700, 700);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Image creating error");
		}
	}

	/**
	 * Metodo que crea un grafico con minimos y lo guarda en formato .jpg
	 * 
	 */
	void crearMinimo() {

		// Objeto de la libreria jfreechart que contendra los datos para generar los
		// graficos

		final XYSeries series = new XYSeries("RamdomData");
		
		try {
			// Añadimos los valores al dataset
			for (int i = 0; i < datos.consumoMin.size(); i++) {

				series.add(i, datos.consumoMin.get(i));
			}
			XYSeriesCollection dataset = new XYSeriesCollection(series);

			// Genera el grafico
			JFreeChart chart = ChartFactory.createXYBarChart("Histograma", "Potencia", false,
					"Numero datos registrados", dataset, PlotOrientation.VERTICAL, false, false, false);
			XYPlot plot = (XYPlot) chart.getPlot();
			
			ValueMarker mark = new ValueMarker(0.1);// Posicion del marcador
			mark.setLabel("Cosas");// Normbre de marcador
			mark.setLabelTextAnchor(TextAnchor.BASELINE_LEFT);// Mover Referencia marcador
			mark.setPaint(Color.RED);// Color marca
			mark.setStroke(new BasicStroke(2));
			
			ValueAxis rangeAxis = plot.getRangeAxis();
			rangeAxis.setAutoRange(true);
			rangeAxis.setAutoRangeMinimumSize(0.15);// Tamaño minimo eje y
			rangeAxis.setLowerBound(0);
			plot.setRangeAxis(rangeAxis);
			plot.setBackgroundPaint(Color.WHITE);
			plot.setOutlinePaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);
			plot.getDomainAxis().setVisible(false);

			plot.addRangeMarker(mark);// Añadir marca
			plot.getRenderer().setSeriesPaint(0,Color.green);

			// Guarda el grafico
			ChartUtilities.saveChartAsJPEG(
					new File("C:\\Users\\josum\\eclipse-workspace\\images\\minimo.jpg"),
					chart, 700, 700);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Image creating error");
		}
	}

	/**
	 * Metodo que crea un grafico con maximos y lo guarda en formato .jpg m
	 */
	void crearMaximo() {

		// Objeto de la libreria jfreechart que contendra los datos para generar los
		// graficos

		final XYSeries series = new XYSeries("Maximos");

		try {
			for (int i = 0; i < datos.consumoMax.size(); i++) {

				series.add(i, (double) datos.consumoMax.get(i));
			}
			XYSeriesCollection dataset = new XYSeriesCollection(series);


			// Genera el grafico
			JFreeChart chart = ChartFactory.createXYBarChart("Maximo", "Datos registrados",false, "Potencia (kWH)", dataset,
					PlotOrientation.VERTICAL, false, true, false);

			chart.setBorderPaint(Color.BLACK); 		
			chart.setBackgroundPaint(Color.WHITE);

			XYPlot plot = (XYPlot) chart.getPlot();

			plot.setBackgroundPaint(Color.WHITE);
			plot.setOutlinePaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);
			plot.getDomainAxis().setVisible(false);
			
			plot.getRenderer().setSeriesPaint(0,Color.green);
			
			// Guarda el grafico

			ChartUtilities.saveChartAsJPEG(
					new File("C:\\Users\\josum\\eclipse-workspace\\images\\maximo.jpg"),
					chart, 700, 700);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Image creating error");
		}

	}

	boolean crearGraficaDia(String Fecha) {

		// Objeto de la libreria jfreechart que contendra los dato para generar los
		// graficos
		final XYSeries series = new XYSeries("RamdomData");
		Date dat;
		boolean sortu = false;

		try {
			// Añadimos los valores al dataset
			for (int i = 0; i < datos.energia.size(); i++) {
				if (Fecha.equals(datos.fechas.get(i))) {
					String dateTime = datos.fechas.get(i) + " " + datos.hora.get(i) + ":00";
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					dat = sdf.parse(datos.fechas.get(i) + " " + datos.hora.get(i) + ":00");

					sortu = true;
					series.add(dat.getTime(), datos.energia.get(i));
				}
			}

			XYSeriesCollection dataset = new XYSeriesCollection(series);

			// Genera el grafico
			JFreeChart chart = ChartFactory.createXYLineChart("Consumo", "Dias", "Potencia", dataset,
					PlotOrientation.VERTICAL, false, true, false);
			XYPlot plot = (XYPlot) chart.getPlot();

			DateAxis xAxis = new DateAxis("Hora");
			plot.setBackgroundPaint(Color.WHITE);
			plot.setOutlinePaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);
			plot.setDomainAxis(xAxis);

			int seriesCount = plot.getSeriesCount();
			  for (int i = 0; i < seriesCount; i++) {
			    plot.getRenderer().setSeriesStroke(i, new BasicStroke(2));
			}

			// Guarda el grafico
			ChartUtilities.saveChartAsJPEG(
					new File("C:\\Users\\josum\\eclipse-workspace\\images\\diario.jpg"),
					chart, 700, 700);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Image creating error");
		}
		return sortu;
	}
}
