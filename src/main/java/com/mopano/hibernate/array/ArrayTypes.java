/*
 * Copyright (c) Mak Ltd. Varna, Bulgaria
 * All rights reserved.
 *
 */
package com.mopano.hibernate.array;

import com.mopano.hibernate.array.java.GenericArrayTypeDescriptor;
import com.mopano.hibernate.array.sql.ArrayTypeDescriptor;

import java.util.ArrayList;
import java.util.IdentityHashMap;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.AbstractStandardBasicType;

/**
 * @author Yordan Gigov
 */
public class ArrayTypes<T>
		extends AbstractSingleColumnStandardBasicType<T[]> {

	private static final long serialVersionUID = 7485056093781707625L;

	private static final IdentityHashMap<AbstractStandardBasicType<?>, ArrayTypes<?>> existingTypes = new IdentityHashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> ArrayTypes<T> get(AbstractStandardBasicType<T> baseDescriptor, ServiceRegistry registry) {
		ArrayTypes<T> t = (ArrayTypes<T>) existingTypes.get(baseDescriptor);
		if (t == null) {
			t = new ArrayTypes<>(baseDescriptor, registry);
			existingTypes.put(baseDescriptor, t);
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	public static <T> ArrayTypes<T> get(AbstractStandardBasicType<T> baseDescriptor, ServiceRegistry registry, Class unwrap) {
		ArrayTypes<T> t = (ArrayTypes<T>) existingTypes.get(baseDescriptor);
		if (t == null) {
			t = new ArrayTypes<>(baseDescriptor, registry, unwrap);
			existingTypes.put(baseDescriptor, t);
		}
		return t;
	}

	private final String name;
	private final String[] regKeys;
	private final ServiceRegistry registry;

	public ArrayTypes(AbstractStandardBasicType<T> baseDescriptor, ServiceRegistry registry) {
		this( baseDescriptor, registry, null );
	}

	public ArrayTypes(AbstractStandardBasicType<T> baseDescriptor, ServiceRegistry registry, Class unwrap) {
		super( ArrayTypeDescriptor.INSTANCE, new GenericArrayTypeDescriptor<>( baseDescriptor, unwrap ) );
		this.name = baseDescriptor.getName() + "[]";
		this.registry = registry;
		this.regKeys = buildTypeRegistrations( baseDescriptor.getRegistrationKeys(), ArrayTypes.class.isInstance( baseDescriptor ) );
	}

	/**
	 * Builds the array registration keys, based on the original type's keys.
	 *
	 * @param baseKeys Array of keys used by the base type.
	 * @return
	 */
	private String[] buildTypeRegistrations(String[] baseKeys, boolean noSQLrecurse) {
		ClassLoaderService cls = registry.getService(ClassLoaderService.class);
		ArrayList<String> keys = new ArrayList<>( baseKeys.length << 1 );
		for ( String bk : baseKeys ) {
			String className;
			boolean addSQL = true;
			try {
				Class c;
				switch ( bk ) {
					case "boolean":
						c = boolean.class;
						className = "Z";
						break;

					case "byte":
						c = byte.class;
						className = "B";
						break;

					case "char":
						c = char.class;
						className = "C";
						break;

					case "double":
						c = double.class;
						className = "D";
						break;

					case "float":
						c = float.class;
						className = "F";
						break;

					case "int":
						c = int.class;
						className = "I";
						break;

					case "long":
						c = long.class;
						className = "J";
						break;

					case "short":
						c = short.class;
						className = "S";
						break;

					default:
						// load to make sure it exists
						c = cls.classForName( bk );
						className = c.getName();
						addSQL = false;
				}
				if ( c.isPrimitive() ) {
					// disallow. 
					continue;
				}
				if ( c.isArray() ) {
					keys.add( "[" + className );
				}
				else {
					keys.add( "[L" + className + ";" );
				}
			}
			catch ( ClassLoadingException ex ) {
				// just ignore. It means we won't be adding that key
			}
			if ( addSQL ) {
				// Not all type names given are Java classes, so assume the others are Database types
				if ( noSQLrecurse ) {
					// type is just "basetype ARRAY", never "basetype ARRAY ARRAY ARRAY"
					keys.add( bk );
				}
				else {
					// PostgreSQL type names
					keys.add( bk + "[]" );
					// standard SQL
					keys.add( bk + " ARRAY" );
					// also possible
					keys.add( bk + " array" );
				}
			}
		}
		return keys.toArray( new String[keys.size()] );
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String[] getRegistrationKeys() {
		return (String[]) regKeys.clone();
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

}
