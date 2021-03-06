package org.joy.core.persistence.orm.jpa;

import org.joy.commons.bean.IEntity;
import org.joy.commons.bean.Single;
import org.joy.commons.log.Log;
import org.joy.commons.log.LogFactory;
import org.joy.commons.query.QueryLogics;
import org.joy.commons.query.sort.Order;
import org.joy.commons.support.GroupExecutor;
import org.joy.core.persistence.support.entity.UuidEntity;
import org.joy.core.rp.pagestore.PageStore;
import org.joy.core.spring.utils.CoreBeanFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * JPA工具类(spring bean)
 *
 * @author Kevice
 * @time 2012-6-26 下午10:36:28
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public class JpaTool extends BaseJpaDao {

    protected static final Log logger = LogFactory.getLog(JpaTool.class);

    private JpaTool() {
    }

    private static JpaTool getInstance() {
        return CoreBeanFactory.getJpaTool();
    }

    /**
     * 持久化实体，如果是UuidEntity的实体，如果没有主键将自动设置
     *
     * @param entity 实体对象
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static void persist(Object entity) {
        UuidEntity.setUuid(entity);
        getEntityMgr().persist(entity);
    }

    /**
     * 持久化实体(带有事务)，如果是UuidEntity的实体，如果没有主键将自动设置
     *
     * @param entity 实体对象
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static void persistWithTx(final Object entity) {
        TransactionTemplate transactionTemplate = CoreBeanFactory.getTransactionTemplate();
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                persist(entity);
            }
        });
    }

    /**
     * 批量持久化实体，如果是UuidEntity的实体，如果没有主键将自动设置
     *
     * @param entities 实体对象集合
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static void batchPersist(Collection<?> entities) {
        for (Object entity : entities) {
            persist(entity);
        }
    }

    /**
     * 批量持久化实体(带有事务)，如果是UuidEntity的实体，如果没有主键将自动设置
     *
     * @param entities 实体对象集合
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public void batchPersistWithTx(final Collection<?> entities) {
        CoreBeanFactory.getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                batchPersist(entities);
            }
        });
    }

    /**
     * 合并实体
     *
     * @param entity 实体对象
     * @param <T>    实体类
     * @return 合并后新生成的实体对象
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T> T merge(T entity) {
        return getEntityMgr().merge(entity);
    }

    /**
     * 合并实体(带有事务)
     *
     * @param entity 实体对象
     * @param <T>    实体类
     * @return 合并后新生成的实体对象
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T> T mergeWithTx(final T entity) {
        final Single<T> result = new Single();
        CoreBeanFactory.getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                result.setValue(merge(entity));
            }
        });
        return result.getValue();
    }

    /**
     * 删除实体
     *
     * @param entity 实体
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static void remove(Object entity) {
        getEntityMgr().remove(entity);
    }

    /**
     * 删除实体(带有事务)
     *
     * @param entity 实体
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static void removeWithTx(final Object entity) {
        CoreBeanFactory.getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                remove(entity);
            }
        });
    }

    /**
     * 批量删除实体
     *
     * @param entities 实体集合
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static void batchRemove(Collection<?> entities) {
        for (Object entity : entities) {
            remove(entity);
        }
    }

    /**
     * 批量删除实体(带有事务)
     *
     * @param entities 实体集合
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static void batchRemoveWithTx(final Collection<?> entities) {
        CoreBeanFactory.getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                batchRemove(entities);
            }
        });
    }

    /**
     * 刷新实体管理器
     *
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static void flush() {
        getEntityMgr().flush();
    }

    /**
     * 刷新实体对象
     *
     * @param entity 实体对象
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static void refresh(Object entity) {
        getEntityMgr().refresh(entity);
    }

    /**
     * 通过主键加载实体对象
     *
     * @param entityClass 实体类
     * @param id          主键值
     * @return 实体对象
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T> T get(Class<T> entityClass, Object id) {
        return getEntityMgr().find(entityClass, id);
    }

    // /**
    // * 通过主键加载实体代理
    // *
    // * @param entityClass 实体类
    // * @param id 主键值
    // * @return 实体代理
    // */
    // public static <T> T load(Class<T> entityClass, Object id) {
    // return getEntityMgr().load(entityClass, id);
    // }

    /**
     * in查询
     *
     * @param entityClass 实体类
     * @param attr        jpa实体元模型属性
     * @param values      属性值集合
     * @return 指定类名对象的结果列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T, E> List<T> inSearch(final Class<T> entityClass, final SingularAttribute<? super T, E> attr, final Collection<E> values, final Order... orders) {
        return inSearch(entityClass, attr.getName(), values, (Order[]) orders);
    }

    /**
     * in查询
     *
     * @param entityClass 实体类
     * @param attr        属性
     * @param values      属性值集合
     * @param orders      排序规则
     * @return 指定类名对象的结果列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T> List<T> inSearch(final Class<T> entityClass, final String attr, Collection<?> values, final Order... orders) {
        if (values == null || values.isEmpty()) {
            return new ArrayList<T>(0);
        }
        Set<Object> set = new HashSet<Object>(values);
        final List<T> resultList = new ArrayList<T>(set.size());
        try {
            new GroupExecutor<Object>(set) {

                @Override
                public void groupExecute(List<Object> subList) {
                    CriteriaQuery<T> criteriaQuery = getCB().createQuery(entityClass);
                    Root<T> root = criteriaQuery.from(entityClass);
                    if (attr == null) {
                        criteriaQuery.where(root.get("id").in(subList));
                    } else {
                        criteriaQuery.where(root.get(attr).in(subList));
                    }
                    TypedQuery<T> typedQuery = getEntityMgr().createQuery(criteriaQuery);
                    resultList.addAll(typedQuery.getResultList());
                }

            }.execute();
        } catch (Exception e) {
            logger.error(e);
        }
        return resultList;
    }

    /**
     * in查询
     *
     * @param entityClass 实体类
     * @param values      属性值集合
     * @param orders      排序规则
     * @return 指定类名对象的结果列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T extends IEntity<?>, E> List<T> inSearch(final Class<T> entityClass, final Collection<E> values, final Order... orders) {
        return inSearch(entityClass, (String) null, values, orders);
    }

    // /**
    // * in查询, 查找主键
    // *
    // * @param clazz 实体类
    // * @param collection 属性值集合
    // * @return 指定类名对象的结果列表
    // */
    // @SuppressWarnings("rawtypes")
    // public static <T extends IEntity, E> List<T> inSearch(final Class<T>
    // clazz, final Collection<E> collection) {
    // return inSearch(clazz, "id", collection);
    // }

    /**
     * 查询指定类的所有结果
     *
     * @param entityClass 实体类
     * @param orders      排序规则
     * @return 指定类名对象的结果列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T> List<T> searchAll(final Class<T> entityClass, final Order... orders) {
        CriteriaQuery<T> criteriaQuery = getCB().createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        criteriaQuery.orderBy(processOrder(root, orders));
        TypedQuery<T> typedQuery = getEntityMgr().createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    // public static <T> List<T> searchAll(final Class<T> entityClass, final
    // Order... orders) {
    // return searchAll(entityClass, (Order[]) orders);
    // }

    /**
     * 根据类和单个属性查询
     *
     * @param entityClass 实体类
     * @param attr        jpa实体元模型属性
     * @param value       属性值
     * @param orders      排序规则
     * @return 指定类名对象的结果列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T, F> List<T> search(final Class<T> entityClass, final SingularAttribute<? super T, F> attr, final F value, final Order... orders) {
        return search(entityClass, attr.getName(), value, (Order[]) orders);
    }

    /**
     * 根据类和单个属性查询
     *
     * @param entityClass 实体类
     * @param attr        属性
     * @param value       属性值
     * @param orders      排序规则
     * @return 指定类名对象的结果列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T> List<T> search(Class<T> entityClass, String attr, Object value, Order... orders) {
        CriteriaQuery<T> criteriaQuery = getCB().createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        Predicate predicate;
        if (value == null) {
            predicate = getCB().isNull(root.get(attr));
        } else {
            predicate = getCB().equal(root.get(attr), value);
        }
        criteriaQuery.where(predicate);
        criteriaQuery.orderBy(processOrder(root, orders));
        TypedQuery<T> typedQuery = getEntityMgr().createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    /**
     * 分页查询
     *
     * @param entityClass 实体类
     * @param pageStore   PageStore
     * @param <T>         实体类
     * @return 实体对象列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T> List<T> pagingSearch(final Class<T> entityClass, PageStore pageStore) {
        CriteriaQuery<T> criteriaQuery = getCB().createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        QueryLogics queryLogics = pageStore.getQueryLogics();
        Predicate[] predicates = JpaQueryLogicConvertor.convert(getCB(), root, queryLogics);
        criteriaQuery.where(predicates);
        Order[] orders = pageStore.getQueryLogics().getOrderArray();
        criteriaQuery.orderBy(processOrder(root, orders));
        List<T> result = JpaPagingSupport.paging(getEntityMgr(), criteriaQuery, root, pageStore);
        pageStore.setResult(result);
        return result;
    }

    /**
     * 多个属性进行and查询
     *
     * @param entityClass 实体类
     * @param attrMap     Map<jpa实体元模型属性，属性值>
     * @param orders      排序规则
     * @return 指定类名对象的结果列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T, E> List<T> andQuery(final Class<T> entityClass, final Map<SingularAttribute<? super T, E>, E> attrMap, final Order... orders) {
        return andSearch(entityClass, convertMap(attrMap), (Order[]) orders);
    }

    /**
     * 多个属性进行and查询
     *
     * @param entityClass 实体类
     * @param attrMap     Map<属性名，属性值>
     * @param orders      排序规则
     * @return 指定类名对象的结果列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T> List<T> andSearch(final Class<T> entityClass, final Map<String, Object> attrMap, final Order... orders) {
        CriteriaQuery<T> criteriaQuery = getCB().createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        Predicate[] predicates = new Predicate[attrMap.size()];
        int idx = 0;
        for (Entry<String, Object> entry : attrMap.entrySet()) {
            predicates[idx] = getCB().equal(root.get(entry.getKey()), entry.getValue());
            idx++;
        }
        criteriaQuery.where(getCB().and(predicates));
        criteriaQuery.orderBy(processOrder(root, orders));
        TypedQuery<T> typedQuery = getEntityMgr().createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    /**
     * 多个属性查询进行or条件查询
     *
     * @param entityClass 实体类
     * @param attrMap     Map<jpa实体元模型属性，属性名>
     * @param orders      排序规则
     * @return 指定类名对象的结果列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T, E> List<T> orQuery(final Class<T> entityClass, final Map<SingularAttribute<? super T, E>, E> attrMap, final Order... orders) {
        return orSearch(entityClass, convertMap(attrMap), (Order[]) orders);
    }

    /**
     * 根据类和多个属性查询进行or条件查询
     *
     * @param entityClass 实体类
     * @param attrMap     Map<属性名，属性名>
     * @param orders      排序规则
     * @return 指定类名对象的结果列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static <T> List<T> orSearch(final Class<T> entityClass, final Map<String, Object> attrMap, final Order... orders) {
        CriteriaQuery<T> criteriaQuery = getCB().createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        Predicate[] predicates = new Predicate[attrMap.size()];
        int idx = 0;
        for (Map.Entry<String, Object> entry : attrMap.entrySet()) {
            predicates[idx] = getCB().equal(root.get(entry.getKey()), entry.getValue());
            idx++;
        }
        criteriaQuery.where(getCB().and(predicates));
        criteriaQuery.orderBy(processOrder(root, orders));
        TypedQuery<T> typedQuery = getEntityMgr().createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    // /**
    // * 查询指定类的所有结果，返回指定属性的值
    // *
    // * @param entityClass 实体类
    // * @param field 属性
    // * @param orders 排序规则
    // * @return List<Object>
    // */
    // @SuppressWarnings({ "rawtypes" })
    // public static <T, E> List search(final Class entityClass, final
    // SingularAttribute<T, E> field, final Order... orders) {
    // List<SingularAttribute<T, E>> fieldList = new
    // ArrayList<SingularAttribute<T, E>>(1);
    // fieldList.add(field);
    // return search(entityClass, fieldList, orders);
    // }
    //
    // /**
    // * 查询指定类的所有结果，返回指定属性集合的值
    // *
    // * @param clazz Hibernate模型的类
    // * @param fieldList 属性名称集合
    // * @param orders 排序规则
    // * @return 单个属性：List<Object>， 多个属性：List<Object[]<>>
    // */
    // @SuppressWarnings({ "rawtypes" })
    // public static <T, E> List search(final Class<T> entityClass, final
    // List<SingularAttribute<T, E>> fieldList, final Order... orders) {
    // CriteriaBuilder criteriaBuilder = getEntityMgr().getCriteriaBuilder();
    // CriteriaQuery<T> criteriaQuery =
    // criteriaBuilder.createQuery(entityClass);
    // Root<T> root = criteriaQuery.from(entityClass);
    //
    //
    // DetachedCriteria criteria = DetachedCriteria.forClass(clazz);
    // ProjectionList projectionList = Projections.projectionList();
    // for (String fieldName : fieldList) {
    // projectionList.add(Projections.property(fieldName));
    // }
    // criteria.setProjection(projectionList);
    // addOrder(criteria, orders);
    // return hibernateTemplate().findByCriteria(criteria);
    // }

    /**
     * 查询某序列的下一个值(ORACLE ONLY)
     *
     * @param sequence 序列名
     * @return 序列的下一个值
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static long querySequence(final String sequence) {
        List<?> resultList = findBySql("select " + sequence + ".nextval from dual");
        return ((BigDecimal) resultList.get(0)).longValue();
    }

    /**
     * 通过Sql语句查询
     *
     * @param sql sql查询语句
     * @return 查询结果列表
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static List<?> findBySql(final String sql) {
        Query query = getEntityMgr().createNativeQuery(sql);
        return query.getResultList();
    }

    private static <T, E> Map<String, Object> convertMap(Map<SingularAttribute<? super T, E>, E> attrMap) {
        Map<String, Object> map = new HashMap<String, Object>(attrMap.size());
        for (Entry<SingularAttribute<? super T, E>, E> entry : attrMap.entrySet()) {
            map.put(entry.getKey().getName(), entry.getValue());
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static final <T> List<javax.persistence.criteria.Order> processOrder(final Root<T> root, final Order... orders) {
        return getInstance().convertOrder(root, orders);
    }

    /**
     * 返回实体管理器
     *
     * @return 实体管理器
     * @author Kevice
     * @time 2012-6-26 下午10:36:28
     * @since 1.0.0
     */
    public static final EntityManager getEntityMgr() {
        return getInstance().em;
    }

    private static final CriteriaBuilder getCB() {
        return getEntityMgr().getCriteriaBuilder();
    }

}
