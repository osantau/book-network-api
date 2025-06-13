package oct.soft.history;

import java.util.Optional;

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
			AND history.returnedApproved=false
			""")
	boolean isAlreadyBorrowed(Long bookId, Long userId);

	@Query("""
			SELECT transaction FROM BookTransactionHistory transaction
			WHERE transaction.user.id = :userId
			AND transaction.book.id=:bookId
			AND transaction.returned=false 
			AND transaction.returnedApproved=false
			""")
	Optional<BookTransactionHistory> findByBookIdAndUserId(Long bookId, Long userIsd);

	@Query("""
			SELECT transaction FROM BookTransactionHistory transaction
			WHERE transaction.book.owner.id = :userId
			AND transaction.book.id=:bookId
			AND transaction.returned=true 
			AND transaction.returnedApproved=false
			""")
	Optional<BookTransactionHistory> findByBookIdAndOwnerId(Long bookId, Long userId);
}
