package oct.soft.book.history;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {

	@Query("""
			SELECT history FROM BookTransactionHistory history WHERE history.user.id=:userId
			""")
	Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Long userId);

	@Query("""
			SELECT history FROM BookTransactionHistory history WHERE history.book.owner.id=:userId
			""")
	Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Long userId);

	@Query("""
			SELECT count(*) > 0 AS isBorrowed FROM BookTransactionHistory history 
			WHERE history.user.id=:userId 
			AND history.book.id = :bookId 
			AND history.returnApproved = false
			""")
	boolean isAlreadbyBorrowedByUser(Long bookId, Long userId);

	@Query("""
			SELECT transaction from BookTransactionHistory transaction 
			WHERE transaction.user.id = :userId 
			AND transaction.book.id=:bookId 
			AND transaction.returned=false 
			AND transaction.returnApproved=false 
			""")
	Optional<BookTransactionHistory> findByBookIdAndUserId(Long bookId, Long userId);

	@Query("""
			SELECT transaction from BookTransactionHistory transaction 
			WHERE transaction.owner.id = :userId 
			AND transaction.book.id=:bookId 
			AND transaction.returned=true 
			AND transaction.returnApproved=false 
			""")
	Optional<BookTransactionHistory> findByBookIdAndOwnerId(Long bookId, Long userId);

}
