package com.iqmsoft.mongo.repository;

import com.iqmsoft.domain.Micropost;
import com.iqmsoft.domain.MyUser;
import com.iqmsoft.dto.PageParams;
import com.iqmsoft.dto.PostDTO;
import com.iqmsoft.dto.RelatedUserDTO;
import com.iqmsoft.dto.UserDTO;
import com.iqmsoft.dto.UserStats;

import com.iqmsoft.domain.Relationship;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UserRepositoryImpl implements UserRepositoryCustom {

	
	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
	
	private final JPAQueryFactory queryFactory;

	//private final QMyUser qUser = QMyUser.myUser;
	//private final QRelationship qRelationship = QRelationship.relationship;

	@Autowired
	public UserRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public List<RelatedUserDTO> findFollowings(MyUser user, MyUser currentUser, PageParams pageParams) {
		///final ConstructorExpression<UserStats> userStatsExpression = UserStatsQueryHelper.userStatsExpression(qUser,
				//currentUser);

		logger.debug("Find Followings Max Page Params " + pageParams.getMaxId());
		logger.debug("Find Followings Since Page Params " + pageParams.getSinceId());
		
		UserStats u = new UserStats(user.getMicroposts().size(), user.getFollowedRelations().size(),
				user.getFollowerRelations().size(), false);

		List<RelatedUserDTO> p = new ArrayList<>();
		
        try {
    		
    		p.sort(new Comparator<RelatedUserDTO>(){
                
       		 @Override
       		 public int compare(RelatedUserDTO s1, RelatedUserDTO s2){
                        return (int) (s1.getUid() - s2.getUid());
                }
                
       	 });
		} catch (Exception e) {
			
			 logger.debug("Find By User Page Params Error " + e.getMessage());
		}
    	 
        int i = 0;
        
		for (Relationship m : user.getFollowedRelations()) {
			
			if ((pageParams.getSinceId().get() != null) && (pageParams.getMaxId().get() != null)) {

				if (m != null) {

					if ((m.getFollower().getUid() > pageParams.getSinceId().get())
							&& (pageParams.getMaxId().get() == 0)) {

						RelatedUserDTO r = RelatedUserDTO.builder().user(m.getFollower()).relationship(m).userStats(u)
								.build();
						p.add(r);
						i = i + 1;

						if (i == pageParams.getCount())
							break;
					}
				}
			}
			
			if ((pageParams.getSinceId().get() == 0) && (pageParams.getMaxId().get() != null)) {

				logger.debug("Find By User UID" + m.getFollower().getUid());
				
				logger.debug("Find By User Max UID" + pageParams.getMaxId().get());
				
				if (pageParams.getMaxId().get() > 0)
				{
					if ((m.getFollower().getUid() > pageParams.getMaxId().get())) {

						logger.debug("Added Followings");

						RelatedUserDTO r = RelatedUserDTO.builder().user(m.getFollower()).relationship(m).userStats(u)
								.build();
						
						p.add(r);
						i = i + 1;
						
						if(i == pageParams.getCount()) break;
					}
				}
			}
			
			
		}

		try {
	    		
	    		p.sort(new Comparator<RelatedUserDTO>(){
	                
	       		 @Override
	       		 public int compare(RelatedUserDTO s1, RelatedUserDTO s2){
	                        return (int) (s1.getUid() - s2.getUid());
	                }
	                
	       	 });
			} catch (Exception e) {
				
				 logger.debug("Find By Followings Page Params Error " + e.getMessage());
			}
	    	 
		
		
		return p;

		/*
		 * return queryFactory.select(qUser, qRelationship, userStatsExpression)
		 * .from(qUser) .innerJoin(qUser.followedRelations, qRelationship)
		 * .where(qRelationship.follower.eq(user) //
		 * .and(pageParams.getSinceId().map(qRelationship.id::gt).orElse(null))
		 * // .and(pageParams.getMaxId().map(qRelationship.id::lt).orElse(null))
		 * ) .orderBy(qRelationship.id.desc()) .limit(pageParams.getCount())
		 * .fetch() .stream() .map(row -> RelatedUserDTO.builder()
		 * .user(row.get(qUser)) .relationship(row.get(qRelationship))
		 * .userStats(row.get(userStatsExpression)) .build())
		 * .collect(Collectors.toList());
		 */
	}

	@Override
	public List<RelatedUserDTO> findFollowers(MyUser user, MyUser currentUser, PageParams pageParams) {
		//final ConstructorExpression<UserStats> userStatsExpression = UserStatsQueryHelper.userStatsExpression(qUser,
				//currentUser);
				
		logger.debug("Find By User Page Params " + pageParams.getMaxId());
		logger.debug("Find By User Page Params " + pageParams.getSinceId());
		
		int i = 0;
		 
		UserStats u = new UserStats(user.getMicroposts().size(), user.getFollowedRelations().size(),
				user.getFollowerRelations().size(), false);

		List<RelatedUserDTO> p = new ArrayList<>();

		for (Relationship m : user.getFollowerRelations()) {
			
				
				if ((pageParams.getSinceId().get() != null) && (pageParams.getMaxId().get() != null)) {

					if (m != null) {

						if ((m.getFollowed().getUid() > pageParams.getSinceId().get())
								&& (pageParams.getMaxId().get() == 0)) {

							RelatedUserDTO r = RelatedUserDTO.builder().user(m.getFollowed())
									.relationship(m).userStats(u)
									.build();
							p.add(r);
							i = i + 1;

							if (i == pageParams.getCount())
								break;
						}
					}
				
				
	
			}
			
			if ((pageParams.getSinceId().get() == 0) && (pageParams.getMaxId().get() != null)) {

				logger.debug("Find By User UID" + m.getFollowed().getUid());
				
				logger.debug("Find By User Max UID" + pageParams.getMaxId().get());
				
				if (pageParams.getMaxId().get() > 0)
				{
					if ((m.getFollowed().getUid() > pageParams.getMaxId().get())) {

						logger.debug("Added Followers");

						RelatedUserDTO r = RelatedUserDTO.builder().user(m.getFollowed())
								.relationship(m).userStats(u)
								.build();
						
						p.add(r);
						i = i + 1;
						
						if(i == pageParams.getCount()) break;
					}
				}
			}
			
			
		}

		
		 try {
	    		
	    		p.sort(new Comparator<RelatedUserDTO>(){
	                
	       		 @Override
	       		 public int compare(RelatedUserDTO s1, RelatedUserDTO s2){
	                        return (int) (s1.getUid() - s2.getUid());
	                }
	                
	       	 });
			} catch (Exception e) {
				
				 logger.debug("Find By User Page Params Error " + e.getMessage());
			}
		
		return p;
		
		/*return queryFactory.select(qUser, qRelationship, userStatsExpression).from(qUser)
				.innerJoin(qUser.followerRelations, qRelationship).where(qRelationship.followed.eq(user)
				// .and(pageParams.getSinceId().map(qRelationship.id::gt).orElse(null))
				// .and(pageParams.getMaxId().map(qRelationship.id::lt).orElse(null))
	
				).orderBy(qRelationship.id.desc()).limit(pageParams.getCount()).fetch().stream().map(row -> RelatedUserDTO.builder().user(row.get(qUser)).relationship(row.get(qRelationship)).userStats(row.get(userStatsExpression)).build()).collect(Collectors.toList());
	      */
	}

	@Override
	public Optional<UserDTO> findOne(String userId, MyUser currentUser) {
		//final ConstructorExpression<UserStats> userStatsExpression = UserStatsQueryHelper.userStatsExpression(qUser,
				//currentUser);

		
		
		UserStats u = new UserStats(currentUser.getMicroposts().size(), currentUser.getFollowedRelations().size(),
				currentUser.getFollowerRelations().size(), false);

		return Optional.ofNullable(UserDTO.builder().user(currentUser).userStats(u).build());

		/*
		 * final Tuple row = queryFactory.select(qUser, userStatsExpression)
		 * .from(qUser) .where(qUser.id.eq(userId)) .fetchOne(); return
		 * Optional.ofNullable(row) .map(r -> UserDTO.builder()
		 * .user(r.get(qUser)) .userStats(r.get(userStatsExpression)) .build());
		 */
	}

}
