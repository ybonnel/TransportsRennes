/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.ybo.twitter.starbusmetro;

import fr.ybo.twitter.starbusmetro.modele.MessageTwitter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
public class TwitterStarBusMetroServlet extends HttpServlet {

	private static final GetTwitters GET_TWITTERS = new GetTwitters();

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		resp.setContentType("text/xml");
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		resp.getWriter().println("<messages>");
		for (final MessageTwitter message : GET_TWITTERS.getMessages()) {
			resp.getWriter().println(message.toXml());
		}
		resp.getWriter().println("</messages>");
	}
}
