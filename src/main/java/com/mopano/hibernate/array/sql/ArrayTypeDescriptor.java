/*
 * Copyright (c) Mak Ltd. Varna, Bulgaria
 * All rights reserved.
 *
 */
package com.mopano.hibernate.array.sql;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

/**
 *
 * @author Yordan Gigov
 */
public class ArrayTypeDescriptor implements SqlTypeDescriptor {

	private static final long serialVersionUID = 1L;

	public static final ArrayTypeDescriptor INSTANCE = new ArrayTypeDescriptor();

	public ArrayTypeDescriptor() {
	}

	@Override
	public int getSqlType() {
		return Types.ARRAY;
	}

	@Override
	public boolean canBeRemapped() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked") 
	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
		return new BasicBinder<X>( javaTypeDescriptor, this ) {

			@Override
			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
				final java.sql.Array arr = javaTypeDescriptor.unwrap( value, java.sql.Array.class, options );
				st.setArray( index, arr );
			}

			private Method nameBinder;
			private boolean checkedBinder;

			@Override
			protected void doBind(CallableStatement st, X value, String name, WrapperOptions options)
					throws SQLException {
				if (  ! checkedBinder ) {
					Class clz = st.getClass();
					try {
						nameBinder = clz.getMethod( "setArray", String.class, java.sql.Array.class );
					}
					catch ( Exception ex ) {
						// add logging? Did we get NoSuchMethodException or SecurityException?
						// Doesn't matter which. We can't use it.
					}
					checkedBinder = true;
				}
				final java.sql.Array arr = javaTypeDescriptor.unwrap( value, java.sql.Array.class, options );
				Throwable suppressedEx = null;
				if ( nameBinder == null ) {
					try {
						nameBinder.invoke( st, name, arr );
					}
					catch ( Throwable t ) {
						suppressedEx = t;
					}
				}
				// Not that it's supposed to have setArray(String,Array) by standard.
				// There are numerous missing methods that only have versions for positional parameter,
				// but not named ones.

				try {
					st.setObject( name, arr, java.sql.Types.ARRAY );
				}
				catch (SQLException ex) {
					RuntimeException rex =  new HibernateException( "JDBC driver does not support named parameters for setArray. Use positional.", ex );
					rex.addSuppressed( suppressedEx );
					throw rex; // poor rex. He did not deserve to be thrown out
				}
			}
		};
	}

	@Override
	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
			@Override
			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
				return javaTypeDescriptor.wrap( rs.getArray( name ), options );
			}

			@Override
			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
				return javaTypeDescriptor.wrap( statement.getArray( index ), options );
			}

			@Override
			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
				return javaTypeDescriptor.wrap( statement.getArray( name ), options );
			}
		};
	}
}
