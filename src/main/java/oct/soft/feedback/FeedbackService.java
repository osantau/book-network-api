package oct.soft.feedback;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import oct.soft.book.Book;
import oct.soft.book.BookRepository;
import oct.soft.common.PageResponse;
import oct.soft.exception.OperationNotPermittedException;
import oct.soft.user.User;

@Service
@RequiredArgsConstructor
public class FeedbackService {
	private final BookRepository bookRepository;
	private final FeedbackRepository feedbackRepository;
	private final FeedbackMapper feedbackMapper;

	public Long save(FeedbackRequest req, Authentication currentUser) {
		Book book = bookRepository.findById(req.bookId())
				.orElseThrow(() -> new EntityNotFoundException("No book found with the ID:: " + req.bookId()));
		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException(
					"You cannot give feedback  for an archived  or not shareable book");
		}
		User user = ((User) currentUser.getPrincipal());

		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot give feedback toyour own book");
		}

		Feedback feedback = feedbackMapper.toFeedback(req);

		return feedbackRepository.save(feedback).getId();
	}

	public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Long bookId, int page, int size,
			Authentication currentUser) {
		Pageable pageable = PageRequest.of(page, size);
		User user = ((User) currentUser.getPrincipal());
		Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
		List<FeedbackResponse> feedbackResponses = feedbacks.stream()
				.map(f -> feedbackMapper.toFeedbackResponse(f, user.getId())).toList();
		return new PageResponse<>(feedbackResponses, feedbacks.getNumber(), feedbacks.getSize(),
				feedbacks.getTotalElements(), feedbacks.getTotalPages(), feedbacks.isFirst(), feedbacks.isLast());
	}

}
