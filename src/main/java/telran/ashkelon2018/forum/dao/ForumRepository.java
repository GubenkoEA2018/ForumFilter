package telran.ashkelon2018.forum.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.ashkelon2018.forum.domain.Post;

public interface ForumRepository extends MongoRepository<Post, String> {

	Stream<Post> findAllBy();
	
	Iterable<Post> findByTags(List<String> tags);
	
	Iterable<Post> findByAuthor(String author);
	
	Iterable<Post> findByDateCreatedBetween(LocalDate from, LocalDate to);
	
}
