package fr.upem.rmirest.bilmancamp.models;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import fr.upem.rmirest.bilmancamp.interfaces.Book;
import fr.upem.rmirest.bilmancamp.interfaces.BookComment;
import fr.upem.rmirest.bilmancamp.interfaces.Image;

public class BookImpl implements Book {

	private final BookPOJO model;

	public BookImpl(BookPOJO model) {

		this.model = Objects.requireNonNull(model);
	}
	
	// Getters
	@Override
	public int getId() throws RemoteException {
		return model.getId();
	}

	@Override
	public LocalDate getDate() throws RemoteException {
		return model.getDate();
	}

	@Override
	public String getTitle() throws RemoteException {
		return model.getTitle();
	}

	@Override
	public List<String> getAuthors() throws RemoteException {
		return Collections.unmodifiableList(model.getAuthors());
	}

	@Override
	public String getSummary() throws RemoteException {
		return model.getSummary();
	}

	@Override
	public List<String> getCategories() throws RemoteException {
		return Collections.unmodifiableList(model.getCategories());
	}

	@Override
	public int getConsultationNumber() throws RemoteException {
		return model.getConsultationNumber();
	}

	@Override
	public List<String> getTags() throws RemoteException {
		return Collections.unmodifiableList(model.getTags());
	}

	@Override
	public Image getMainImage() throws RemoteException {
		return model.getMainImage();
	}

	@Override
	public List<Image> getSecondaryImages() throws RemoteException {
		return Collections.unmodifiableList(model.getSecondaryImages());
	}

	@Override
	public double getPrice() throws RemoteException {
		return model.getPrice();
	}

	@Override
	public float getRate() throws RemoteException {
		if (model.getRateNumber() == 0) {
			return 0;
		}
		return model.getTotalRate() / model.getRateNumber();
	}

	@Override
	public int getRateNumber() throws RemoteException {
		return model.getRateNumber();
	}

	@Override
	public List<BookComment> getComments() throws RemoteException {
		return Collections.unmodifiableList(model.getComments());
	}

	@Override
	public void comment(BookComment bookComment) throws RemoteException {
		model.getComments().add(Objects.requireNonNull(bookComment));
	}

	@Override
	public String toString() {
		return String.format("%s by %s", model.getTitle(), model.getAuthors());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((model.getAuthors() == null) ? 0 : model.getAuthors().hashCode());
		result = prime * result + ((model.getCategories() == null) ? 0 : model.getCategories().hashCode());
		long temp;
		temp = Double.doubleToLongBits(model.getPrice());
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((model.getSummary() == null) ? 0 : model.getSummary().hashCode());
		result = prime * result + ((model.getTags() == null) ? 0 : model.getTags().hashCode());
		result = prime * result + ((model.getTitle() == null) ? 0 : model.getTitle().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BookImpl)) {
			return false;
		}

		BookImpl other = (BookImpl) obj;

		return model.getId() == other.model.getId();
	}

	public BookPOJO getModel() {
		return model;
	}

}
