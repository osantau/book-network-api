package oct.soft.book;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oct.soft.book.history.BookTransactionHistory;
import oct.soft.book.history.BookTransactionHistoryRepository;
import oct.soft.common.PageResponse;
import oct.soft.user.User;

@Service
@RequiredArgsConstructor
public class BookService {
	private final BookRepository bookRepository;
	private final BookMapper bookMapper;
	private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

	public Long save(@Valid BookRequest request, Authentication connectedUser) {

		User user = (User) connectedUser.getPrincipal();
		Book book = bookMapper.toBook(request);
		book.setOwner(user);
		return bookRepository.save(book).getId();
	}

	public BookResponse findById(Long id) {
		return bookRepository.findById(id).map(bookMapper::toBookResponse)
				.orElseThrow(() -> new EntityNotFoundException("No book found with the ID::" + id));
	}

	public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
		User user = (User) connectedUser.getPrincipal();
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
		List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();

		return new PageResponse<BookResponse>(bookResponses, books.getNumber(), books.getSize(),
				books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());
	}

	public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
		User user = (User) connectedUser.getPrincipal();
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
		List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();

		return new PageResponse<BookResponse>(bookResponses, books.getNumber(), books.getSize(),
				books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());
	}

	public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
		User user = (User) connectedUser.getPrincipal();
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable,
				user.getId());
		List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse)
				.toList();

		return new PageResponse<BorrowedBookResponse>(bookResponse, allBorrowedBooks.getNumber(),
				allBorrowedBooks.getSize(), allBorrowedBooks.getTotalElements(), allBorrowedBooks.getTotalPages(),
				allBorrowedBooks.isFirst(), allBorrowedBooks.isLast());
	}
	
	public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
		User user = (User) connectedUser.getPrincipal();
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable,
				user.getId());
		List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse)
				.toList();

		return new PageResponse<BorrowedBookResponse>(bookResponse, allBorrowedBooks.getNumber(),
				allBorrowedBooks.getSize(), allBorrowedBooks.getTotalElements(), allBorrowedBooks.getTotalPages(),
				allBorrowedBooks.isFirst(), allBorrowedBooks.isLast());
	}

}
