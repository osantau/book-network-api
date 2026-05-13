package oct.soft.feedback;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oct.soft.book.Book;
import oct.soft.book.BookRepository;
import oct.soft.common.PageResponse;
import oct.soft.exception.OperationNotPermittedException;
import oct.soft.user.User;

@Service
@RequiredArgsConstructor
public class FeedbackService {

	private final FeedbackRepository feedbackRepository;
	private final BookRepository bookRepository;
	private final FeedbackMapper feedbackMapper;

	public Long save(@Valid FeedbackRequest request, Authentication connectedUser) {
		Book book = bookRepository.findById(request.bookId())
				.orElseThrow(() -> new EntityNotFoundException("Book not found with " + request.bookId()));
		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException(
					"You cannot give a feedback for an archived or NOT shareable book!");
		}
		User user = (User) connectedUser.getPrincipal();
		if (Objects.equals(book.getOwner().getId(), user.getId())) {
			throw new OperationNotPermittedException("You cannot give a feedback to your own  book!");
		}

		Feedback feedback = feedbackMapper.toFeedback(request);
		return feedbackRepository.save(feedback).getId();
	}

	public PageResponse<FeedbackResponse> findAllFeedbackByBook(Long bookId, int page, int size,
			Authentication connectedUser) {
		Pageable pageable = PageRequest.of(page, size);
		User user = (User) connectedUser.getPrincipal();
		Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
		List<FeedbackResponse> feedbackResponses = feedbacks.stream()
				.map(f -> feedbackMapper.toFeedbackResponse(f, user.getId())).toList();
		return new PageResponse<>(feedbackResponses, feedbacks.getNumber(), feedbacks.getSize(),
				feedbacks.getTotalElements(), feedbacks.getTotalPages(), feedbacks.isFirst(), feedbacks.isLast());
	}
}
