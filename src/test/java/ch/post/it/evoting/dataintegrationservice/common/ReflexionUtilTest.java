/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.dataintegrationservice.common.ReflexionUtil;

class ReflexionUtilTest {

	@Test
	void getFieldValue() {
		A a = new A();

		String value = ReflexionUtil.getFieldValue(a, "b.f");
		assertNotNull(value);
		assertEquals("toto", value);
	}

	class A {
		private B b;

		public A() {
			b = new B();
			b.f = "toto";
		}

		class B {
			private String f;
		}
	}
}