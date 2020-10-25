/*
 * Copyright (c) Mak-Si Management Ltd. Varna, Bulgaria
 *
 * License: BSD 3-Clause license.
 * See the LICENSE.md file in the root directory or <https://opensource.org/licenses/BSD-3-Clause>.
 * See also <https://tldrlegal.com/license/bsd-3-clause-license-(revised)>.
 */
package com.mopano.hibernate.test;

import java.io.Serializable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

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
	public void testWriteRead() throws MalformedURLException {

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		// test persist data
		try {
			MyEntity entity = new MyEntity();
			entity.id = 1l;
			entity.longs = new Long[]{ 55L, 12L, null, 616L };
			entity.strings = new String[]{ "512", "", null, "null" };
			entity.dates = new LocalDate[]{ null, LocalDate.ofEpochDay(0), LocalDate.of(2000, 1, 1), null};
			entity.bigdecimals = new BigDecimal[] {BigDecimal.TEN, BigDecimal.valueOf(14000000L), new BigDecimal("444444444.4444444442111")};
			entity.bigints = new BigInteger[] {BigInteger.TEN, BigInteger.valueOf(14000000L), new BigInteger("4444444444444444442111")};
			entity.bools = new Boolean[] {Boolean.FALSE, Boolean.FALSE, Boolean.TRUE};
			entity.currencies = new Currency[] {Currency.getInstance("EUR"), Currency.getInstance("USD"), Currency.getInstance("PHP")};
			entity.doubles = new Double[]{33.5, 13.0, 0.0};
			entity.instants = new Instant[]{Instant.EPOCH, Instant.ofEpochMilli(1500000000)};
			entity.ints = new Integer[]{41, 2, -2};
			entity.localdatetimes = new LocalDateTime[]{LocalDateTime.of(2020, Month.MARCH, 3, 12, 0), LocalDateTime.of(2020, 6, 3, 12, 0)};
			entity.localtimes = new LocalTime[]{LocalTime.MIN, LocalTime.NOON};
			entity.offsetdatetimes = new OffsetDateTime[]{OffsetDateTime.of(LocalDateTime.of(2020, 3, 3, 12, 0), ZoneOffset.ofHours(1))};
			entity.reals = new Float[]{30.0f, 12.5f, -2.0f};
			entity.shorts = new Short[]{2, 33, -3};
			entity.uuids = new UUID[]{UUID.fromString("bb6afa0a-0160-4de7-be14-708bea53ae51"), UUID.fromString("f80715a0-4ab9-4fdf-bc47-db62eb37afb7"), UUID.fromString("8a0fc972-4705-4607-b724-c559b173f61d")};
			entity.zoneddatetimes = new ZonedDateTime[]{ZonedDateTime.of(LocalDateTime.of(2020, 3, 3, 12, 0), ZoneId.of("Europe/Berlin"))};
			entity.urls = new URL[]{new URL("https://github.com"), new URL("https://www.hibernate.org")};
			entity.locales = new Locale[]{Locale.GERMANY, Locale.UK, Locale.US};
			entity.offsettimes = new OffsetTime[]{OffsetTime.of(LocalTime.NOON, ZoneOffset.UTC), OffsetTime.of(LocalTime.NOON, ZoneOffset.ofHours(-7))};
			entity.durations = new Duration[] {Duration.ofHours(36)};
			entity.classes = new Class[] {MyEntity.class, UUID.class};
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
			entity1.bigdecimals = new BigDecimal[] {BigDecimal.TEN, BigDecimal.valueOf(14000000L), new BigDecimal("444444444.4444444442111")};
			entity1.bigints = new BigInteger[] {BigInteger.TEN, BigInteger.valueOf(14000000L), new BigInteger("4444444444444444442111")};
			entity1.bools = new Boolean[] {Boolean.FALSE, Boolean.FALSE, Boolean.TRUE};
			entity1.currencies = new Currency[] {Currency.getInstance("EUR"), Currency.getInstance("USD"), Currency.getInstance("PHP")};
			entity1.doubles = new Double[]{33.5, 13.0, 0.0};
			entity1.instants = new Instant[]{Instant.EPOCH, Instant.ofEpochMilli(1500000000)};
			entity1.ints = new Integer[]{41, 2, -2};
			entity1.localdatetimes = new LocalDateTime[]{LocalDateTime.of(2020, Month.MARCH, 3, 12, 0), LocalDateTime.of(2020, 6, 3, 12, 0)};
			entity1.localtimes = new LocalTime[]{LocalTime.MIN, LocalTime.NOON};
			entity1.offsetdatetimes = new OffsetDateTime[]{OffsetDateTime.of(LocalDateTime.of(2020, 3, 3, 12, 0), ZoneOffset.ofHours(1))};
			entity1.reals = new Float[]{30.0f, 12.5f, -2.0f};
			entity1.shorts = new Short[]{2, 33, -3};
			entity1.uuids = new UUID[]{UUID.fromString("bb6afa0a-0160-4de7-be14-708bea53ae51"), UUID.fromString("f80715a0-4ab9-4fdf-bc47-db62eb37afb7"), UUID.fromString("8a0fc972-4705-4607-b724-c559b173f61d")};
			entity1.zoneddatetimes = new ZonedDateTime[]{ZonedDateTime.of(LocalDateTime.of(2020, 3, 3, 12, 0), ZoneId.of("Europe/Berlin"))};
			entity1.urls = new URL[]{new URL("https://github.com"), new URL("https://www.hibernate.org")};
			entity1.locales = new Locale[]{Locale.GERMANY, Locale.UK, Locale.US};
			entity1.offsettimes = new OffsetTime[]{OffsetTime.of(LocalTime.NOON, ZoneOffset.UTC), OffsetTime.of(LocalTime.NOON, ZoneOffset.ofHours(-7))};
			entity1.durations = new Duration[] {Duration.ofHours(36)};
			entity1.classes = new Class[] {MyEntity.class, UUID.class};
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
		@Column(columnDefinition = "uuid array")
		public UUID[] uuids;
		@Column(columnDefinition = "boolean array")
		public Boolean[] bools;
		@Column(columnDefinition = "smallint array")
		public Short[] shorts;
		@Column(columnDefinition = "integer array")
		public Integer[] ints;
		@Column(columnDefinition = "real array")
		public Float[] reals;
		@Column(columnDefinition = "double precision array")
		public Double[] doubles;
		@Column(columnDefinition = "numeric array")
		public BigInteger[] bigints;
		@Column(columnDefinition = "numeric array")
		public BigDecimal[] bigdecimals;
		@Column(columnDefinition = "varchar array")
		public Currency[] currencies;
		@Column(columnDefinition = "timestamp with time zone array")
		public Instant[] instants;
		@Column(columnDefinition = "timestamp without time zone array")
		public LocalDateTime[] localdatetimes;
		@Column(columnDefinition = "timestamp with time zone array")
		public ZonedDateTime[] zoneddatetimes;
		@Column(columnDefinition = "timestamp with time zone array")
		public OffsetDateTime[] offsetdatetimes;
		@Column(columnDefinition = "time without time zone array")
		public LocalTime[] localtimes;
		@Column(columnDefinition = "text array")
		public URL[] urls;
		@Column(columnDefinition = "varchar array")
		public Locale[] locales;
		@Column(columnDefinition = "timetz array")
		public OffsetTime[] offsettimes;
		@Column(columnDefinition = "bigint array")
		public Duration[] durations;
		@Column(columnDefinition = "varchar array")
		public Class[] classes;

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
			// equals will require the same timezone, where as when it's extracted from the database, the timezone offset is lost
			if (this.zoneddatetimes != null && that.zoneddatetimes != null) {
				if (this.zoneddatetimes.length != that.zoneddatetimes.length) {
					LOGGER.error("zoneddatetimes differ " + Arrays.toString(this.zoneddatetimes) + " != " + Arrays.toString(that.zoneddatetimes));
					return false;
				}
				for (int i = 0, l = this.zoneddatetimes.length; i < l; i++) {
					ZonedDateTime thisTime = this.zoneddatetimes[i];
					ZonedDateTime thatTime = that.zoneddatetimes[i];
					if (thisTime != null && thatTime != null) {
						if (!thisTime.isEqual(thatTime)) {
							LOGGER.error("zoneddatetimes differ " + Arrays.toString(this.zoneddatetimes) + " != " + Arrays.toString(that.zoneddatetimes));
							return false;
						}
					}
					else if (thisTime == null && thatTime == null) {
						// continue to next
					}
					else {
						LOGGER.error("zoneddatetimes differ " + Arrays.toString(this.zoneddatetimes) + " != " + Arrays.toString(that.zoneddatetimes));
						return false;
					}
				}
			}
			// equals will require the same timezone, where as when it's extracted from the database, the timezone offset is lost
			if (this.offsetdatetimes != null && that.offsetdatetimes != null) {
				if (this.offsetdatetimes.length != that.offsetdatetimes.length) {
					LOGGER.error("offsetdatetimes differ " + Arrays.toString(this.offsetdatetimes) + " != " + Arrays.toString(that.offsetdatetimes));
					return false;
				}
				for (int i = 0, l = this.offsetdatetimes.length; i < l; i++) {
					OffsetDateTime thisTime = this.offsetdatetimes[i];
					OffsetDateTime thatTime = that.offsetdatetimes[i];
					if (thisTime != null && thatTime != null) {
						if (!thisTime.isEqual(thatTime)) {
							LOGGER.error("offsetdatetimes differ " + Arrays.toString(this.offsetdatetimes) + " != " + Arrays.toString(that.offsetdatetimes));
							return false;
						}
					}
					else if (thisTime == null && thatTime == null) {
						// continue to next
					}
					else {
						LOGGER.error("offsetdatetimes differ " + Arrays.toString(this.offsetdatetimes) + " != " + Arrays.toString(that.offsetdatetimes));
						return false;
					}
				}
			}
			// equals will require the same timezone, where as when it's extracted from the database, the timezone offset is lost
			if (this.offsettimes != null && that.offsettimes != null) {
				if (this.offsettimes.length != that.offsettimes.length) {
					LOGGER.error("offsetdatetimes differ " + Arrays.toString(this.offsettimes) + " != " + Arrays.toString(that.offsettimes));
					return false;
				}
				for (int i = 0, l = this.offsettimes.length; i < l; i++) {
					OffsetTime thisTime = this.offsettimes[i];
					OffsetTime thatTime = that.offsettimes[i];
					if (thisTime != null && thatTime != null) {
						if (!thisTime.isEqual(thatTime)) {
							LOGGER.error("offsettimes differ " + Arrays.toString(this.offsettimes) + " != " + Arrays.toString(that.offsettimes));
							return false;
						}
					}
					else if (thisTime == null && thatTime == null) {
						// continue to next
					}
					else {
						LOGGER.error("offsettimes differ " + Arrays.toString(this.offsettimes) + " != " + Arrays.toString(that.offsettimes));
						return false;
					}
				}
			}
			if (!Arrays.equals(this.longs, that.longs)) {
				LOGGER.error("longs differ " + Arrays.toString(this.longs) + " != " + Arrays.toString(that.longs));
				return false;
			}
			if (!Arrays.equals(this.strings, that.strings)) {
				LOGGER.error("strings differ " + Arrays.toString(this.strings) + " != " + Arrays.toString(that.strings));
				return false;
			}
			if (!Arrays.equals(this.uuids, that.uuids)) {
				LOGGER.error("uuids differ " + Arrays.toString(this.uuids) + " != " + Arrays.toString(that.uuids));
				return false;
			}
			if (!Arrays.equals(this.bools, that.bools)) {
				LOGGER.error("bools differ " + Arrays.toString(this.bools) + " != " + Arrays.toString(that.bools));
				return false;
			}
			if (!Arrays.equals(this.shorts, that.shorts)) {
				LOGGER.error("shorts differ " + Arrays.toString(this.shorts) + " != " + Arrays.toString(that.shorts));
				return false;
			}
			if (!Arrays.equals(this.ints, that.ints)) {
				LOGGER.error("ints differ " + Arrays.toString(this.ints) + " != " + Arrays.toString(that.ints));
				return false;
			}
			if (!Arrays.equals(this.doubles, that.doubles)) {
				LOGGER.error("doubles differ " + Arrays.toString(this.doubles) + " != " + Arrays.toString(that.doubles));
				return false;
			}
			if (!Arrays.equals(this.bigints, that.bigints)) {
				LOGGER.error("bigints differ " + Arrays.toString(this.bigints) + " != " + Arrays.toString(that.bigints));
				return false;
			}
			if (!Arrays.equals(this.bigdecimals, that.bigdecimals)) {
				LOGGER.error("bigdecimals differ " + Arrays.toString(this.bigdecimals) + " != " + Arrays.toString(that.bigdecimals));
				return false;
			}
			if (!Arrays.equals(this.currencies, that.currencies)) {
				LOGGER.error("currencies differ " + Arrays.toString(this.currencies) + " != " + Arrays.toString(that.currencies));
				return false;
			}
			if (!Arrays.equals(this.instants, that.instants)) {
				LOGGER.error("instants differ " + Arrays.toString(this.instants) + " != " + Arrays.toString(that.instants));
				return false;
			}
			if (!Arrays.equals(this.localdatetimes, that.localdatetimes)) {
				LOGGER.error("localdatetimes differ " + Arrays.toString(this.localdatetimes) + " != " + Arrays.toString(that.localdatetimes));
				return false;
			}
			if (!Arrays.equals(this.dates, that.dates)) {
				LOGGER.error("dates differ " + Arrays.toString(this.dates) + " != " + Arrays.toString(that.dates));
				return false;
			}
			if (!Arrays.equals(this.urls, that.urls)) {
				LOGGER.error("urls differ " + Arrays.toString(this.urls) + " != " + Arrays.toString(that.urls));
				return false;
			}
			if (!Arrays.equals(this.locales, that.locales)) {
				LOGGER.error("locales differ " + Arrays.toString(this.locales) + " != " + Arrays.toString(that.locales));
				return false;
			}
			return Objects.equals(this.id, that.id);
		}

		@Override
		public int hashCode() {
			if (this instanceof HibernateProxy) {
				((HibernateProxy) this).getHibernateLazyInitializer().initialize();
			}
			return Objects.hash(id,
					Arrays.hashCode(longs),
					Arrays.hashCode(strings),
					Arrays.hashCode(dates),
					Arrays.hashCode(uuids),
					Arrays.hashCode(bools),
					Arrays.hashCode(shorts),
					Arrays.hashCode(ints),
					Arrays.hashCode(doubles),
					Arrays.hashCode(bigints),
					Arrays.hashCode(bigdecimals),
					Arrays.hashCode(currencies),
					Arrays.hashCode(instants),
					Arrays.hashCode(localdatetimes),
					Arrays.hashCode(localtimes),
					Arrays.hashCode(zoneddatetimes),
					Arrays.hashCode(offsetdatetimes),
					Arrays.hashCode(offsettimes),
					Arrays.hashCode(durations),
					Arrays.hashCode(classes),
					Arrays.hashCode(urls),
					Arrays.hashCode(locales)
			);
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
