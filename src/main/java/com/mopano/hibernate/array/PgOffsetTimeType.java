/*
 * Copyright (c) Mak-Si Management Ltd. Varna, Bulgaria
 *
 * License: BSD 3-Clause license.
 * See the LICENSE.md file in the root directory or <https://opensource.org/licenses/BSD-3-Clause>.
 * See also <https://tldrlegal.com/license/bsd-3-clause-license-(revised)>.
 */
package com.mopano.hibernate.array;

import com.mopano.hibernate.array.java.PgOffsetTimeJavaDescriptor;

import java.time.OffsetTime;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.OffsetTimeType;
import org.hibernate.type.descriptor.sql.TimeTypeDescriptor;

/**
 * At the time of writing this, OffsetTimeJavaDescriptor neglects to convert to and from String,
 * which is compatible with PostgreSQL.
 *
 * @author Yordan Gigov
 */
public class PgOffsetTimeType 
		extends AbstractSingleColumnStandardBasicType<OffsetTime>
		implements LiteralType<OffsetTime> {
	
	public static final PgOffsetTimeType INSTANCE = new PgOffsetTimeType();

	public PgOffsetTimeType() {
		super( TimeTypeDescriptor.INSTANCE, PgOffsetTimeJavaDescriptor.INSTANCE );
	}

	@Override
	public String objectToSQLString(OffsetTime value, Dialect dialect) throws Exception {
		return "{t '" + OffsetTimeType.FORMATTER.format( value ) + "'}";
	}

	@Override
	public String getName() {
		return OffsetTime.class.getSimpleName();
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}
}
