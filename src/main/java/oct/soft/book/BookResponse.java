package oct.soft.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
	private Long id;
	private String authorName;
	private String isbn;
	private String synopsis;
	private String title;
	private byte[] cover;
	private boolean archived;
	private boolean shareable;
	private String owner;
	private double rate;
	
}
