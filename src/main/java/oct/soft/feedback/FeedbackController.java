package oct.soft.feedback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name="Feedback")
public class FeedbackController {
	private final FeedbackService service;
	
	@PostMapping
	public ResponseEntity<Long> saveFeedback(@Valid @RequestBody FeedbackRequest req, Authentication currentUser)
	{
		return ResponseEntity.ok(service.save(req, currentUser));
	}
	
	@GetMapping("/book/{book-id}")
	public ResponseEntity<PageResponse<FeedbackResponse>> findFeedbacksByBook(@PathVariable("book-id") Long bookId,
			@RequestParam(name="page", defaultValue = "0", required = false) int page,
			@RequestParam(name="size", defaultValue = "10", required = false) int size,
			 Authentication currentUser)
	{
		return ResponseEntity.ok(service.findAllFeedbacksByBook(bookId, page,size,currentUser));
	}
}
