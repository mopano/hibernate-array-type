/*
 * Copyright (c) Mak-Si Management Ltd. Varna, Bulgaria
 *
 * License: BSD 3-Clause license.
 * See the LICENSE.md file in the root directory or <https://opensource.org/licenses/BSD-3-Clause>.
 * See also <https://tldrlegal.com/license/bsd-3-clause-license-(revised)>.
 */
package com.mopano.hibernate.array.java;

import java.time.OffsetTime;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.OffsetTimeJavaDescriptor;

/**
 * At the time of writing this, OffsetTimeJavaDescriptor neglects to convert to and from String,
 * which is compatible with PostgreSQL.
 *
 * @author Yordan Gigov
 */
public class PgOffsetTimeJavaDescriptor extends OffsetTimeJavaDescriptor {

	public static final PgOffsetTimeJavaDescriptor INSTANCE = new PgOffsetTimeJavaDescriptor();

	@Override
	@SuppressWarnings("unchecked")
	public <X> X unwrap(OffsetTime offsetTime, Class<X> type, WrapperOptions options) {
		if ( offsetTime == null ) {
			return null;
		}

		if ( String.class.isAssignableFrom( type ) ) {
			return (X) this.toString( offsetTime );
		}

		return super.unwrap(offsetTime, type, options);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <X> OffsetTime wrap(X value, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}
		
		if ( String.class.isInstance( value ) ) {
			return this.fromString( (String) value );
		}

		return super.wrap(value, options);
	}

}
