package oct.soft.book;

import java.util.Base64;

import org.springframework.stereotype.Service;

import oct.soft.history.BookTransactionHistory;
import oct.soft.utils.FileUtils;

@Service
public class BookMapper {

	public Book toBook(BookRequest request) {

		return Book.builder()
				.id(request.id())
				.title(request.title())
				.authorName(request.authorName())
				.synopsis(request.synopsis())
				.archived(false)
				.shareable(request.shareable())
				.build();
	}
	
	public BookResponse toBookResponse(Book book)
	{
		String imgBase64 = null;
		if(book.getBookCover()!=null)
		{
			imgBase64 = Base64.getEncoder().encodeToString(FileUtils.readFileFromLocation(book.getBookCover()));
		}
		return BookResponse.builder()
				.id(book.getId())
				.title(book.getTitle())
				.isbn(book.getIsbn())
				.synopsis(book.getSynopsis())
				.rate(book.getRate())
				.archived(book.isArchived())
				.shareable(book.isShareable())
				.owner(book.getOwner().fullName())
//				.cover(FileUtils.readFileFromLocation(book.getBookCover()))
				.bookCover(book.getBookCover())
				.imgBase64(imgBase64)
				.build();
	}
	
	public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history)
	{
		return BorrowedBookResponse.builder()
				.id(history.getBook().getId())
				.title(history.getBook().getTitle())
				.authorName(history.getBook().getAuthorName())
				.isbn(history.getBook().getIsbn())				
				.rate(history.getBook().getRate())				
				.returned(history.isReturned())
				.returnedApproved(history.isReturnedApproved())				
				.build();
	}

}
