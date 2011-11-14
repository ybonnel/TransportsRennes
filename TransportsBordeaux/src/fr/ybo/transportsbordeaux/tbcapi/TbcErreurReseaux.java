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
package fr.ybo.transportsbordeaux.tbcapi;

public class TbcErreurReseaux extends Exception {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    public TbcErreurReseaux() {
        super();
    }

    public TbcErreurReseaux(String pArg0, Throwable pArg1) {
        super(pArg0, pArg1);
    }

    public TbcErreurReseaux(String pArg0) {
        super(pArg0);
    }

    public TbcErreurReseaux(Throwable pArg0) {
        super(pArg0);
    }


}
