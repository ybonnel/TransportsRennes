package fr.ybo.transportsrennes.keolis.modele;

import java.util.ArrayList;
import java.util.List;

/**
 * Réponse Kéolis.
 *
 * @param <ObjectKeolis> type d'objet kéolis.
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
	public List<ObjectKeolis> getData() {
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
	public StatusKeolis getStatus() {
		return this.status;
	}

	/**
	 * Setter.
	 *
	 * @param pStatus le status.
	 */
	public void setStatus(StatusKeolis pStatus) {
		this.status = pStatus;
	}
}
