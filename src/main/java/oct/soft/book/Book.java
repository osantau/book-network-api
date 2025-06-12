package oct.soft.book;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import oct.soft.common.BaseEntity;
import oct.soft.feedback.Feedback;
import oct.soft.history.BookTransactionHistory;
import oct.soft.user.User;  

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Book extends BaseEntity{

	
	private String title;
	private String authorName;
	private String isbn;
	private String synopsis;
	private String bookCover;
	private boolean archived;
	private boolean shareable;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	private User owner;
	
	@OneToMany(mappedBy = "book")
	private List<Feedback> feedbacks;
	
	@OneToMany(mappedBy = "book")
	private List<BookTransactionHistory> histories ;
}
