package oct.soft.feedback;

import java.util.Objects;

import org.springframework.stereotype.Service;

import oct.soft.book.Book;

@Service
public class FeedbackMapper {

	public Feedback toFeedback(FeedbackRequest req) {
	
		return Feedback.builder()
				.note(req.note())
				.comment(req.comment())
				.book(Book.builder().id(req.bookId()).build())
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
