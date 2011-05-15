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

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("serial")
public class TwitterStarBusMetroGsonServlet extends HttpServlet {

	private static final GetTwitters GET_TWITTERS = new GetTwitters("starbusmetro");


	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/xml");
		resp.setCharacterEncoding("utf-8");
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		resp.getWriter().print(gson.toJson(GET_TWITTERS.getMessages()));
	}
}
