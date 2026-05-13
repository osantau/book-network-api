package oct.soft.feedback;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oct.soft.common.PageResponse;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

	private final FeedbackService service;

	@PostMapping
	public ResponseEntity<Long> saveFeedback(@Valid @RequestBody FeedbackRequest request,
			Authentication connectedUser) {
		return ResponseEntity.ok(service.save(request, connectedUser));
	}

	@GetMapping("/book/{book-id}")
	public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(@PathVariable("book-id") Long bookId,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication connectedUser) {
		return ResponseEntity.ok(service.findAllFeedbackByBook(bookId, page, size, connectedUser));
	}
}
