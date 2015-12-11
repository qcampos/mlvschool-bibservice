package fr.upem.rmirest.bilmancamp.models;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.upem.rmirest.bilmancamp.database.Database;
import fr.upem.rmirest.bilmancamp.interfaces.Book;
import fr.upem.rmirest.bilmancamp.interfaces.Image;
import fr.upem.rmirest.bilmancamp.interfaces.Library;
import fr.upem.rmirest.bilmancamp.interfaces.MailBox;
import fr.upem.rmirest.bilmancamp.interfaces.User;
import utils.LibraryImplDataLoader;
import utils.Mapper;

public class LibraryImpl extends UnicastRemoteObject implements Library {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	// Library values
	private final Database database;
	private final Map<User, MailBox<Book>> addresses;
	private final Map<String, String> categories;
	private final List<String> status;

	public LibraryImpl(Database database) throws RemoteException {
		super(0);
		this.database = database;
		addresses = new ConcurrentHashMap<>();
		categories = new HashMap<>();
		status = new ArrayList<>();
	}

	public static Library createLibraryImpl(Database database, String configPath)
			throws JsonParseException, JsonMappingException, IOException {
		LibraryImpl lib = new LibraryImpl(database);
		// Initialize the loader and load data into the fields.
		LibraryImplDataLoader loader = LibraryImplDataLoader.createLoader(configPath);
		lib.categories.putAll(loader.getCategories());
		lib.status.addAll(loader.getStatus());
		// Return the fully initialized library.
		return lib;
	}

	@Override
	public void addBook(String title, List<String> authors, String summary, Image mainImage,
			List<Image> secondaryImages, List<String> categories, double price, List<String> tags)
					throws IllegalArgumentException, RemoteException {

		database.addBook(new BookPOJO(0, title, authors, summary, categories, price, tags, mainImage, secondaryImages));
	}

	@Override
	public boolean addUser(String status, String firstName, String lastName, int cardNumber, String password)
			throws IllegalArgumentException, RemoteException {

		return database.addUser(new UserPOJO(0, status, firstName, lastName, password, cardNumber, new ArrayList<>()),
				password);
	}

	@Override
	public User connect(String id, String password) throws IllegalArgumentException, RemoteException {
		UserPOJO model = database.connectUser(id, password);
		if (model == null) {
			throw new IllegalArgumentException("Invalid authentification for user " + id);
		}
		return new UserImpl(model);
	}

	@Override
	public List<Book> searchBooks(String... keywords) throws RemoteException {
		return Pojos.booksPojoToBooksRemote(database.searchBookFromKeywords(keywords));
	}

	@Override
	public List<String> getCategories() throws RemoteException {
		return Collections.unmodifiableList(database.getCategories());
	}

	@Override
	public String getCategoryDescription(String category) {
		return categories.get(category);
	}

	@Override
	public List<String> getStatus() throws RemoteException {
		return Collections.unmodifiableList(status);
	}

	@Override
	public List<Book> getCategoryBooks(String category) throws RemoteException {
		List<BookPOJO> books = database.getBookFromCategory(category);
		return Pojos.booksPojoToBooksRemote(books);
	}

	@Override
	public List<Book> getMoreRecentBooks(int number) throws RemoteException {
		return Pojos.booksPojoToBooksRemote(database.getBookRecents(number));
	}

	@Override
	public List<Book> getBestRatedBooks(int number) throws RemoteException {
		return Pojos.booksPojoToBooksRemote(database.getBookBestRate(number));
	}

	@Override
	public List<Book> getMostConsultedBooks(int number) throws RemoteException {
		return Pojos.booksPojoToBooksRemote(database.getBookMostConsulted(number));
	}

	@Override
	public List<Book> getMostSimilarsBooks(Book book, int number) throws RemoteException {

		return Pojos.booksPojoToBooksRemote(database.getBookMostSimilar(Mapper.createBookPOJO(book), number));
	}

	/* Actions on the content */

	@Override
	public boolean borrow(Book book, User user) throws RemoteException {
		return database.borrow(Mapper.createBookPOJO(book), Mapper.createUserPOJO(user));
	}

	@Override
	public boolean rateBook(Book book, User user, int value) throws RemoteException {
		return database.rateBook(Mapper.createBookPOJO(book), Mapper.createUserPOJO(user), value);
	}

	@Override
	public User connect(String id, String password, MailBox<Book> callback) throws RemoteException {

		Objects.requireNonNull(callback);

		// database authentication ?
		UserPOJO uPojo = database.connectUser(id, password);

		// authentication OK
		if (uPojo != null) {

			User user = new UserImpl(uPojo);
			addresses.put(user, callback);

			// Notification
			List<BookPOJO> books = database.hasBookInWait(uPojo);

			// For each available book
			for (BookPOJO b : books) {
				contactIfOnline(uPojo, new BookImpl(b), callback);
			}

			return user;
		}

		return null;
	}

	@Override
	public boolean giveBack(Book book, User user) throws RemoteException {

		BookPOJO bPojo = Mapper.createBookPOJO(book);
		UserPOJO uPojo = Mapper.createUserPOJO(user);

		if (database.giveBack(bPojo, uPojo)) {

			List<UserPOJO> queue = database.getQueue(bPojo, 1);

			if (!queue.isEmpty()) {
				UserPOJO head = queue.get(0);
				contactIfOnline(head, book, addresses.get(new UserImpl(head)));
			}

			return true;
		}

		return false;
	}

	@Override
	public List<Book> getBookHistory(User user) throws RemoteException {
		return Pojos.booksPojoToBooksRemote(database.getBorrowedBooks(Mapper.createUserPOJO(user)));
	}

	@Override
	public void disconnect(User user) throws RemoteException {
		addresses.remove(user);
		user.disconnect();
	}

	/**
	 * Handle {@link Book} availability of a book by notifying user
	 * 
	 * @param uPojo
	 *            The {@link UserPOJO} to notify
	 * @param book
	 *            The {@link Book} to borrow
	 * @throws RemoteException
	 */
	private void contactIfOnline(UserPOJO uPojo, Book book, MailBox<Book> callbackAddr) throws RemoteException {

		if (callbackAddr != null) {

			BookPOJO bPojo = Mapper.createBookPOJO(book);
			callbackAddr.receive(book);
			database.borrow(bPojo, uPojo);
			database.removeFromQueue(bPojo, uPojo);
		}
	}
}
