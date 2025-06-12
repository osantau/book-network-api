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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oct.soft.common.PageResponse;


@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {
	private final BookService bookService;

	@PostMapping
	public ResponseEntity<Long> saveBook(@Valid @RequestBody BookRequest request, Authentication connectedUser) {

		return ResponseEntity.ok(bookService.save(request, connectedUser));
	}
	
	@GetMapping("{book-id}")
	public ResponseEntity<BookResponse> findById(@PathVariable("book-id") Long bookId) {
		return ResponseEntity.ok(bookService.findById(bookId));
	}
	
	@GetMapping
	public ResponseEntity <PageResponse<BookResponse>> findAllBooks(@RequestParam(name="page", defaultValue = "0", required = false) int page,
			@RequestParam(name="size", defaultValue = "10", required = false) int size,
			Authentication connectedUser
			){
		return ResponseEntity.ok(bookService.findAll(page, size, connectedUser));
	}
	
	@GetMapping("/owner")
	public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(@RequestParam(name="page", defaultValue = "0", required = false) int page,
			@RequestParam(name="size", defaultValue = "10", required = false) int size,
			Authentication connectedUser) {
		return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectedUser));
	}
	
	@GetMapping("/borrowed")
	public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(@RequestParam(name="page", defaultValue = "0", required = false) int page,
			@RequestParam(name="size", defaultValue = "10", required = false) int size,
			Authentication connectedUser) {
		return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, connectedUser));
	}
	
	@GetMapping("/returned")
	public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(@RequestParam(name="page", defaultValue = "0", required = false) int page,
			@RequestParam(name="size", defaultValue = "10", required = false) int size,
			Authentication connectedUser) {
		return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, connectedUser));
	}
	
	@PatchMapping("/shareable/{book-id}")
	public ResponseEntity<Long> updateShareableStatus(@PathVariable(name = "book-id") Long bookId, Authentication connectedUser)
	{
		return ResponseEntity.ok(bookService.updateShareableStatus(bookId, connectedUser));
	}
	
	@PatchMapping("/archive/{book-id}")
	public ResponseEntity<Long> updateArchiveStatus(@PathVariable(name = "book-id") Long bookId, Authentication connectedUser)
	{
		return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, connectedUser));
	}
	
	@PostMapping("/borrow/{book-id}")
	public ResponseEntity<Long> borrowBook(@PathVariable(name = "book-id") Long bookId, Authentication connectedUser)
	{
		return ResponseEntity.ok(bookService.borrowBook(bookId, connectedUser));
	}
	
	@PatchMapping("/borrow/return/{book-id}")
	public ResponseEntity<Long> returnBorrowBook(@PathVariable(name = "book-id") Long bookId, Authentication connectedUser)
	{
		return ResponseEntity.ok(bookService.returnBorrowedBook(bookId, connectedUser));
	}
		
}
