package oct.soft.feedback;

import java.util.Objects;

import org.springframework.stereotype.Service;

import oct.soft.book.Book;

@Service
public class FeedbackMapper {

	public Feedback toFeedback(FeedbackRequest request) {
		
		return Feedback.builder().note(request.note())
				.comment(request.comment())
				.book(Book.builder().id(request.bookId())
						.archived(false) // Not required and has no impact. It just to satisfy lombok
						.shareable(false) // Not required and has no impact. It just to satisfy lombok
						.build())
				.build();
	}

	public FeedbackResponse toFeedbackResponse(Feedback f, Long userId) {

		return FeedbackResponse.builder()
				.note(f.getNote())
				.comment(f.getComment())
				.ownFeedback(Objects.equals(f.getCreatedBy(), userId))
				.build();
	}

}
