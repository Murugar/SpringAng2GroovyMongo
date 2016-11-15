package com.iqmsoft.controller;


import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.iqmsoft.domain.Relationship;
import com.iqmsoft.domain.MyUser;
import com.iqmsoft.mongo.repository.RelationshipRepository;
import com.iqmsoft.mongo.repository.UserRepository;
import com.iqmsoft.security.SecurityContextService;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

	
	private static final Logger logger = LoggerFactory.getLogger(RelationshipController.class);
	
    private final UserRepository userRepository;
    private final RelationshipRepository relationshipRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public RelationshipController(UserRepository userRepository,
                                  RelationshipRepository relationshipRepository,
                                  SecurityContextService securityContextService) {
        this.userRepository = userRepository;
        this.relationshipRepository = relationshipRepository;
        this.securityContextService = securityContextService;
    }

    @RequestMapping(value = "/to/{followedId}", method = RequestMethod.POST)
	public void follow(@PathVariable("followedId") long followedId) {

		List<MyUser> l = userRepository.findAll();

		for (MyUser u : l) {
			logger.debug("Follow " + u.getUid() + "  " + followedId);

			if (u.getUid() == followedId) {

				logger.debug("Follow " + u.getUid() + "  " + u.getId());

				final MyUser followed = userRepository.findOne(u.getId());

				Set a = followed.getFollowedRelations();

				final MyUser currentUser = securityContextService.currentUser();

				Set b = currentUser.getFollowerRelations();

				List<Relationship> al = relationshipRepository.findAll();

				if (al.size() == 0) {

					logger.debug("First case " + "  ");
					final Relationship relationship = new Relationship(currentUser, followed);

					currentUser.getFollowerRelations().add(relationship);
					followed.getFollowedRelations().add(relationship);

					relationshipRepository.save(relationship);

					userRepository.save(currentUser);
					userRepository.save(followed);
				}

				else {
					logger.debug("Other case " + "  ");

					for (Relationship r : al) {
						logger.debug("First For " + "  ");

						MyUser followed1 = r.getFollowed();
						MyUser follower1 = r.getFollower();

						logger.debug("Followed " + "  " + followed1);
						logger.debug("Follower " + "  " + follower1);

						if (currentUser.getUid() == follower1.getUid()) {
							if (followed.getUid() == followed1.getUid()) {

								logger.debug("Other case " + " delete before add ");

								relationshipRepository.delete(r);
								currentUser.getFollowerRelations().remove(r);
								followed.getFollowedRelations().remove(r);

								userRepository.save(currentUser);
								userRepository.save(followed);
							}
						}
					}

					logger.debug("Other case " + "  add ");

					final Relationship relationship = new Relationship(currentUser, followed);

					currentUser.getFollowerRelations().add(relationship);
					followed.getFollowedRelations().add(relationship);

					relationshipRepository.save(relationship);

					userRepository.save(currentUser);
					userRepository.save(followed);

					logger.debug("Follower Saved " + "  " + followed);
					logger.debug("Current User Saved " + "  " + currentUser);

				}

			}

		}

	}

    @RequestMapping(value = "/to/{followedId}", method = RequestMethod.DELETE)
    public void unfollow(@PathVariable("followedId") long followedId) {
    	
    	
    	 List<MyUser> l = userRepository.findAll();
    	
		for (MyUser u : l) {
			
			logger.debug("UnFollow " + u.getUid() + "  " + followedId);

			if (u.getUid() == followedId) {

				final MyUser followed = userRepository.findOne(u.getId());
				final MyUser currentUser = securityContextService.currentUser();
				final Relationship relationship = relationshipRepository
						.findOneByFollowerAndFollowed(currentUser, followed)
						.orElseThrow(RelationshipNotFoundException::new);

				relationshipRepository.delete(relationship);

				currentUser.getFollowerRelations().remove(relationship);
				followed.getFollowedRelations().remove(relationship);

				userRepository.save(currentUser);
				userRepository.save(followed);
			}
		}
                
        
    }

    @SuppressWarnings("serial")
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No relationship")
    private class RelationshipNotFoundException extends RuntimeException {
    }

}
