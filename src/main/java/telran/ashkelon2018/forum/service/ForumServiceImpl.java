package telran.ashkelon2018.forum.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.ForumRepository;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.Comment;
import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.ForbiddenException;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;

@Service
public class ForumServiceImpl implements ForumService {

	@Autowired
	ForumRepository repository;
	
	@Autowired
	UserAccountRepository userRepository;

	@Autowired
	AccountConfiguration accountConfiguration;

	@Override
	public Post addNewPost(NewPostDto newPost) {
		Post post = new Post(newPost.getTitle(), newPost.getContent(), newPost.getAuthor(),
				newPost.getTags());
		return repository.save(post);
	}

	@Override
	public Post getPost(String id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public Post removePost(String id, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccountWhoDelete = userRepository.findById(credentials.getLogin()).get();
		Post post = repository.findById(id).orElse(null);
		
		String userAccountWhoOwner = post.getAuthor();
				
		if(userAccountWhoDelete.getRoles().contains("Admin")||
				userAccountWhoDelete.getRoles().contains("MODERATOR") ||
				userAccountWhoDelete.getLogin().toString().equals(userAccountWhoOwner)) {
			if(post != null) {
				repository.deleteById(id);
				}
		}
		return post;
	}

	@Override
	public Post updatePost(PostUpdateDto postUpdateDto, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccountWhoUpdate = userRepository.findById(credentials.getLogin()).get();
		Post post = repository.findById(postUpdateDto.getId()).get();
		String userAccountWhoOwner = post.getAuthor();
		
		Set<String> roles = userAccountWhoUpdate.getRoles();
		boolean hasRight = roles.stream()
				.anyMatch(s->userAccountWhoOwner.equals(s));
						
		if(hasRight) {
		try {
			throw new ForbiddenException();
		} catch (ForbiddenException e) {
			e.printStackTrace();
		}
		}
		post.setTitle(postUpdateDto.getTitle());
		post.setContent(postUpdateDto.getContent());
		return repository.save(post);
	}

	@Override
	public boolean addLike(String id) {
		Post post = repository.findById(id).orElse(null);
		if (post == null) {
			return false;
		}
		post.addLike();
		repository.save(post);
		return true;
	}

	@Override
	public Post addComment(String id, NewCommentDto newComment) {
		Post post = repository.findById(id).get();
		Comment comment = new Comment(newComment.getUser(), 
				newComment.getMessage());
		post.addComment(comment);
		return repository.save(post);
		}

	@Override
	public Iterable<Post> findPostsByTags(List<String> tags) {
		return repository.findByTags(tags);
	}

	@Override
	public Iterable<Post> findPostsByAuthor(String author) {
			return repository.findByAuthor(author);
	}

	@Override
	public Iterable<Post> findPostsByDates(DatePeriodDto datePeriodDto) {
		return repository.findByDateCreatedBetween(datePeriodDto.getFrom(), datePeriodDto.getTo());
	}

}
