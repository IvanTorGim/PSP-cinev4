
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet implementation class ServletCine
 */
public class ServletCine extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Cine cine;

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
				sesion.removeAttribute("pelicula");
				sesion.removeAttribute("fase");
				response.sendRedirect("index.jsp");
				return;
			}
		}

		PrintWriter printWriter = response.getWriter();

		printWriter.println("<html><head><title>CINE v4.0</title></head><body>"
				+ "<h1 style=\"background:gray;color:white;text-align:center\">Proyecto CINE v4.0</h1>"
				+ "<div style=\"background:salmon;width:800px;height:600px;margin:auto;padding:20px\">");

		switch ((int) sesion.getAttribute("fase")) {
		case 1:
			System.out.println("[" + sesion.getId() + "] Fase 1");
			printWriter.println("<p style=\"background:gold;padding:20px;font-size:2rem;text-align:center\">" + "Fase "
					+ sesion.getAttribute("fase") + " de 6: Seleccion de pelicula</p>"
					+ "<p style=\"font-size:2rem;\">Selecciona la película</p>"
					+ "<form style=\"text-align:center\" action=\"ServletCine\" method=\"POST\">" + "<select style=\"width:80%\" name=\"pelicula\">");

			for (int i = 0; i < cine.getPelicules().size(); i++) {
				Pelicula pelicula = cine.getPelicules().get(i);
				printWriter.println(
						"<option value=\"" + i + "\">" + pelicula.getNomPeli() + " | " + pelicula.getDirector() + "</option>");
			}

			printWriter.println(
					"</select><br><br>" + "<button type=\"submit\" name=\"accion\" value=\"anterior\">Anterior</button>"
							+ "<button type=\"submit\" name=\"accion\" value=\"siguiente\">Siguiente</button><br>"
							+ "<button type=\"submit\" name=\"accion\" value=\"reiniciar\">Reiniciar sesión</button>"
							+ "</form>");
			break;
		case 2:
			System.out.println("[" + sesion.getId() + "] Fase 2");
			sesion.setAttribute("pelicula", request.getParameter("pelicula"));
			int numPelicula = Integer.parseInt((String) sesion.getAttribute("pelicula"));
			Pelicula pelicula = cine.getPelicules().get(numPelicula);
			System.out.println("[" + sesion.getId() + "] Elige la pelicula " + pelicula.getNomPeli());
			printWriter.println("<p style=\"background:gold;padding:20px;font-size:2rem;text-align:center\">" + "Fase "
					+ sesion.getAttribute("fase") + " de 6: Seleccion de sesion</p>"
					+ "<p style=\"font-size:2rem;\">Selecciona la sesion</p>"
					+ "<form style=\"text-align:center\" action=\"ServletCine\" method=\"POST\">" + "<select style=\"width:80%\" name=\"sesion\">");

			for (int i = 0; i < pelicula.getSessionsPeli().size(); i++) {
				Sessio sesionesPeli = pelicula.getSessionsPeli().get(i);
				printWriter.println("<option value=\"" + i + "\">" + sesionesPeli.getNomSessio() + " | " + sesionesPeli.mostraDataFormatada()
						+ " | " + sesionesPeli.getPreu() + "€</option>");
			}

			printWriter.println(
					"</select><br><br>" + "<button type=\"submit\" name=\"accion\" value=\"anterior\">Anterior</button>"
							+ "<button type=\"submit\" name=\"accion\" value=\"siguiente\">Siguiente</button><br>"
							+ "<button type=\"submit\" name=\"accion\" value=\"reiniciar\">Reiniciar sesión</button>"
							+ "</form>");
			break;
		default:
			sesion.removeAttribute("fase");
			sesion.removeAttribute("pelicula");
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
