package de.sb.messenger.persistence;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import javax.validation.*;
import java.util.*;
import java.util.List.*;

public class EntityTest {

	private List<Long> wasteBasket = new List<Long>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<Long> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(Long aLong) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Long> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends Long> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public Long get(int index) {
            return null;
        }

        @Override
        public Long set(int index, Long element) {
            return null;
        }

        @Override
        public void add(int index, Long element) {

        }

        @Override
        public Long remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<Long> listIterator() {
            return null;
        }

        @Override
        public ListIterator<Long> listIterator(int index) {
            return null;
        }

        @Override
        public List<Long> subList(int fromIndex, int toIndex) {
            return null;
        }
    };
	
	public EntityManagerFactory EntityManagerFactory()
	{
		return new EntityManagerFactory() {
            @Override
            public EntityManager createEntityManager() {
                return null;
            }

            @Override
            public EntityManager createEntityManager(Map map) {
                return null;
            }

            @Override
            public EntityManager createEntityManager(SynchronizationType synchronizationType) {
                return null;
            }

            @Override
            public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
                return null;
            }

            @Override
            public CriteriaBuilder getCriteriaBuilder() {
                return null;
            }

            @Override
            public Metamodel getMetamodel() {
                return null;
            }

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public void close() {

            }

            @Override
            public Map<String, Object> getProperties() {
                return null;
            }

            @Override
            public Cache getCache() {
                return null;
            }

            @Override
            public PersistenceUnitUtil getPersistenceUnitUtil() {
                return null;
            }

            @Override
            public void addNamedQuery(String name, Query query) {

            }

            @Override
            public <T> T unwrap(Class<T> cls) {
                return null;
            }

            @Override
            public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {

            }
        };
	}
	
	public ValidatorFactory getEntityValidatorFactory()
	{
		return new ValidatorFactory() {
            @Override
            public Validator getValidator() {
                return null;
            }

            @Override
            public ValidatorContext usingContext() {
                return null;
            }

            @Override
            public MessageInterpolator getMessageInterpolator() {
                return null;
            }

            @Override
            public TraversableResolver getTraversableResolver() {
                return null;
            }

            @Override
            public ConstraintValidatorFactory getConstraintValidatorFactory() {
                return null;
            }

            @Override
            public ParameterNameProvider getParameterNameProvider() {
                return null;
            }

            @Override
            public <T> T unwrap(Class<T> type) {
                return null;
            }

            @Override
            public void close() {

            }
        };
	}
	
	public List<Long> getWasteBasket()
	{
		return wasteBasket;
	}
}
