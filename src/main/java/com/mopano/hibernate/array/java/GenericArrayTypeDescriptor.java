/*
 * Copyright (c) Mak-Si Management Ltd. Varna, Bulgaria
 *
 * License: BSD 3-Clause license.
 * See the LICENSE.md file in the root directory or <https://opensource.org/licenses/BSD-3-Clause>.
 * See also <https://tldrlegal.com/license/bsd-3-clause-license-(revised)>.
 */
package com.mopano.hibernate.array.java;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.SessionImpl;
import org.hibernate.type.AbstractStandardBasicType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ArrayMutabilityPlan;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.MutabilityPlan;

import org.jboss.logging.Logger;

/**
 * @author Yordan Gigov
 */
public class GenericArrayTypeDescriptor<T> extends AbstractTypeDescriptor<T[]> {

	private static final Logger LOGGER = Logger.getLogger(GenericArrayTypeDescriptor.class);
	private final JavaTypeDescriptor<T> componentDescriptor;
	private final Class<T> componentClass;
	private final MutabilityPlan<T[]> mutaplan;
	private final int sqlType;
	private final String sqlTypeName;
	private final Class unwrapTo;
	private final boolean skipTypeLookup;

	public GenericArrayTypeDescriptor(AbstractStandardBasicType<T> baseDescriptor) {
		this( baseDescriptor, null );
	}

	@SuppressWarnings("unchecked")
	public GenericArrayTypeDescriptor(AbstractStandardBasicType<T> baseDescriptor, Class unwrapTo) {
		super( (Class<T[]>) Array.newInstance( baseDescriptor.getJavaTypeDescriptor().getJavaTypeClass(), 0 ).getClass() );
		this.componentDescriptor = baseDescriptor.getJavaTypeDescriptor();
		this.componentClass = baseDescriptor.getJavaTypeDescriptor().getJavaTypeClass();
		if ( this.componentClass.isArray() ) {
			this.mutaplan = new LocalArrayMutabilityPlan( this.componentDescriptor.getMutabilityPlan() );
		}
		else {
			this.mutaplan = ArrayMutabilityPlan.INSTANCE;
		}
		this.sqlType = baseDescriptor.getSqlTypeDescriptor().getSqlType();
		this.unwrapTo = unwrapTo == null ? componentClass : unwrapTo;
		String typeName = baseDescriptor.getName();
		while (typeName.endsWith("[]") || typeName.endsWith(" array")) {
			if (typeName.endsWith("[]")) {
				typeName = typeName.substring(0, typeName.length() - 2).trim();
			}
			else {
				typeName = typeName.substring(0, typeName.length() - 6).trim();
			}
		}
		boolean skipTypeLookup = false;
		if (typeName.equals("pg-uuid")) {
			typeName = "uuid";
			skipTypeLookup = true;
		}
		else if (componentClass == java.time.OffsetTime.class) {
			typeName = "timetz";
			skipTypeLookup = true;
		}
		this.sqlTypeName = typeName;
		this.skipTypeLookup = skipTypeLookup;
		LOGGER.tracef("Created GenericArrayTypeDescriptor(sqlType = %d, sqlTypeName = %s, componentClass = %s, unwrapto = %s)",
				this.sqlType, this.sqlTypeName, this.componentClass.getName(), this.unwrapTo.getName());
	}

	@SuppressWarnings("unchecked")
	private class LocalArrayMutabilityPlan implements MutabilityPlan<T[]> {

		MutabilityPlan<T> superplan;

		public LocalArrayMutabilityPlan(MutabilityPlan<T> superplan) {
			this.superplan = superplan;
		}

		@Override
		public boolean isMutable() {
			return true;
		}

		@Override
		public T[] deepCopy(T[] value) {
			if ( value == null ) {
				return null;
			}
			T[] copy = (T[]) Array.newInstance( componentClass, value.length );
			for ( int i = 0; i < value.length; i ++ ) {
				copy[ i ] = superplan.deepCopy( value[ i ] );
			}
			return copy;
		}

		@Override
		public Serializable disassemble(T[] value) {
			return (Serializable) deepCopy( value );
		}

		@Override
		public T[] assemble(Serializable cached) {
			return deepCopy( (T[]) cached );
		}

	}

	@Override
	public boolean areEqual(T[] one, T[] another) {
		if (one == null && another == null) {
			return true;
		}
		if (one == null || another == null) {
			return false;
		}
		if (one.length != another.length) {
			return false;
		}

		final int l = one.length;
		for (int i = 0; i < l; i++) {
			if ( ! componentDescriptor.areEqual( one[i], another[i] )) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int extractHashCode(T[] value) {
		return java.util.Arrays.hashCode( value );
	}

	@Override
	public MutabilityPlan<T[]> getMutabilityPlan() {
		return this.mutaplan;
	}

	@Override
	public String toString(T[] value) {
		if ( value == null ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append( '{' );
		String glue = "";
		for ( T v : value ) {
			sb.append( glue );
			if ( v == null ) {
				sb.append( "null" );
				glue = ",";
				continue;
			}
			sb.append( '"' );
			String valstr = this.componentDescriptor.toString( v );
			// using replaceAll is a shorter, but much slower way to do this
			for (int i = 0, len = valstr.length(); i < len; i ++ ) {
				char c = valstr.charAt( i );
				// Surrogate pairs. This is how they're done.
				if (c == '\\' || c == '"') {
					sb.append( '\\' );
				}
				sb.append( c );
			}
			sb.append( '"' );
			glue = ",";
		}
		sb.append( '}' );
		String result = sb.toString();
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T[] fromString(String string) {
		if ( string == null ) {
			return null;
		}
		java.util.ArrayList<String> lst = new java.util.ArrayList<>();
		string = string.trim();
		StringBuilder sb = null;
		char lastchar = string.charAt( string.length() - 1 );
		char firstchar = string.charAt( 0 );
		if ( firstchar != '{' || lastchar != '}' ) {
			throw new IllegalArgumentException( "Cannot parse given string into array of strings. First and last character must be { and }" );
		}
		int len = string.length();
		boolean inquote = false;
		for ( int i = 1; i < len; i ++ ) {
			char c = string.charAt( i );
			if ( c == '"' ) {
				if (inquote) {
					lst.add( sb.toString() );
				}
				else {
					sb = new StringBuilder();
				}
				inquote = !inquote;
				continue;
			}
			else if ( !inquote ) {
				if ( Character.isWhitespace( c ) ) {
					continue;
				}
				else if ( c == ',' ) {
					// treat no-value between commas to mean null
					if ( sb == null ) {
						lst.add( null );
					}
					else {
						sb = null;
					}
					continue;
				}
				else {
					// i + 4, because there has to be a comma or closing brace after null
					if ( i + 4 < len
							&& string.charAt( i ) == 'n'
							&& string.charAt( i + 1 ) == 'u'
							&& string.charAt( i + 2 ) == 'l'
							&& string.charAt( i + 3 ) == 'l') {
						lst.add( null );
						i += 4;
						continue;
					}
					if (i + 1 == len) {
						break;
					}
					throw new IllegalArgumentException( "Cannot parse given string into array of strings."
							+ " Outside of quote, but neither whitespace, comma, array end, nor null found." );
				}
			}
			else if ( c == '\\' && i + 2 < len && (string.charAt( i + 1 ) == '\\' || string.charAt( i + 1 ) == '"')) {
				c = string.charAt( ++i );
			}
			// If there is ever a null-pointer here, the if-else logic before is incomplete
			sb.append( c );
		}
		String[] objects = lst.toArray( new String[lst.size()] );
		T[] result = (T[]) Array.newInstance( componentClass, lst.size() );
		for ( int i = 0; i < result.length; i ++ ) {
			result[ i ] = componentDescriptor.fromString( objects[ i ] );
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <X> X unwrap(T[] value, Class<X> type, WrapperOptions options) {
		// function used for PreparedStatement binding

		if ( value == null ) {
			return null;
		}

		if ( java.sql.Array.class.isAssignableFrom( type ) ) {
			Dialect sqlDialect;
			Connection conn;
			if (  ! ( options instanceof SessionImpl ) ) {
				throw new IllegalStateException( "You can't handle the truth! I mean arrays..." );
			}
			SessionImpl sess = (SessionImpl) options;
			sqlDialect = sess.getJdbcServices().getDialect();
			Object[] unwrapped = new Object[value.length];
			Class cls = value.getClass().getComponentType();
			for (int i = 0; i < value.length; i++) {
				unwrapped[i] = unwrapTo.isAssignableFrom( cls )
						? value[i]
						: componentDescriptor.unwrap( value[i], unwrapTo, options );
			}
			try {
				conn = sess.connection();
				String typeName;
				if ( skipTypeLookup ) {
					typeName = sqlTypeName;
				}
				else {
					switch (sqlType) {
						case Types.OTHER:
							typeName = sqlTypeName;
							break;
						case Types.SQLXML:
							typeName = "xml";
							break;
						default:
							typeName = sqlDialect.getTypeName( sqlType );
							LOGGER.tracef("Dialect getTypeName %d returned %s", sqlType, typeName);
							break;
					}
					int cutIndex = typeName.indexOf( '(' );
					if ( cutIndex > 0 ) {
						// getTypeName for this case required length, etc, parameters.
						// Cut them out and use database defaults.
						typeName = typeName.substring( 0, cutIndex );
					}
				}
				return (X) conn.createArrayOf( typeName, unwrapped );
			}
			catch ( SQLException ex ) {
				// This basically shouldn't happen unless you've lost connection to the database.
				// Or the JDBC driver can't use the type you need.
				throw new HibernateException( ex );
			}
		}

		throw unknownUnwrap( type );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <X> T[] wrap(X value, WrapperOptions options) {
		// function used for ResultSet extraction

		if ( value == null ) {
			return null;
		}

		Class cls = value.getClass();

		if ( cls.isArray() ) {
			if ( componentClass.isAssignableFrom( cls.getComponentType() ) ) {
				Object[] raw = (Object[]) value;
				T[] wrapped = (T[]) java.lang.reflect.Array.newInstance( componentClass, raw.length );
				for (int i = 0; i < raw.length; i++) {
					wrapped[i] = (T) raw[i];
				}
				return wrapped;
			}
			Object[] raw = (Object[]) value;
			T[] wrapped = (T[]) java.lang.reflect.Array.newInstance( componentClass, raw.length );
			for (int i = 0; i < raw.length; i++) {
				wrapped[i] = componentDescriptor.wrap( raw[i], options );
			}
			return wrapped;
		}

		if (  ! ( value instanceof java.sql.Array ) ) {
			throw unknownWrap( value.getClass() );
		}

		java.sql.Array original = (java.sql.Array) value;
		try {
			Object[] raw = (Object[]) original.getArray();
			if (raw == null) {
				return null;
			}
			T[] wrapped = (T[]) java.lang.reflect.Array.newInstance( componentClass, raw.length );
			for (int i = 0; i < raw.length; i++) {
				wrapped[i] = componentDescriptor.wrap( raw[i], options );
			}
			return (T[]) wrapped;
		}
		catch ( SQLException ex ) {
			// This basically shouldn't happen unless you've lost connection to the database.
			// Or the JDBC driver can't use the type you need.
			throw new HibernateException( ex );
		}
	}
}
