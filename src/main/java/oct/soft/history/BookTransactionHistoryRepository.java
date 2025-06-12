package oct.soft.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookTransactionHistoryRepository
		extends JpaRepository<BookTransactionHistory, Long>, JpaSpecificationExecutor<BookTransactionHistory> {

	@Query(""" 
			SELECT history from BookTransactionHistory history 
			WHERE history.user.id = :userId 
			""")
	Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Long userId);
	
	@Query(""" 
			SELECT history from BookTransactionHistory history 
			WHERE history.book.createdBy = :userId
			""")
	Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Long userId);

	@Query(""" 
			SELECT (count(*) > 0) as isBorrowed FROM BookTransactionHistory history 
			WHERE history.user.id = :userId 
			AND history.book.id=:bookId 
			AND history.returnApproved=false
			""")
	boolean isAlreadyBorrowed(Long bookId, Long userId);
}
