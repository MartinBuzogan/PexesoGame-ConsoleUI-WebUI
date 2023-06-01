package gamestudio.service;

import gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class RatingServiceJPA implements RatingService{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        if (getRating(rating.getGame(), rating.getPlayer()) == -1) {
            entityManager.persist(rating);
        } else {
            entityManager.createNamedQuery("Rating.updateRating").setParameter("newRating",rating.getRating()).
                    setParameter("ratedOn",rating.getRatedOn()).
                    setParameter("game",rating.getGame()).setParameter("player",rating.getPlayer()).executeUpdate();
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try {
            var average =  entityManager.createNamedQuery("Rating.getAverageRating").setParameter("game",game).getSingleResult();
            return average == null ? 0 : ((int)((double)average));
        } catch (Exception e) {
            throw new RatingException("Problem selecting rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try{
            return (int) entityManager.createNamedQuery("Rating.getRating").setParameter("game",game).setParameter("player",player).getSingleResult();
        }catch (NoResultException e){
            return -1;
        }
    }

    @Override
    public void reset() throws RatingException {
        entityManager.createNamedQuery("Rating.resetRating").executeUpdate();
    }
}
