
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Servlet implementation class ServletCine
 */
public class ServletCine extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Cine cine;
	private Pelicula pelicula;
	private Sala sala;
	private Sessio sessio;
	ArrayList<Seient> asientosElegidos;
	private int numEntradas;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServletCine() {
		super();
		cine = new Cine("Kinepolis");
		cine.carregaDadesInicials(cine);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		HttpSession sesion = request.getSession();

		if (sesion.getAttribute("fase") == null) {
			sesion.setAttribute("fase", 1);
			System.out.println("[" + sesion.getId() + "] Inicia la compra");
		} else {
			switch (request.getParameter("accion").toString()) {
			case "anterior":
				System.out.println("[" + sesion.getId() + "] Pulsa anterior");
				sesion.setAttribute("fase", (int) sesion.getAttribute("fase") - 1);
				break;
			case "siguiente":
				System.out.println("[" + sesion.getId() + "] Pulsa siguiente");
				sesion.setAttribute("fase", (int) sesion.getAttribute("fase") + 1);
				break;
			case "reiniciar":
				System.out.println("[" + sesion.getId() + "] Pulsa reiniciar");
				sesion.invalidate();
				response.sendRedirect("index.jsp");
				return;
			}
		}

		PrintWriter printWriter = response.getWriter();

		printWriter.println(
				"<html><head><title>CINE v4.0</title></head><link rel=\"stylesheet\" href=\"styles.css\"><body>"
						+ "<h1>Proyecto CINE v4.0</h1>" + "<div class=\"contenedor\">");

		switch ((int) sesion.getAttribute("fase")) {
		case 1:
			if (sesion.getAttribute("pelicula") != null) {
				sesion.removeAttribute("pelicula");
			}
			System.out.println("[" + sesion.getId() + "] Fase 1");
			printWriter.println("<p class=\"fase\">" + "Fase " + sesion.getAttribute("fase")
					+ " de 6: Seleccion de pelicula</p>" + "<p class=\"seleccion\">Selecciona la película</p>"
					+ "<form action=\"ServletCine\" method=\"POST\">" + "<select name=\"pelicula\">");

			for (int i = 0; i < cine.getPelicules().size(); i++) {
				Pelicula pelicula = cine.getPelicules().get(i);
				printWriter.println("<option value=\"" + i + "\">" + pelicula.getNomPeli() + " | "
						+ pelicula.getDirector() + "</option>");
			}

			printWriter.println(
					"</select><br><br>" + "<button type=\"submit\" name=\"accion\" value=\"anterior\">Anterior</button>"
							+ "<button type=\"submit\" name=\"accion\" value=\"siguiente\">Siguiente</button><br>"
							+ "<button type=\"submit\" name=\"accion\" value=\"reiniciar\">Reiniciar sesión</button>"
							+ "</form>");
			break;
		case 2:
			if (sesion.getAttribute("sesion") != null) {
				sesion.removeAttribute("sesion");
			}
			System.out.println("[" + sesion.getId() + "] Fase 2");
			if (sesion.getAttribute("pelicula") == null) {
				sesion.setAttribute("pelicula", request.getParameter("pelicula"));
			}

			pelicula = cine.getPelicules().get(Integer.parseInt((String) sesion.getAttribute("pelicula")));

			System.out.println("[" + sesion.getId() + "] Elige la pelicula " + pelicula.getNomPeli());
			printWriter.println("<p class=\"fase\">" + "Fase " + sesion.getAttribute("fase")
					+ " de 6: Seleccion de sesion</p>" + "<p class=\"seleccion\">Selecciona la sesion</p>"
					+ "<form action=\"ServletCine\" method=\"POST\">" + "<select name=\"sesion\">");

			for (int i = 0; i < pelicula.getSessionsPeli().size(); i++) {
				Sessio sesionesPeli = pelicula.getSessionsPeli().get(i);
				printWriter.println("<option value=\"" + i + "\">" + sesionesPeli.getNomSessio() + " | "
						+ sesionesPeli.mostraDataFormatada() + " | " + sesionesPeli.getPreu() + "€</option>");
			}

			printWriter.println(
					"</select><br><br>" + "<button type=\"submit\" name=\"accion\" value=\"anterior\">Anterior</button>"
							+ "<button type=\"submit\" name=\"accion\" value=\"siguiente\">Siguiente</button><br>"
							+ "<button type=\"submit\" name=\"accion\" value=\"reiniciar\">Reiniciar sesión</button>"
							+ "</form>");
			break;
		case 3:
			if (sesion.getAttribute("entradas") != null) {
				sesion.removeAttribute("entradas");
			}
			if (sesion.getAttribute("sesion") == null) {
				sesion.setAttribute("sesion", request.getParameter("sesion"));
			}
			System.out.println("[" + sesion.getId() + "] Fase 3");

			sessio = pelicula.retornaSessioPeli(Integer.parseInt((String) sesion.getAttribute("sesion")) + 1);
			sala = sessio.getSala();

			System.out.println("[" + sesion.getId() + "] Elige la sesion " + sessio.getNomSessio());
			printWriter.println("<p class=\"fase\">" + "Fase " + sesion.getAttribute("fase")
					+ " de 6: Seleccion de numero de entradas</p>"
					+ "<p class=\"seleccion\">Selecciona numero de entradas</p>" + "<div class=\"sala\">");
			for (int i = 0; i < sessio.getSeients().length; i++) {
				printWriter.println("<div class=\"fila\">");
				for (int j = 0; j < sessio.getSeients()[0].length; j++) {
					Seient seient = sessio.getSeients()[i][j];
					if (seient.getDisponibilitat() == Seient.Estat.OCUPAT) {
						printWriter.println("<div class=\"butaca-ocupada\">" + i + "-" + j + "</div>");
					} else {
						printWriter.println("<div class=\"butaca-libre\">" + i + "-" + j + "</div>");
					}

				}
				printWriter.println("</div>");
			}

			// Mostramos el número de entradas del selector
			printWriter.println("</div><form action=\"ServletCine\" method=\"POST\">" + "<select name=\"entradas\">");
			int contador = 1;
			for (int i = 0; i < sessio.getSeients().length; i++) {
				for (int j = 0; j < sessio.getSeients()[0].length; j++) {
					printWriter.println("<option value=\"" + contador + "\">" + contador + "</option>");
					contador++;
				}
			}

			printWriter.println(
					"</select><br><br>" + "<button type=\"submit\" name=\"accion\" value=\"anterior\">Anterior</button>"
							+ "<button type=\"submit\" name=\"accion\" value=\"siguiente\">Siguiente</button><br>"
							+ "<button type=\"submit\" name=\"accion\" value=\"reiniciar\">Reiniciar sesión</button>"
							+ "</form>");
			break;
		case 4:
			System.out.println("[" + sesion.getId() + "] Fase 4");
			if (sesion.getAttribute("asientos") != null) {
				sesion.removeAttribute("asientos");
			}
			if (sesion.getAttribute("entradas") == null) {
				sesion.setAttribute("entradas", request.getParameter("entradas"));
				System.out.println(
						"[" + sesion.getId() + "] Numero de entradas elegidas: " + sesion.getAttribute("entradas"));
			}

			numEntradas = Integer.parseInt((String) sesion.getAttribute("entradas"));

			printWriter.println("<p class=\"fase\">" + "Fase " + sesion.getAttribute("fase")
					+ " de 6: Seleccion de numero de entradas</p>"
					+ "<p class=\"seleccion\">Selecciona numero de entradas</p>" + "<div class=\"sala\">");
			for (int i = 0; i < sessio.getSeients().length; i++) {
				printWriter.println("<div class=\"fila\">");
				for (int j = 0; j < sessio.getSeients()[0].length; j++) {
					Seient seient = sessio.getSeients()[i][j];
					if (seient.getDisponibilitat() == Seient.Estat.OCUPAT) {
						printWriter.println("<div class=\"butaca-ocupada\">" + i + "-" + j + "</div>");
					} else {
						printWriter.println("<div class=\"butaca-libre\">" + i + "-" + j + "</div>");
					}

				}
				printWriter.println("</div>");
			}

			// Mostramos los asientos a comprar
			printWriter.println("</div><form action=\"ServletCine\" method=\"POST\">");

			for (int i = 0; i < numEntradas; i++) {
				printWriter.println("<p>Elige asiento " + (i + 1) + "</p>");
				printWriter.println("<div>Fila: <select name=\"asiento" + i + "-fila\">");
				for (int j = 0; j < sessio.getSeients().length; j++) {
					printWriter.println("<option value=\"" + j + "\">" + j + "</option>");
				}
				printWriter.println("</select> Columna: <select name=\"asiento" + i + "-columna\">");
				for (int j = 0; j < sessio.getSeients()[0].length; j++) {
					printWriter.println("<option value=\"" + j + "\">" + j + "</option>");
				}
				printWriter.println("</select></div>");
			}

			printWriter
					.println("<br><br>" + "<button type=\"submit\" name=\"accion\" value=\"anterior\">Anterior</button>"
							+ "<button type=\"submit\" name=\"accion\" value=\"siguiente\">Siguiente</button><br>"
							+ "<button type=\"submit\" name=\"accion\" value=\"reiniciar\">Reiniciar sesión</button>"
							+ "</form>");
			break;
		case 5:
			System.out.println("[" + sesion.getId() + "] Fase 5");
			if (sesion.getAttribute("pago") != null) {
				sesion.removeAttribute("pago");
			}

			asientosElegidos = new ArrayList<>();

			printWriter.println("<p class=\"fase\">" + "Fase " + sesion.getAttribute("fase")
					+ " de 6: Pago de entradas</p>" + "<p class=\"seleccion\">Pago de entradas</p>" + "<div>");

			if (sesion.getAttribute("asientos") == null) {
				numEntradas = Integer.parseInt((String) sesion.getAttribute("entradas"));
				for (int i = 0; i < numEntradas; i++) {
					int fila = Integer.parseInt(request.getParameter("asiento" + i + "-fila"));
					int columna = Integer.parseInt(request.getParameter("asiento" + i + "-columna"));
					Seient asiento = new Seient(fila, columna);
					if (asientosElegidos.contains(asiento)
							|| sessio.getSeients()[fila][columna].getDisponibilitat() == Seient.Estat.OCUPAT) {
						printWriter.println("<p class=\"butaca-ocupada\">Butaca ocupada : " + asiento.getFilaSeient()
								+ "-" + asiento.getNumeroSeient() + "</p>");
					} else {
						printWriter.println("<p class=\"butaca-libre\">Butaca libre: " + asiento.getFilaSeient() + "-"
								+ asiento.getNumeroSeient() + "</p>");
						asientosElegidos.add(asiento);
					}
				}
				sesion.setAttribute("asientos", asientosElegidos);
				System.out.println("[" + sesion.getId() + "] Elige las entradas: " + asientosElegidos);
			}

			// Si no se han añadido todas las entradas es que hay repetidas no dejamos
			// continuar
			if (numEntradas != asientosElegidos.size()) {
				printWriter.println("</div><form action=\"ServletCine\" method=\"POST\">" + "<br><br>"
						+ "<button type=\"submit\" name=\"accion\" value=\"anterior\">Anterior</button>"
						+ "<button type=\"submit\" name=\"accion\" value=\"reiniciar\">Reiniciar sesión</button>"
						+ "</form>");
			} else {
				printWriter.println("</div><p>Total a pagar " + new BigDecimal(numEntradas).multiply(sessio.getPreu())
						+ "</p><p>¿Se paga el importe?</p>" + "<form action=\"ServletCine\" method=\"POST\">"
						+ "<br><br>" + "<select name=\"pago\"><option value=\"si\">Si</option>"
						+ "<option value=\"no\">No</option></select><br><br>"
						+ "<button type=\"submit\" name=\"accion\" value=\"anterior\">Anterior</button>"
						+ "<button type=\"submit\" name=\"accion\" value=\"siguiente\">Siguiente</button><br>"
						+ "<button type=\"submit\" name=\"accion\" value=\"reiniciar\">Reiniciar sesión</button>"
						+ "</form>");
			}
			break;
		case 6:
			System.out.println("[" + sesion.getId() + "] Fase 6");
			if (sesion.getAttribute("pago") == null) {
				sesion.setAttribute("pago", request.getParameter("pago"));
				System.out.println("[" + sesion.getId() + "] Eleccion de pago: " + sesion.getAttribute("pago"));
			}

			printWriter.println("<p class=\"fase\">" + "Fase " + sesion.getAttribute("fase")
					+ " de 6: Confirmacion de pago</p>" + "<p class=\"seleccion\">Confirmacion de pago</p>" + "<div>");

			if (sesion.getAttribute("pago").equals("si")) {
				synchronized (cine) {
					if (cine.pagamentEntradesFil(asientosElegidos, sessio)) {

						cine.ocupaSeients(asientosElegidos, sessio);
						printWriter.println("<div class=\"sala\">");
						for (int i = 0; i < sessio.getSeients().length; i++) {
							printWriter.println("<div class=\"fila\">");
							for (int j = 0; j < sessio.getSeients()[0].length; j++) {
								Seient seient = sessio.getSeients()[i][j];
								if (seient.getDisponibilitat() == Seient.Estat.OCUPAT) {
									printWriter.println("<div class=\"butaca-ocupada\">" + i + "-" + j + "</div>");
								} else {
									printWriter.println("<div class=\"butaca-libre\">" + i + "-" + j + "</div>");
								}

							}
							printWriter.println("</div>");
						}
						printWriter.println("</div>");

						for (int i = 0; i < asientosElegidos.size(); i++) {

							printWriter.println("<div class=\"ticket\">"
									+ sessio.imprimirTicket(asientosElegidos.get(i), sessio, sala, pelicula)
									+ "</div>");
						}
					} else {
						printWriter.println("<p> Pago NO aceptado, se liberan los asientos</p>");
					}
				}
			} else {
				printWriter.println("<p> Pago NO aceptado, se liberan los asientos</p>");
			}
			printWriter.println("</div><form action=\"ServletCine\" method=\"POST\">" + "<br><br>"
					+ "<button type=\"submit\" name=\"accion\" value=\"reiniciar\">Reiniciar sesión</button>"
					+ "</form>");
			break;
		default:
			sesion.invalidate();
			response.sendRedirect("index.jsp");
			return;
		}
		printWriter.println("</div></body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
