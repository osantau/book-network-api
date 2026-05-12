package oct.soft.book;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oct.soft.book.history.BookTransactionHistory;
import oct.soft.book.history.BookTransactionHistoryRepository;
import oct.soft.common.PageResponse;
import oct.soft.exception.OperationNotPermittedException;
import oct.soft.file.FileStorageService;
import oct.soft.user.User;

@Service
@RequiredArgsConstructor
public class BookService {
	private final BookRepository bookRepository;
	private final BookMapper bookMapper;
	private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;
    
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

	public Long udpateShareableStatus(Long bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("No book found with ID::  "+bookId));
		User user = (User) connectedUser.getPrincipal();
		if(!Objects.equals(user.getId(), book.getOwner().getId()))
		{
			throw new OperationNotPermittedException("You cannot update books shareable status !");
		}
		book.setShareable(!book.isShareable());
		bookRepository.save(book);
		return bookId;
	}

	public Long udpateArchivedStatus(Long bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("No book found with ID::  "+bookId));
		User user = (User) connectedUser.getPrincipal();
		if(!Objects.equals(user.getId(), book.getOwner().getId()))
		{
			throw new OperationNotPermittedException("You cannot update books archived status !");
		}
		book.setArchived(!book.isArchived());
		bookRepository.save(book);
		return bookId;
	}

	public Long borrowBook(Long bookId, Authentication connectedUser) {
		
		Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("No book found with ID::  "+bookId));
		if(book.isArchived() || !book.isShareable())
		{
			throw new OperationNotPermittedException("The requested book cannot be borrwed since it is borrowed and not shareable !");
		}
		User user = (User) connectedUser.getPrincipal();
		if(Objects.equals(user.getId(), book.getOwner().getId()))
		{
			throw new OperationNotPermittedException("You cannot borrow your own book !");
		}
		final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadbyBorrowedByUser(bookId, user.getId());
		if (isAlreadyBorrowed)
		{
			throw new OperationNotPermittedException("The requested bok is already borrowed !");
		}
		BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
				.user(user)
				.book(book)
				.returned(false)
				.returnedApproved(false)
				.build();
		return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();		
	}

	public Long returnBorrowedBook(Long bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("No book found with ID::  "+bookId));
		if(book.isArchived() || !book.isShareable())
		{
			throw new OperationNotPermittedException("The requested book cannot be returned since it is archived or not shareable !");
		}
		User user = (User) connectedUser.getPrincipal();
		if(Objects.equals(user.getId(), book.getOwner().getId()))
		{
			throw new OperationNotPermittedException("You cannot borrow or return return your own book !");
		}
		
		BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
				.orElseThrow(()->  new OperationNotPermittedException("You did not borrowed this book !"));
				bookTransactionHistory.setReturned(true);
			return	bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
		
	}

	public Long approveReturnBorrowedBook(Long bookId, Authentication connectedUser) {
		Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("No book found with ID::  "+bookId));
		if(book.isArchived() || !book.isShareable())
		{
			throw new OperationNotPermittedException("The requested book cannot be returned since it is archived or not shareable !");
		}
		User user = (User) connectedUser.getPrincipal();
		if(Objects.equals(user.getId(), book.getOwner().getId()))
		{
			throw new OperationNotPermittedException("You cannot borrow or return return your own book !");
		}
		
		BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndOwnerId(bookId, book.getOwner().getId())
				.orElseThrow(()->  new OperationNotPermittedException("The book is not retrurned yet ! "));
				bookTransactionHistory.setReturnedApproved(true);
			return	bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
		
	}

	public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Long bookId) {
		Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("No book found with ID::  "+bookId));
		User user = (User) connectedUser.getPrincipal();
		var bookCover = fileStorageService.saveFile(file, user.getId());
		book.setBookCover(bookCover);
		bookRepository.save(book);
	}

}
