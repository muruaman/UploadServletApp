package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class Grafikak
 */
@WebServlet("/Grafikak")
public class Graficas extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String UPLOAD_DIRECTORY = "upload";
	private static final int THRESHOLD_SIZE = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Graficas() {
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
		// checks if the request actually contains upload file

		if (!ServletFileUpload.isMultipartContent(request)) {
			PrintWriter writer = response.getWriter();
			writer.println("Request does not contain upload data");
			writer.flush();
			return;
		}

		// configures upload settings
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(THRESHOLD_SIZE);
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(MAX_FILE_SIZE);
		upload.setSizeMax(MAX_REQUEST_SIZE);

		// constructs the directory path to store upload file
		String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
		// creates the directory if it does not exist
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		try {
			// parses the request's content to extract file data
			List formItems = upload.parseRequest(request);
			Iterator iter = formItems.iterator();
			DatosCUPS datosCUPS = new DatosCUPS();
			CrearGrafica grafikak;

			// iterates over form's fields
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				// processes only fields that are not form fields
				if (!item.isFormField()) {
					String fileName = new File(item.getName()).getName();
					String filePath = uploadPath + File.separator + fileName;
					File storeFile = new File(filePath);

					// saves the file on disk
					item.write(storeFile);
					// se guarda en
					// WORKSPACE/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/UploadServletAppupload

					// aquí deben procesarse los datos

					final int N = 100;

					// DatosCUPS datosCUPS = new DatosCUPS();

					float energia = 0;

					BufferedReader br = null;
					String line = "";
					String cvsSplitBy = ";";

					int lineNumber = 0;

					try {

						br = new BufferedReader(new FileReader(filePath));
						while ((line = br.readLine()) != null) {

							// si primera línea lee otra más
							if (lineNumber == 0) {
								line = br.readLine();
							}
							;

							lineNumber++;

							// use ; as separator
							String[] nextLine = line.split(cvsSplitBy);

							// lee datos
							String cups = nextLine[0];
							String fecha = nextLine[1];
							int dia = Integer.parseInt(fecha.substring(0, 2));
							int mes = Integer.parseInt(fecha.substring(3, 5));
							int ano = Integer.parseInt(fecha.substring(6, 10));
							String addfecha = fecha.substring(6, 10) + "-" + fecha.substring(3, 5) + "-"
									+ fecha.substring(0, 2);

							String stringEnergia = nextLine[3];
							String stringHora = nextLine[2];
							int hora = Integer.parseInt(stringHora);
							energia = Float.parseFloat(stringEnergia.replace(',', '.'));

							// lee CUPS y fecha inicial
							if (lineNumber == 1) {
								datosCUPS.codigoCUPS = cups;
								datosCUPS.fechaInicial = fecha;
								datosCUPS.fechaFinal = fecha;
							}
							// Añade energia y fecha
							datosCUPS.hora.add(stringHora);
							datosCUPS.fechas.add(addfecha);
							datosCUPS.energia.add(energia);

							// comprueba fechas inicial y final

							if (datosCUPS.fechaInicial.substring(3, 5).compareTo(fecha.substring(3, 5)) >= 0) {
								if (datosCUPS.fechaInicial.substring(0, 2).compareTo(fecha.substring(0, 2)) > 0)
									datosCUPS.fechaInicial = fecha;
							}
							// if(datosCUPS.fechaInicial.compareTo(fecha) > 0) datosCUPS.fechaInicial =
							// fecha;
							// if(datosCUPS.fechaFinal.compareTo(fecha) < 0) datosCUPS.fechaFinal = fecha;

							// actualiza lista de consumos mínimos
							if (datosCUPS.consumoMin.get(N - 1) > Float.valueOf(energia)) {
								datosCUPS.consumoMin.set(N - 1, Float.valueOf(energia));
								Collections.sort(datosCUPS.consumoMin);
							}

							// actualiza lista de consumos máximos
							if (datosCUPS.consumoMax.get(0) < Float.valueOf(energia)) {
								datosCUPS.consumoMax.set(0, Float.valueOf(energia));
								Collections.sort(datosCUPS.consumoMax);
							}

							// actualiza consumo en pico o valle
							@SuppressWarnings("deprecation")
							boolean isDST = TimeZone.getTimeZone("Europe/Madrid")
									.inDaylightTime(new Date(ano - 1900, mes - 1, dia));

							if (true == isDST) {
								// horario de verano
								if ((hora <= 13) || (hora > 23))
									datosCUPS.consumoValle += energia;
								else
									datosCUPS.consumoPico += energia;
							} else {
								// horario de invierno
								if ((hora <= 12) || (hora > 22))
									datosCUPS.consumoValle += energia;
								else
									datosCUPS.consumoPico += energia;
							}
							;
							datosCUPS.fechaFinal = fecha;

							// actuliza histograma
							int indice = 0;
							if (energia < 0) {
								indice = 0;
							} else if (energia == 0) {
								indice = 1;
							} else {
								indice = (int) (energia * 1000 / 115) + 2;
							}
							datosCUPS.histogramaConsumos[indice]++;

						}

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (br != null) {
							try {
								br.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

					request.getSession().setAttribute("datos", datosCUPS);
					request.getSession().setAttribute("line", lineNumber);

					grafikak = new CrearGrafica(datosCUPS);
					grafikak.crearMinimo();
					String chartFilenameMin = "images/minimo.jpg?dummy=" + Long.toString(Instant.now().toEpochMilli());
					grafikak.crearMaximo();
					String chartFilenameMax = "images/maximo.jpg?dummy=" + Long.toString(Instant.now().toEpochMilli());
					grafikak.crearHistograma();
					String chartFilenameHist = "images/histograma.jpg?dummy="
							+ Long.toString(Instant.now().toEpochMilli());
					grafikak.crearGrafica();
					String chartFilenameMens = "images/mensual.jpg?dummy=" + Long.toString(Instant.now().toEpochMilli());
					
						
					
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
							datosCUPS.codigoCUPS, datosCUPS.fechaInicial, datosCUPS.fechaFinal,
							Float.toString(datosCUPS.consumoPico), Float.toString(datosCUPS.consumoValle), lineNumber);

					// menu de opciones
					writer.print(
							"<div class=\"one\"><p><button class=\"button\" onclick=\"showImage()\">Minimo</button>");
					writer.print("<button class=\"button\" onclick=\"showMax()\">Maximo</button>");
					writer.print("<button class=\"button\" onclick=\"showHist()\">Histograma</button>");
					writer.print("<button class=\"button\" onclick=\"showMens()\">Mensuales</button>");
					writer.print("</p></div>");
					writer.print(
							"<div class=\"two\"><p><form id=\"formEgunak\" action=\"GraficasDia\" method=\"post\">");
					writer.print("<input type=\"text\" value=\"\" name=\"data\"/>\r\n");
					writer.print("<input type=\"submit\" value=\"Grafica de dia\"/>");
					writer.print("<h5>Introduce en formato aaaa-mm-dd</h5>");
					writer.print("</p></from></div>");

					// lista de consumos mensuales
					writer.print(
							"<div id=\"mensual\"><h3>CONSUMO HORARIO FACTURADO (kWh)</h3><h4>(consumo por factura)</h4><div id=\"max\"><img src=\""
									+ chartFilenameMens + "\" height=\"700\" width=\"1400\"></div></div>");

					// muestra consumos mínimos y máximos
					// lista de consumos mínimos
					writer.print(
							"<div id=\"minimos\"><h3>CONSUMOS MÍNIMOS HORARIOS (kWh)</h3><h4>(ordenados de menor a mayor)</h4>");
					writer.print("<div id=\"min\"><img src=\"" + chartFilenameMin
							+ "\" height=\"700\" width=\"700\"></div></div>");

					// lista de consumos máximos

					writer.print(
							"<div id=\"maximos\"><h3>CONSUMOS MÁXIMOS HORARIOS (kWh)</h3><h4>(ordenados de menor a mayor)</h4><div id=\"max\"><img src=\""
									+ chartFilenameMax + "\" height=\"700\" width=\"700\"></div></div>");

					// histograma de consumos

					writer.print(
							"<div id=\"histograma\"><h3>HISTOGRAMA DE CONSUMOS (115 W)</h3><h4>(según tramos de 115 Wh)</h4><div id=\"hist\"><img src=\""
									+ chartFilenameHist + "\" height=\"700\" width=\"700\"></div></div>");

					writer.print(
							"<div class=\"pie\"><strong><a class=\"atras\" href=\"upload.jsp\">Atras</a></strong></div>");
					writer.print("<script>function showImage(){\r\n"
							+ "	document.getElementById('minimos').style.display = 'block';\r\n"
							+ "	document.getElementById('maximos').style.display = 'none';\r\n"
							+ "	document.getElementById('histograma').style.display = 'none';"
							+ "	document.getElementById('mensual').style.display = 'none';" + "}</script>");
					writer.print("<script>function showMax(){\r\n"
							+ "	document.getElementById('minimos').style.display = 'none';\r\n"
							+ "	document.getElementById('maximos').style.display = 'block';\r\n"
							+ "	document.getElementById('histograma').style.display = 'none';"
							+ "	document.getElementById('mensual').style.display = 'none';" + "}</script>");
					writer.print("<script>function showHist(){\r\n"
							+ "	document.getElementById('minimos').style.display = 'none';\r\n"
							+ "	document.getElementById('maximos').style.display = 'none';\r\n"
							+ "	document.getElementById('histograma').style.display = 'block';"
							+ "	document.getElementById('mensual').style.display = 'none';" + "}</script>");
					writer.print("<script>function showMens(){\r\n"
							+ "	document.getElementById('minimos').style.display = 'none';\r\n"
							+ "	document.getElementById('maximos').style.display = 'none';\r\n"
							+ "	document.getElementById('histograma').style.display = 'none';"
							+ "	document.getElementById('mensual').style.display = 'block';" + "}</script>");
					writer.print("</body></html>");
					writer.flush();

					return;

				}
			}

			request.setAttribute("message", "Upload has been done successfully!");
		} catch (Exception ex) {
			request.setAttribute("message", "There was an error: " + ex.getMessage());
		}
		getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);

	}

}
