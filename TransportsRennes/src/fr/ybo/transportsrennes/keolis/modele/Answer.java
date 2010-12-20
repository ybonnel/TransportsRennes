package fr.ybo.transportsrennes.keolis.modele;

import java.util.ArrayList;
import java.util.List;

/**
 * R�ponse K�olis.
 *
 * @param <ObjectKeolis> type d'objet k�olis.
 * @author ybonnel
 */
public class Answer<ObjectKeolis> {

	/**
	 * Status.
	 */
	private StatusKeolis status;
	/**
	 * Liste d'objet Keolis.
	 */
	private List<ObjectKeolis> data;

	/**
	 * @return les liste d'objet Keolis.
	 */
	public final List<ObjectKeolis> getData() {
		if (this.data == null) {
			this.data = new ArrayList<ObjectKeolis>();
		}
		return this.data;
	}

	/**
	 * Getter.
	 *
	 * @return le status.
	 */
	public final StatusKeolis getStatus() {
		return this.status;
	}

	/**
	 * Setter.
	 *
	 * @param pStatus le status.
	 */
	public final void setStatus(final StatusKeolis pStatus) {
		this.status = pStatus;
	}
}
