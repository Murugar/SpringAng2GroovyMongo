package com.iqmsoft.mongo.repository;

import com.iqmsoft.domain.*;
import com.iqmsoft.dto.PageParams;
import com.iqmsoft.dto.PostDTO;
import com.iqmsoft.dto.UserStats;
//import com.iqmsoft.utils.UserStatsQueryHelper;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class MicropostRepositoryImpl implements MicropostRepositoryCustom {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = LoggerFactory.getLogger(MicropostRepositoryImpl.class);

    private final JPAQueryFactory queryFactory;

    @Autowired
    public MicropostRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<PostDTO> findAsFeed(MyUser user, PageParams pageParams) {
    	
    	
    	
    	 List<Micropost> l =  user.getMicroposts();
    	 
    	 
    	 Comparator<Micropost> c = new Comparator<Micropost>(){
            
    		 @Override
    		 public int compare(Micropost s1, Micropost s2){
                     return (int) (s1.getUid() - s2.getUid());
             }
             
    	 };
    	 
    	 l.sort(c);
    	 
    	 List<PostDTO> p = new ArrayList<>();
    	 
    	 UserStats u = new UserStats(user.getMicroposts().size(),
    			 user.getFollowedRelations().size(), user.getFollowerRelations().size(), false);
    	 
    	 logger.debug("Page Params " + pageParams);
    	
    	 for(Micropost m : l)
		{
    		 
    		 logger.debug("Micropost findAsFeed " + m.getUid());
    		 
    		 p.add(PostDTO.builder().micropost(m).user(user).userStats(u).isMyPost(true).build());
    		 
			/*if ((pageParams.getSinceId().get() != null) && (pageParams.getMaxId().get() != null)) {

				if ((m.getUid() > pageParams.getSinceId().get()) && (m.getUid() < pageParams.getMaxId().get())) {
					p.add(PostDTO.builder().micropost(m).user(user).userStats(u).isMyPost(true).build());
				}
			}

			else if ((pageParams.getSinceId().get() == null) && (pageParams.getMaxId().get() != null)) {

				if ((m.getUid() > pageParams.getSinceId().get()) && (m.getUid() < pageParams.getMaxId().get())) {
					p.add(PostDTO.builder().micropost(m).user(user).userStats(u).isMyPost(true).build());
				}
			}

			else if ((pageParams.getSinceId().get() != null) && (pageParams.getMaxId().get() == null)) {

				if ((m.getUid() > pageParams.getSinceId().get())) {
					p.add(PostDTO.builder().micropost(m).user(user).userStats(u).isMyPost(true).build());
				}
			}
			else
			{
				p.add(PostDTO.builder().micropost(m).user(user).userStats(u).isMyPost(true).build());
			}*/
			
		}
    	 
    	 return p;
     
    	/*final QMicropost qMicropost = QMicropost.micropost;
        final QRelationship qRelationship = QRelationship.relationship;

        final ConstructorExpression<UserStats> userStatsExpression =
                UserStatsQueryHelper.userStatsExpression(qMicropost.user, user);
        final JPQLQuery<Relationship> relationshipSubQuery = JPAExpressions
                .selectFrom(qRelationship)
                .where(qRelationship.follower.eq(user)
                        .and(qRelationship.followed.eq(qMicropost.user))
                );
        return queryFactory.select(qMicropost, qMicropost.user, userStatsExpression)
                .from(qMicropost)
             //   .innerJoin(qMicropost.user)
               // .where((qMicropost.user.eq(user).or(relationshipSubQuery.exists()))
                       // .and(pageParams.getSinceId().map(qMicropost.id::gt).orElse(null))
                        //.and(pageParams.getMaxId().map(qMicropost.id::lt).orElse(null))
               // )
              //  .orderBy(qMicropost.id.desc())
              //  .limit(pageParams.getCount())
                .fetch()
                .stream()
                .map(row -> PostDTO.builder()
                        .micropost(row.get(qMicropost))
                       // .user(row.get(qMicropost.user))
                       // .userStats(row.get(userStatsExpression))
                        .build()
                )
                .collect(Collectors.toList());*/
    }

    @Override
    public List<Micropost> findByUser(MyUser user, PageParams pageParams) {
        
    	 logger.debug("Find By User Page Params " + pageParams.getMaxId());
    	
         int i = 0;
    	 
         List<Micropost> ml =  new ArrayList<>();
         
         List<Micropost> l = user.getMicroposts();
         
         if(l == null)
         {
        	 return ml;
         }
         
         if(l.size() == 0) return l;
         
         
    	try {
    		
    		l.sort(new Comparator<Micropost>(){
                
       		 @Override
       		 public int compare(Micropost s1, Micropost s2){
                        return (int) (s1.getUid() - s2.getUid());
                }
                
       	 });
		} catch (Exception e) {
			
			 logger.debug("Find By User Page Params Error " + e.getMessage());
		}
    	 
        if(pageParams.getCount() > l.size()) return l;
    	 
         for(Micropost m : l)
 		{
         
			if ((pageParams.getSinceId().get() != null) && (pageParams.getMaxId().get() != null)) {

				if ((m.getUid() > pageParams.getSinceId().get())  && (pageParams.getMaxId().get() == 0)) {
					ml.add(m);
					i = i + 1;
					
					if(i == pageParams.getCount()) break;
				}
			}
			
			if ((pageParams.getSinceId().get() == 0) && (pageParams.getMaxId().get() != null)) {

				logger.debug("Find By User UID" + m.getUid());
				
				logger.debug("Find By User Max UID" + pageParams.getMaxId().get());
				
				if (pageParams.getMaxId().get() > 0)
				{
					if ((m.getUid() > pageParams.getMaxId().get())) {

						logger.debug("Added Micropost");

						ml.add(m);
						i = i + 1;
						
						if(i == pageParams.getCount()) break;
					}
				}
			}

			else if ((pageParams.getSinceId().get() == null) && (pageParams.getMaxId().get() != null)) {

				if ((m.getUid() > pageParams.getSinceId().get()) && (m.getUid() < pageParams.getMaxId().get())) {
					//ml.add(m);
				}
			}

			else if ((pageParams.getSinceId().get() != null) && (pageParams.getMaxId().get() == null)) {

				if ((m.getUid() > pageParams.getSinceId().get())) {
					ml.add(m);
				}
			}
			else
			{
				//ml.add(m);
			}
         
		
			
			
 		 }
         
         
         logger.debug("Find By User Page Params " + ml.size());
         
    	 return ml;
    	 
    	 /*
    	
    	final QMicropost qMicropost = QMicropost.micropost;
        
        
        
        return queryFactory.selectFrom(qMicropost)
                .where(qMicropost.user.uid.eq(user.getUid())
                        .and(pageParams.getSinceId().map(qMicropost.uid::gt).orElse(null))
                        .and(pageParams.getMaxId().map(qMicropost.uid::lt).orElse(null))
                )
                .orderBy(qMicropost.uid.desc())
                .limit(pageParams.getCount())
                .fetch();*/
                
    }
}
