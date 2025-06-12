package oct.soft.book;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oct.soft.common.PageResponse;
import oct.soft.exception.OperationNotPermittedException;
import oct.soft.history.BookTransactionHistory;
import oct.soft.history.BookTransactionHistoryRepository;
import oct.soft.user.User;

@Service
@RequiredArgsConstructor
public class BookService {
	private final BookRepository bookRepository;
	private final BookMapper bookMapper;
	private final BookTransactionHistoryRepository historyRepo;

	public Long save(@Valid BookRequest request, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Book book = bookMapper.toBook(request);
		book.setOwner(user);

		return bookRepository.save(book).getId();
	}

	public BookResponse findById(Long bookId) {

		return bookRepository.findById(bookId).map(bookMapper::toBookResponse)
				.orElseThrow(() -> new EntityNotFoundException("No book found with the ID:: " + bookId));
	}

	public PageResponse<BookResponse> findAll(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
		List<BookResponse> bookRepsonse = books.stream().map(bookMapper::toBookResponse).toList();
		return new PageResponse<>(bookRepsonse, books.getNumber(), books.getSize(), books.getTotalElements(),
				books.getTotalPages(), books.isFirst(), books.isLast());
	}

	public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

		Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
		List<BookResponse> bookRepsonse = books.stream().map(bookMapper::toBookResponse).toList();

		return new PageResponse<>(bookRepsonse, books.getNumber(), books.getSize(), books.getTotalElements(),
				books.getTotalPages(), books.isFirst(), books.isLast());
	}

	public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

		Page<BookTransactionHistory> allBorrowedBooks = historyRepo.findAllBorrowedBooks(pageable, user.getId());
		List<BorrowedBookResponse> bookRepsonse = allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse)
				.toList();

		return new PageResponse<>(bookRepsonse, allBorrowedBooks.getNumber(), allBorrowedBooks.getSize(),
				allBorrowedBooks.getTotalElements(), allBorrowedBooks.getTotalPages(), allBorrowedBooks.isFirst(),
				allBorrowedBooks.isLast());
	}

	public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

		Page<BookTransactionHistory> allReturnedBooks = historyRepo.findAllReturnedBooks(pageable, user.getId());
		List<BorrowedBookResponse> bookRepsonse = allReturnedBooks.stream().map(bookMapper::toBorrowedBookResponse)
				.toList();

		return new PageResponse<>(bookRepsonse, allReturnedBooks.getNumber(), allReturnedBooks.getSize(),
				allReturnedBooks.getTotalElements(), allReturnedBooks.getTotalPages(), allReturnedBooks.isFirst(),
				allReturnedBooks.isLast());
	}

	public Long updateShareableStatus(Long bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with id:: " + bookId));
		User user = ((User) connectedUser.getPrincipal());
		if (!Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot update others book shareable status");
		}
		book.setShareable(!book.isShareable());
		bookRepository.save(book);
		return bookId;
	}

	public Long updateArchivedStatus(Long bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with id:: " + bookId));
		User user = ((User) connectedUser.getPrincipal());
		if (!Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot update other book archived status");
		}
		book.setShareable(!book.isArchived());
		bookRepository.save(book);
		return bookId;
	}

	public Long borrowBook(Long bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with id:: " + bookId));
		
		if(book.isArchived() || !book.isShareable())
		{
			throw new OperationNotPermittedException("Book cannot be borrowd since it is archived or not shareable");
		}
		
		User user = ((User) connectedUser.getPrincipal());
		
		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot borrow your own book");
		}
		
		final boolean isAlreadyBorrowed = historyRepo.isAlreadyBorrowed(bookId, user.getId());
		if(isAlreadyBorrowed)
		{
			throw new OperationNotPermittedException("The requested book is alreadt borrowed");
		}
		
		return bookId;
	}

}
