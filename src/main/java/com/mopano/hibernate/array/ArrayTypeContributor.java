/*
 * Copyright (c) Mak Ltd. Varna, Bulgaria
 * All rights reserved.
 *
 */
package com.mopano.hibernate.array;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.BinaryType;
import org.hibernate.type.BlobType;
import org.hibernate.type.BooleanType;
import org.hibernate.type.ByteType;
import org.hibernate.type.CalendarDateType;
import org.hibernate.type.CalendarType;
import org.hibernate.type.CharArrayType;
import org.hibernate.type.CharacterArrayType;
import org.hibernate.type.CharacterType;
import org.hibernate.type.ClassType;
import org.hibernate.type.ClobType;
import org.hibernate.type.CurrencyType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.DurationType;
import org.hibernate.type.FloatType;
import org.hibernate.type.ImageType;
import org.hibernate.type.InstantType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LocalDateTimeType;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.LocalTimeType;
import org.hibernate.type.LocaleType;
import org.hibernate.type.LongType;
import org.hibernate.type.MaterializedBlobType;
import org.hibernate.type.MaterializedClobType;
import org.hibernate.type.MaterializedNClobType;
import org.hibernate.type.NClobType;
import org.hibernate.type.NTextType;
import org.hibernate.type.NumericBooleanType;
import org.hibernate.type.OffsetDateTimeType;
import org.hibernate.type.OffsetTimeType;
import org.hibernate.type.SerializableType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringNVarcharType;
import org.hibernate.type.StringType;
import org.hibernate.type.TextType;
import org.hibernate.type.TimeType;
import org.hibernate.type.TimeZoneType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.TrueFalseType;
import org.hibernate.type.UUIDBinaryType;
import org.hibernate.type.UUIDCharType;
import org.hibernate.type.UrlType;
import org.hibernate.type.WrapperBinaryType;
import org.hibernate.type.YesNoType;
import org.hibernate.type.ZonedDateTimeType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;

import org.jboss.logging.Logger;

/**
 * TypeContributor for adding single-dimensional arrays
 *
 * @author Yordan Gigov
 */
public class ArrayTypeContributor implements TypeContributor {

	@Override
	public void contribute( TypeContributions typeContributions, ServiceRegistry serviceRegistry ) {

		Logger log = Logger.getLogger(getClass());

		ConfigurationService config = serviceRegistry.getService(ConfigurationService.class);

		final boolean replaceByteArrays = config.getSetting("hibernate.arrays.byte", StandardConverters.BOOLEAN, Boolean.FALSE);
		final boolean replaceCharArrays = config.getSetting("hibernate.arrays.char", StandardConverters.BOOLEAN, Boolean.FALSE);
		final boolean byteWrapArrays = config.getSetting("hibernate.arrays.bytewrap", StandardConverters.BOOLEAN, Boolean.FALSE);
		final boolean nationalString = config.getSetting("hibernate.arrays.national.string", StandardConverters.BOOLEAN, Boolean.FALSE);
		final boolean nationalText = config.getSetting("hibernate.arrays.national.text", StandardConverters.BOOLEAN, Boolean.FALSE);
		final boolean nationalClob = config.getSetting("hibernate.arrays.national.clob", StandardConverters.BOOLEAN, Boolean.FALSE);
		final boolean nationalMaterializedClob = config.getSetting("hibernate.arrays.national.materialized_clob", StandardConverters.BOOLEAN, Boolean.FALSE);

		log.debug("Creation of array type based on org.hibernate.type.ByteType: " + (replaceByteArrays ? "enabled" : "disabled"));
		log.debug("Creation of array type based on org.hibernate.type.CharacterType: " + (replaceCharArrays ? "enabled" : "disabled"));
		log.debug("Creation of array type based on org.hibernate.type.WrapperBinaryType: " + (byteWrapArrays ? "enabled" : "disabled"));
		log.debug("Creation of array type based on org.hibernate.type.NTextType: " + (nationalText ? "enabled" : "disabled"));
		log.debug("Creation of array type based on org.hibernate.type.NClobType: " + (nationalClob ? "enabled" : "disabled"));
		log.debug("Creation of array type based on org.hibernate.type.StringNVarcharType: " + (nationalString ? "enabled" : "disabled"));
		log.debug("Creation of array type based on org.hibernate.type.MaterializedNClobType: " + (nationalMaterializedClob ? "enabled" : "disabled"));

		ArrayTypes BOOLEAN = ArrayTypes.get(BooleanType.INSTANCE, serviceRegistry);
		ArrayTypes NUMERIC_BOOLEAN = ArrayTypes.get(NumericBooleanType.INSTANCE, serviceRegistry);
		ArrayTypes TRUE_FALSE = ArrayTypes.get(TrueFalseType.INSTANCE, serviceRegistry);
		ArrayTypes YES_NO = ArrayTypes.get(YesNoType.INSTANCE, serviceRegistry);
		ArrayTypes SHORT = ArrayTypes.get(ShortType.INSTANCE, serviceRegistry);
		ArrayTypes INTEGER = ArrayTypes.get(IntegerType.INSTANCE, serviceRegistry);
		ArrayTypes LONG = ArrayTypes.get(LongType.INSTANCE, serviceRegistry);
		ArrayTypes FLOAT = ArrayTypes.get(FloatType.INSTANCE, serviceRegistry);
		ArrayTypes DOUBLE = ArrayTypes.get(DoubleType.INSTANCE, serviceRegistry);
		ArrayTypes BIG_INTEGER = ArrayTypes.get(BigIntegerType.INSTANCE, serviceRegistry);
		ArrayTypes BIG_DECIMAL = ArrayTypes.get(BigDecimalType.INSTANCE, serviceRegistry);
		ArrayTypes STRING = ArrayTypes.get(StringType.INSTANCE, serviceRegistry);
		ArrayTypes URL = ArrayTypes.get(UrlType.INSTANCE, serviceRegistry);
		ArrayTypes TIME = ArrayTypes.get(TimeType.INSTANCE, serviceRegistry);
		ArrayTypes DATE = ArrayTypes.get(DateType.INSTANCE, serviceRegistry);
		ArrayTypes TIMESTAMP = ArrayTypes.get(TimestampType.INSTANCE, serviceRegistry);
		ArrayTypes CALENDAR = ArrayTypes.get(CalendarType.INSTANCE, serviceRegistry);
		ArrayTypes CALENDAR_DATE = ArrayTypes.get(CalendarDateType.INSTANCE, serviceRegistry);
		ArrayTypes CLASS = ArrayTypes.get(ClassType.INSTANCE, serviceRegistry);
		ArrayTypes LOCALE = ArrayTypes.get(LocaleType.INSTANCE, serviceRegistry);
		ArrayTypes CURRENCY = ArrayTypes.get(CurrencyType.INSTANCE, serviceRegistry);
		ArrayTypes TIMEZONE = ArrayTypes.get(TimeZoneType.INSTANCE, serviceRegistry);
		ArrayTypes UUID_BINARY = ArrayTypes.get(UUIDBinaryType.INSTANCE, serviceRegistry);
		ArrayTypes UUID_CHAR = ArrayTypes.get(UUIDCharType.INSTANCE, serviceRegistry);
		ArrayTypes BINARY = ArrayTypes.get(BinaryType.INSTANCE, serviceRegistry);
		ArrayTypes IMAGE = ArrayTypes.get(ImageType.INSTANCE, serviceRegistry);
		ArrayTypes BLOB = ArrayTypes.get(BlobType.INSTANCE, serviceRegistry);
		ArrayTypes MATERIALIZED_BLOB = ArrayTypes.get(MaterializedBlobType.INSTANCE, serviceRegistry);
		ArrayTypes CHAR_ARRAY = ArrayTypes.get(CharArrayType.INSTANCE, serviceRegistry);
		ArrayTypes CHARACTER_ARRAY = ArrayTypes.get(CharacterArrayType.INSTANCE, serviceRegistry);
		ArrayTypes TEXT = ArrayTypes.get(TextType.INSTANCE, serviceRegistry);
		ArrayTypes CLOB = ArrayTypes.get(ClobType.INSTANCE, serviceRegistry);
		ArrayTypes MATERIALIZED_CLOB = ArrayTypes.get(MaterializedClobType.INSTANCE, serviceRegistry );
		ArrayTypes SERIALIZABLE = ArrayTypes.get(SerializableType.INSTANCE, serviceRegistry );

		ArrayTypes INSTANT = ArrayTypes.get(InstantType.INSTANCE, serviceRegistry, java.sql.Timestamp.class );
		ArrayTypes DURATION = ArrayTypes.get(DurationType.INSTANCE, serviceRegistry, String.class );
		ArrayTypes LOCAL_DATE_TIME = ArrayTypes.get(LocalDateTimeType.INSTANCE, serviceRegistry, java.sql.Timestamp.class );
		ArrayTypes LOCAL_DATE = ArrayTypes.get(LocalDateType.INSTANCE, serviceRegistry, java.sql.Date.class );
		ArrayTypes LOCAL_TIME = ArrayTypes.get(LocalTimeType.INSTANCE, serviceRegistry, java.sql.Time.class );
		ArrayTypes ZONED_DATE_TIME = ArrayTypes.get(ZonedDateTimeType.INSTANCE, serviceRegistry, java.sql.Timestamp.class );
		ArrayTypes OFFSET_DATE_TIME = ArrayTypes.get(OffsetDateTimeType.INSTANCE, serviceRegistry, java.sql.Timestamp.class );
		ArrayTypes OFFSET_TIME = ArrayTypes.get(OffsetTimeType.INSTANCE, serviceRegistry, java.sql.Time.class );

		ArrayTypes BYTE = null;
		ArrayTypes CHARACTER = null;
		ArrayTypes WRAPPER_BINARY = null;
		ArrayTypes NTEXT = null;
		ArrayTypes NCLOB = null;
		ArrayTypes STRING_N_VARCHAR = null;
		ArrayTypes MATERIALIZED_NCLOB = null;

		if ( replaceByteArrays ) {
			BYTE = ArrayTypes.get(ByteType.INSTANCE, serviceRegistry);
		}
		if ( replaceCharArrays ) {
			CHARACTER = ArrayTypes.get(CharacterType.INSTANCE, serviceRegistry);
		}
		if ( byteWrapArrays ) {
			WRAPPER_BINARY = ArrayTypes.get(WrapperBinaryType.INSTANCE, serviceRegistry);
		}
		if ( nationalText ) {
			NTEXT = ArrayTypes.get(NTextType.INSTANCE, serviceRegistry );
		}
		if ( nationalClob ) {
			NCLOB = ArrayTypes.get(NClobType.INSTANCE, serviceRegistry );
		}
		if ( nationalString ) {
			STRING_N_VARCHAR = ArrayTypes.get(StringNVarcharType.INSTANCE, serviceRegistry );
		}
		if ( nationalMaterializedClob ) {
			MATERIALIZED_NCLOB = ArrayTypes.get(MaterializedNClobType.INSTANCE, serviceRegistry );
		}

		// Do we really need all these types?
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( BOOLEAN.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( NUMERIC_BOOLEAN.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( TRUE_FALSE.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( YES_NO.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( SHORT.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( INTEGER.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( LONG.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( FLOAT.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( DOUBLE.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( BIG_INTEGER.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( BIG_DECIMAL.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( STRING.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( URL.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( TIME.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( DATE.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( TIMESTAMP.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( CALENDAR.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( CALENDAR_DATE.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( CLASS.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( LOCALE.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( CURRENCY.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( TIMEZONE.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( UUID_BINARY.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( UUID_CHAR.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( BINARY.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( IMAGE.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( BLOB.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( MATERIALIZED_BLOB.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( CHAR_ARRAY.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( CHARACTER_ARRAY.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( TEXT.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( CLOB.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( MATERIALIZED_CLOB.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( SERIALIZABLE.getJavaTypeDescriptor() );

		// Java 8 time classes
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( INSTANT.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( DURATION.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( LOCAL_DATE_TIME.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( LOCAL_DATE.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( LOCAL_TIME.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( ZONED_DATE_TIME.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( OFFSET_DATE_TIME.getJavaTypeDescriptor() );
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( OFFSET_TIME.getJavaTypeDescriptor() );

		// could use the booleans, but that leaves the null-pointer warnings
		if ( BYTE != null ) {
			JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( BYTE.getJavaTypeDescriptor() );
		}
		if ( CHARACTER != null ) {
			JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( CHARACTER.getJavaTypeDescriptor() );
		}
		if ( WRAPPER_BINARY != null ) {
			JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( WRAPPER_BINARY.getJavaTypeDescriptor() );
		}
		if ( STRING_N_VARCHAR != null ) {
			JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( STRING_N_VARCHAR.getJavaTypeDescriptor() );
		}
		if ( NTEXT != null ) {
			JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( NTEXT.getJavaTypeDescriptor() );
		}
		if ( NCLOB != null ) {
			JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( NCLOB.getJavaTypeDescriptor() );
		}
		if ( MATERIALIZED_NCLOB != null ) {
			JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( MATERIALIZED_NCLOB.getJavaTypeDescriptor() );
		}

		// register the Hibernate type mappings
		typeContributions.contributeType( BOOLEAN );
		typeContributions.contributeType( NUMERIC_BOOLEAN );
		typeContributions.contributeType( TRUE_FALSE );
		typeContributions.contributeType( YES_NO );
		typeContributions.contributeType( SHORT );
		typeContributions.contributeType( INTEGER );
		typeContributions.contributeType( LONG );
		typeContributions.contributeType( FLOAT );
		typeContributions.contributeType( DOUBLE );
		typeContributions.contributeType( BIG_INTEGER );
		typeContributions.contributeType( BIG_DECIMAL );
		typeContributions.contributeType( STRING );
		typeContributions.contributeType( URL );
		typeContributions.contributeType( TIME );
		typeContributions.contributeType( DATE );
		typeContributions.contributeType( TIMESTAMP );
		typeContributions.contributeType( CALENDAR );
		typeContributions.contributeType( CALENDAR_DATE );
		typeContributions.contributeType( CLASS );
		typeContributions.contributeType( LOCALE );
		typeContributions.contributeType( CURRENCY );
		typeContributions.contributeType( TIMEZONE );
		typeContributions.contributeType( UUID_BINARY );
		typeContributions.contributeType( UUID_CHAR );
		typeContributions.contributeType( BINARY );
		typeContributions.contributeType( IMAGE );
		typeContributions.contributeType( BLOB );
		typeContributions.contributeType( MATERIALIZED_BLOB );
		typeContributions.contributeType( CHAR_ARRAY );
		typeContributions.contributeType( CHARACTER_ARRAY );
		typeContributions.contributeType( TEXT );
		typeContributions.contributeType( CLOB );
		typeContributions.contributeType( MATERIALIZED_CLOB );
		typeContributions.contributeType( SERIALIZABLE );

		// Java 8 time classes
		typeContributions.contributeType( INSTANT );
		typeContributions.contributeType( DURATION );
		typeContributions.contributeType( LOCAL_DATE_TIME );
		typeContributions.contributeType( LOCAL_DATE );
		typeContributions.contributeType( LOCAL_TIME );
		typeContributions.contributeType( ZONED_DATE_TIME );
		typeContributions.contributeType( OFFSET_DATE_TIME );
		typeContributions.contributeType( OFFSET_TIME );

		if ( BYTE != null ) {
			typeContributions.contributeType( BYTE );
		}
		if ( CHARACTER != null ) {
			typeContributions.contributeType( CHARACTER );
		}
		if ( WRAPPER_BINARY != null ) {
			typeContributions.contributeType( WRAPPER_BINARY );
		}
		if ( STRING_N_VARCHAR != null ) {
			typeContributions.contributeType( STRING_N_VARCHAR );
		}
		if ( NTEXT != null ) {
			typeContributions.contributeType( NTEXT );
		}
		if ( NCLOB != null ) {
			typeContributions.contributeType( NCLOB );
		}
		if ( MATERIALIZED_NCLOB != null ) {
			typeContributions.contributeType( MATERIALIZED_NCLOB );
		}

	}
}
