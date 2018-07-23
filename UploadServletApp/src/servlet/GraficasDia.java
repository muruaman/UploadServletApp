package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class EgunGrafikak
 */
@WebServlet("/EgunGrafikak")
public class GraficasDia extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GraficasDia() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Set response content type
		response.setContentType("text/html");

		DatosCUPS datos = (DatosCUPS) request.getSession().getAttribute("datos");
		int lineNumber = (int) request.getSession().getAttribute("line");
		String data = (String) request.getParameter("data");
		// Actual logic goes here.

		PrintWriter out = response.getWriter();

		request.getSession().setAttribute("datos", datos);
		request.getSession().setAttribute("line", lineNumber);
		
		CrearGrafica grafikak = new CrearGrafica(datos);

		String chartFilenameMin = "images/minimo.jpg?dummy=" + Long.toString(Instant.now().toEpochMilli());
		String chartFilenameMax = "images/maximo.jpg?dummy=" + Long.toString(Instant.now().toEpochMilli());
		String chartFilenameHist = "images/histograma.jpg?dummy=" + Long.toString(Instant.now().toEpochMilli());
		String chartFilenameMens = "images/mensual.jpg?dummy=" + Long.toString(Instant.now().toEpochMilli());

		boolean sortu = grafikak.crearGraficaDia(data);

		String chartFilenameEgun = "images/diario.jpg?dummy=" + Long.toString(Instant.now().toEpochMilli());

		PrintWriter writer = response.getWriter();
		writer.print("<html><head>");
		writer.print("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
		// writer.print("<script type=\"type/javascript\"
		// scr=\"js/funcion.js\"></script>");
		writer.print(
				"</head><body><div class=\"InfoGeneral\"><h3>INFORMACION GENERAL</h3><ul style=\"list-style-type:none\">");

		// muestra resultados totales

		writer.format(
				"<li>Código CUPS: %s</li><li>Datos registrados desde %s hasta %s</li><li>Consumo total en período PICO: %s</li><li>Consumo total en período VALLE: %s</li><li>%d medidas horarias registradas en total</li></ul></div>",
				datos.codigoCUPS, datos.fechaInicial, datos.fechaFinal, Float.toString(datos.consumoPico),
				Float.toString(datos.consumoValle), lineNumber);

		// menu de opciones
		writer.print("<div class=\"one\"><p><button class=\"button\" onclick=\"showImage()\">Minimo</button>");
		writer.print("<button class=\"button\" onclick=\"showMax()\">Maximo</button>");
		writer.print("<button class=\"button\" onclick=\"showHist()\">Histograma</button>");
		writer.print("<button class=\"button\" onclick=\"showMens()\">Mensuales</button>");
		writer.print("<button class=\"button\" onclick=\"showEguna()\">Diario</button>");
		writer.print("</p></div>");
		writer.print("<div class=\"two\"><p><form id=\"formEgunak\" action=\"EgunGrafikak\" method=\"post\">");
		writer.print("<input type=\"text\" value=\"\" name=\"data\"/>\r\n");
		writer.print("<input type=\"submit\" value=\"Grafica de dia\"/>");
		writer.print("<h5>Introduce en formato aaaa-mm-dd</h5>");
		writer.print("</p></from></div>");

		// lista de consumos mensuales
		writer.print(
				"<div id=\"mensual\" style=\"display: none;\"><h3>CONSUMO HORARIO FACTURADO (kWh)</h3><h4>(consumo por factura)</h4><div id=\"max\"><img src=\""
						+ chartFilenameMens + "\" height=\"700\" width=\"1400\"></div></div>");

		// muestra consumos mínimos y máximos
		// lista de consumos mínimos
		writer.print(
				"<div id=\"minimos\"><h3>CONSUMOS MÍNIMOS HORARIOS (kWh)</h3><h4>(ordenados de menor a mayor)</h4>");
		writer.print("<div id=\"min\"><img src=\"" + chartFilenameMin + "\" height=\"700\" width=\"700\"></div></div>");

		// lista de consumos máximos

		writer.print(
				"<div id=\"maximos\"><h3>CONSUMOS MÁXIMOS HORARIOS (kWh)</h3><h4>(ordenados de menor a mayor)</h4><div id=\"max\"><img src=\""
						+ chartFilenameMax + "\" height=\"700\" width=\"700\"></div></div>");

		// histograma de consumos

		writer.print(
				"<div id=\"histograma\"><h3>HISTOGRAMA DE CONSUMOS (115 W)</h3><h4>(según tramos de 115 Wh)</h4><div id=\"hist\"><img src=\""
						+ chartFilenameHist + "\" height=\"700\" width=\"700\"></div></div>");
		// Consumos de dia
		if (sortu == true) {
			writer.print("<div id=\"eguna\"><h3>CONSUMO DEL DIA " + data + " (kWh)</h3><div id=\"egun\"><img src=\""
					+ chartFilenameEgun + "\" height=\"700\" width=\"700\"></div></div>");
		} else {
			writer.print("<div id=\"eguna\"><h2>Introduce el formato correcto o fecha correcta</h2></div>");
		}
		writer.print(
				"<div class=\"pie\"><strong><a class=\"atras\" href=\"upload.jsp\">Atras</a></strong></div>");
		writer.print(
				"<script>function showImage(){\r\n" + "	document.getElementById('minimos').style.display = 'block';\r\n"
						+ "	document.getElementById('maximos').style.display = 'none';\r\n"
						+ "	document.getElementById('histograma').style.display = 'none';"
						+ "	document.getElementById('eguna').style.display = 'none';"
						+ "	document.getElementById('mensual').style.display = 'none';" + "}</script>");
		writer.print(
				"<script>function showMax(){\r\n" + "	document.getElementById('minimos').style.display = 'none';\r\n"
						+ "	document.getElementById('maximos').style.display = 'block';\r\n"
						+ "	document.getElementById('histograma').style.display = 'none';"
						+ "	document.getElementById('eguna').style.display = 'none';"
						+ "	document.getElementById('mensual').style.display = 'none';" + "}</script>");
		writer.print(
				"<script>function showHist(){\r\n" + "	document.getElementById('minimos').style.display = 'none';\r\n"
						+ "	document.getElementById('maximos').style.display = 'none';\r\n"
						+ "	document.getElementById('histograma').style.display = 'block';"
						+ "	document.getElementById('eguna').style.display = 'none';"
						+ "	document.getElementById('mensual').style.display = 'none';" + "}</script>");
		writer.print(
				"<script>function showMens(){\r\n" + "	document.getElementById('minimos').style.display = 'none';\r\n"
						+ "	document.getElementById('maximos').style.display = 'none';\r\n"
						+ "	document.getElementById('histograma').style.display = 'none';"
						+ "	document.getElementById('eguna').style.display = 'none';"
						+ "	document.getElementById('mensual').style.display = 'block';" + "}</script>");
		writer.print(
				"<script>function showEguna(){\r\n" + "	document.getElementById('minimos').style.display = 'none';\r\n"
						+ "	document.getElementById('maximos').style.display = 'none';\r\n"
						+ "	document.getElementById('histograma').style.display = 'none';"
						+ "	document.getElementById('eguna').style.display = 'block';"
						+ "	document.getElementById('mensual').style.display = 'none';" + "}</script>");

		writer.print("</body></html>");
		writer.flush();

		return;

	}
}
