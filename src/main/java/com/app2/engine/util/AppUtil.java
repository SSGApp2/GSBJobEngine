package com.app2.engine.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AppUtil {

	public static Object getDefaultValueIfNull(final Object value, final Object defaultValue) {
		Object result = defaultValue;
		if (value != null) {
			result = value;
		}
		return (result);
	}

	public static boolean isEmpty(final BigDecimal d) {
		final boolean b = AppUtil.isNull(d);
		return (b);
	}

	public static boolean isEmpty(final Byte byt) {
		final boolean b = AppUtil.isNull(byt);
		return (b);
	}

	public static boolean isEmpty(final Character c) {
		final boolean b = AppUtil.isNull(c);
		return (b);
	}

	public static boolean isEmpty(final Double d) {
		final boolean b = AppUtil.isNull(d);
		return (b);
	}

	public static boolean isEmpty(final Float f) {
		final boolean b = AppUtil.isNull(f);
		return (b);
	}

	public static boolean isEmpty(final Integer integer) {
		final boolean b = AppUtil.isNull(integer);
		return (b);
	}

	public static boolean isEmpty(final List<?> ls) {
		boolean b = true;
		if ((ls != null) && !ls.isEmpty()) {
			b = false;
		}
		return (b);
	}

	public static boolean isEmpty(final Long l) {
		final boolean b = AppUtil.isNull(l);
		return (b);
	}

	public static boolean isEmpty(final Map<?, ?> map) {
		boolean b = true;
		if ((map != null) && !map.isEmpty()) {
			b = false;
		}
		return (b);
	}

	public static boolean isEmpty(final Number num) {
		final boolean b = AppUtil.isNull(num);
		return (b);

	}

	public static boolean isEmpty(final Short s) {
		final boolean b = AppUtil.isNull(s);
		return (b);
	}

	public static boolean isEmpty(final String st) {
		boolean b = true;
		if ((st != null) && (st.trim().length() > 0)) {
			b = false;
		}
		return (b);
	}

	public static boolean isEmpty(final StringBuilder st) {
		boolean b = true;
		if ((st != null) && (st.toString().trim().length() > 0)) {
			b = false;
		}
		return (b);
	}
	
	public static boolean isEmpty(final String[] st) {
		boolean b = true;
		if ((st != null) && (st.length > 0)) {
			b = false;
		}
		return (b);
	}

	public static boolean isNotEmpty(final BigDecimal d) {
		final boolean b = AppUtil.isNotNull(d);
		return (b);
	}

	public static boolean isNotEmpty(final Byte byt) {
		final boolean b = AppUtil.isNotNull(byt);
		return (b);
	}

	public static boolean isNotEmpty(final Character c) {
		final boolean b = AppUtil.isNotNull(c);
		return (b);

	}

	public static boolean isNotEmpty(final Double d) {
		final boolean b = AppUtil.isNotNull(d);
		return (b);

	}

	public static boolean isNotEmpty(final Float f) {
		final boolean b = AppUtil.isNotNull(f);
		return (b);

	}

	public static boolean isNotEmpty(final Integer integer) {
		final boolean b = AppUtil.isNotNull(integer);
		return (b);

	}

	public static boolean isNotEmpty(final List<?> ls) {
		boolean b = false;
		if ((ls != null) && !ls.isEmpty()) {
			b = true;
		}
		return (b);

	}

	public static boolean isNotEmpty(final Long l) {
		final boolean b = AppUtil.isNotNull(l);
		return (b);

	}

	public static boolean isNotEmpty(final Map<?, ?> map) {
		boolean b = false;
		if ((map != null) && !map.isEmpty()) {
			b = true;
		}
		return (b);
	}

	public static boolean isNotEmpty(final Number num) {
		final boolean b = AppUtil.isNotNull(num);
		return (b);

	}

	public static boolean isNotEmpty(final Object obj) {
		boolean b = false;
		if (obj != null) {
			b = true;
		}
		return (b);
	}

	public static boolean isNotEmpty(final Short s) {
		final boolean b = AppUtil.isNotNull(s);
		return (b);
	}

	public static boolean isNotEmpty(final String st) {
		boolean b = true;
		if ((st == null) || (st.trim().length() == 0)) {
			b = false;
		}
		return (b);
	}
	
	public static boolean isNotEmpty(final String[] st) {
		boolean b = true;
		if ((st == null) || (st.length == 0)) {
			b = false;
		}
		return (b);
	}

	public static boolean isNotNull(final Object obj) {
		boolean b = false;
		if (obj != null) {
			b = true;
		}
		return (b);
	}

	public static boolean isNull(final Object obj) {
		boolean b = true;
		if (obj != null) {
			b = false;
		}
		return (b);
	}
	
	public static String toString(Object obj) {
		String r = "";
		if(AppUtil.isNotNull(obj)) {
			r = obj.toString();
		}
		return r;
	}
}
