package oct.soft.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class BorrowedBookResponse {
	private Long id;
	private String title;
	private String authorName;
	private String isbn;
	private double rate;
	private boolean returned;
	private boolean returnedApproved;
}
