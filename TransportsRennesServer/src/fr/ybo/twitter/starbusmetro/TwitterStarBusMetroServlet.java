package fr.ybo.twitter.starbusmetro;
import java.io.IOException;
import javax.servlet.http.*;

import fr.ybo.twitter.starbusmetro.modele.MessageTwitter;

@SuppressWarnings("serial")
public class TwitterStarBusMetroServlet extends HttpServlet {
	
	private static GetTwitters getTwitters = new GetTwitters();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/xml");
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		resp.getWriter().println("<messages>");
		for (MessageTwitter message : getTwitters.getMessages()) {
			resp.getWriter().println(message.toXml());
		}
		resp.getWriter().println("</messages>");
	}
}
