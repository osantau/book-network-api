package oct.soft.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FeedbackRequest(
		@Positive(message="200")
		@Min(message = "201", value = 0)
		@Max(message = "202", value = 5)
		Double note,
		@NotNull(message = "203")
		@NotEmpty(message = "203")
		@NotBlank(message = "203")
		String comment,
		@NotNull(message = "204")
		Long bookId
		) {}