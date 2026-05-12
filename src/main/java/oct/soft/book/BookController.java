package oct.soft.book;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oct.soft.common.PageResponse;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
public class BookController {
	private final BookService service;

	@PostMapping
	public ResponseEntity<Long> saveBook(@RequestBody @Valid BookRequest request, Authentication connectedUser) {
		return ResponseEntity.ok(service.save(request, connectedUser));
	}

	@GetMapping("/{book-id}")
	public ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Long bookId) {
		return ResponseEntity.ok(service.findById(bookId));
	}

	@GetMapping
	public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication connectedUser) {
		return ResponseEntity.ok(service.findAllBooks(page, size, connectedUser));
	}

	@GetMapping("/owner")
	public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication connectedUser) {
		return ResponseEntity.ok(service.findAllBooksByOwner(page, size, connectedUser));
	}

	@GetMapping("/borrwed")
	public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication connectedUser) {
		return ResponseEntity.ok(service.findAllBorrowedBooks(page, size, connectedUser));
	}

	@GetMapping("/returned")
	public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication connectedUser) {
		return ResponseEntity.ok(service.findAllReturnedBooks(page, size, connectedUser));
	}

	@PatchMapping("/shareable/{bookId}")
	public ResponseEntity<Long> updateShareableStatus(@PathVariable("bookId") Long bookId,
			Authentication connectedUser) {
		return ResponseEntity.ok(service.udpateShareableStatus(bookId, connectedUser));
	}

	@PatchMapping("/archived/{bookId}")
	public ResponseEntity<Long> updateArchivedStatus(@PathVariable("bookId") Long bookId,
			Authentication connectedUser) {
		return ResponseEntity.ok(service.udpateArchivedStatus(bookId, connectedUser));
	}

	@PostMapping("/borrow/{book-id}")
	public ResponseEntity<Long> borrwedBook(@PathVariable("book-id") Long bookId, Authentication connectedUser) {
		return ResponseEntity.ok(service.borrowBook(bookId, connectedUser));
	}

	@PatchMapping("/borrow/return/{book-id}")
	public ResponseEntity<Long> returnBook(@PathVariable("book-id") Long bookId, Authentication connectedUser) {
		return ResponseEntity.ok(service.returnBorrowedBook(bookId, connectedUser));
	}
	
	@PostMapping("/borrow/return/approve/{book-id}")
	public ResponseEntity<Long> approveReturnBorrowBook(@PathVariable("book-id") Long bookId, Authentication connectedUser) {
		return ResponseEntity.ok(service.approveReturnBorrowedBook(bookId, connectedUser));
	}
}
