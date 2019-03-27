/*
 * Copyright (c) Mak Ltd. Varna, Bulgaria
 * All rights reserved.
 *
 */
package com.mopano.hibernate.test;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.hibernate.proxy.HibernateProxy;
import org.jboss.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayContributionTest {

	private static EntityManagerFactory emf;

	private static final Logger LOGGER = Logger.getLogger(ArrayContributionTest.class);

	@BeforeClass
	public static void setupJPA() {
		emf = Persistence.createEntityManagerFactory("com.mopano.hibernate");
	}

	@AfterClass
	public static void closeJPA() {
		emf.close();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testWriteRead() {

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		// test persist data
		try {
			MyEntity entity = new MyEntity();
			entity.id = 1l;
			entity.longs = new Long[]{ 55L, 12L, null, 616L };
			entity.strings = new String[]{ "512", "", null, "null" };
			entity.dates = new LocalDate[]{ null, LocalDate.ofEpochDay(0), LocalDate.of(2000, 1, 1), null};
			LOGGER.info("Persisting entity: " + entity);
			em.persist(entity);
			em.flush();
			entity = new MyEntity();
			entity.id = 2l;
			// leave nulls because postgres is being shitty with those
			LOGGER.info("Persisting entity: " + entity);
			em.persist(entity);
			em.flush();
			em.getTransaction().commit();
		}
		finally {
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}

		// test fetching
		em = emf.createEntityManager();
		em.getTransaction().begin();

		try {
			MyEntity entity1 = new MyEntity();
			entity1.id = 1l;
			entity1.longs = new Long[]{ 55L, 12L, null, 616L };
			entity1.strings = new String[]{ "512", "", null, "null" };
			entity1.dates = new LocalDate[]{ null, LocalDate.ofEpochDay(0), LocalDate.of(2000, 1, 1), null };
			MyEntity entity2 = new MyEntity();
			entity2.id = 2l;
			MyEntity me1 = em.find(MyEntity.class, new Long(1));
			LOGGER.info("Extracted entity: " + me1);
			MyEntity me2 = em.find(MyEntity.class, new Long(2));
			LOGGER.info("Extracted entity: " + me2);
			assertEquals(entity1, me1);
			assertEquals(entity2, me2);
		}
		finally {
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}

		// test binding parameters
		em = emf.createEntityManager();
		em.getTransaction().begin();

		try {
			Long[] longs = new Long[]{ 55L, 12L, null, 616L };
			LocalDate[] dates = new LocalDate[]{ LocalDate.of(2000, 1, 1) };
			LOGGER.info("Testing long array parameter in non-native query");
			List<MyEntity> ent = em.createQuery("SELECT e FROM MyEntity e WHERE e.longs = :l", MyEntity.class)
					.setParameter("l", longs)
					.getResultList();
			assertEquals(ent.size(), 1);
			// cannot bind nulls on arrays in native queries in PostgreSQL
			// because it checks types before checking for null
			// the PG team does not consider this a fault
			LOGGER.info("Testing LocalDate array parameter in native query");
			ent = em.createNativeQuery("SELECT * FROM array_entity WHERE dates && :dt", MyEntity.class)
					.setParameter("dt", dates)
					.getResultList();
			assertEquals(ent.size(), 1);
		}
		finally {
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}

	}

	@Entity(name = "MyEntity")
	@Table(name = "array_entity")
	@SuppressWarnings("PersistenceUnitPresent")
	public static class MyEntity implements Serializable {

		private static final long serialVersionUID = 1L;

		@Id
		public Long id;
		@Column(columnDefinition = "bigint array")
		public Long[] longs;
		@Column(columnDefinition = "varchar array")
		public String[] strings;
		@Column(columnDefinition = "date array")
		public LocalDate[] dates;

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof MyEntity)) {
				return false;
			}
			MyEntity that = (MyEntity) other;
			if (this instanceof HibernateProxy) {
				((HibernateProxy) this).getHibernateLazyInitializer().initialize();
			}
			if (that instanceof HibernateProxy) {
				((HibernateProxy) that).getHibernateLazyInitializer().initialize();
			}
			// assuming (possibly incorrectly) that the implementations take care of their equals() properly
			return Objects.equals(this.id, that.id)
					&& Arrays.equals(this.longs, that.longs)
					&& Arrays.equals(this.strings, that.strings)
					&& Arrays.equals(this.dates, that.dates);
		}

		@Override
		public int hashCode() {
			if (this instanceof HibernateProxy) {
				((HibernateProxy) this).getHibernateLazyInitializer().initialize();
			}
			return Objects.hash(id, Arrays.hashCode(longs), Arrays.hashCode(strings), Arrays.hashCode(dates));
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append('{')
					.append("id = ")
					.append(id)
					.append(", longs = ");

			if (longs == null) {
				sb.append((String) null);
			}
			else {
				sb.append('[');
				for (Long l : longs) {
					// faster, because it sb.append(Long) allocates another internal string and discards it
					if (l == null) {
						sb.append((String) null).append(", ");
					}
					else {
						sb.append(l.longValue()).append(", ");
					}
				}
				sb.setLength(sb.length() - 2);
				sb.append(']');
			}

			sb.append(", strings = ");
			if (strings == null) {
				sb.append((String) null);
			}
			else {
				sb.append('[');
				for (String l : strings) {
					// faster, because it sb.append(Long) allocates another internal string and discards it
					if (l == null) {
						sb.append((String) null).append(", ");
					}
					else {
						sb.append('"');
						final int len = l.length();
						for (int i = 0; i < len; i++) {
							char c = l.charAt(i);
							switch (c) {
								case '\n':
									sb.append("\\n");
									break;

								case '\r':
									sb.append("\\r");
									break;

								case '\t':
									sb.append("\\t");
									break;

								case '\b':
									sb.append("\\b");
									break;

								case '\f':
									sb.append("\\f");
									break;

								case '\\':
								case '"':
									sb.append('\\');
									// fallthrough
								default:
									sb.append(c);
							}
						}
						sb.append("\", ");
					}
				}
				sb.setLength(sb.length() - 2);
				sb.append(']');
			}

			sb.append(", dates = ");
			if (dates == null) {
				sb.append((String) null);
			}
			else {
				sb.append('[');
				for (LocalDate l : dates) {
					sb.append(l).append(", ");
				}
				sb.setLength(sb.length() - 2);
				sb.append(']');
			}
			sb.append('}');

			return sb.toString();
		}
	}
}
