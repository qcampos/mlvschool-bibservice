package application.model;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Objects;

import fr.upem.rmirest.bilmancamp.interfaces.Book;
import fr.upem.rmirest.bilmancamp.interfaces.Library;
import fr.upem.rmirest.bilmancamp.interfaces.User;

/**
 * Wrapper of a remote User (RMI). Allows the caching of static value like
 * getId(), getFirst/LastName() and so one to prevent multiple requests &
 * simplifying the GUI part.
 * 
 * @author Jefferson
 *
 */
public class UserAsynchrone {

	private final User remoteUser;
	private final String firstName;
	private final String lastName;
	private final String status;
	private final int id;
	private final int nbBooks;
	private final List<BookAsynchrone> books;

	private UserAsynchrone(User remoteUser, String firstName, String lastName, String status,
			List<BookAsynchrone> books, int id, int nbBooks) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.status = status;
		this.books = books;
		this.id = id;
		this.nbBooks = nbBooks;
		this.remoteUser = Objects.requireNonNull(remoteUser);
	}

	/**
	 * Creates an instance of {@link UserAsynchrone} by calling a multitude of
	 * Remote methods. Has to be called in the thread reserved for RMI
	 * transfers.
	 * 
	 * @return The {@link UserAsynchrone} created.
	 * @throws RemoteException
	 */
	static UserAsynchrone createUserAsynchrone(Library library, User remoteUser, List<Book> bookHistory)
			throws RemoteException {
		String firstName = remoteUser.getFirstName();
		String lastName = remoteUser.getLastName();
		String status = remoteUser.getStatus();
		int id = remoteUser.getId();
		List<BookAsynchrone> books = BookAsynchrone.convertToBooksAsynchrone(library.getBooks(remoteUser));
		int nbBooks = books.size();
		return new UserAsynchrone(remoteUser, firstName, lastName, status, books, id, nbBooks);
	}

	// Default and not public !
	User getUser() {
		return remoteUser;
	}

	public List<BookAsynchrone> getBooks() {
		return books;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getStatus() {
		return status;
	}

	public int getId() {
		return id;
	}

	public int getNbBooks() {
		return nbBooks;
	}
}
