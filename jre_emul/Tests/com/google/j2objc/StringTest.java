/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.j2objc;

import junit.framework.TestCase;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Additional tests for java.lang.String support.
 *
 * @author Keith Stanger
 */
public class StringTest extends TestCase {

  // Regression for Issue #751.
  public void testRegexReplace() {
    assertEquals("103456789", "000103456789".replaceFirst("^0+(?!$)", ""));
    assertEquals("103456789", "000103456789".replaceAll("^0+(?!$)", ""));
  }

  public void testReflectOnStringConstructors() throws Exception {
    Constructor<String> c;

    c = String.class.getDeclaredConstructor();
    assertNotNull(c);
    assertEquals("", c.newInstance());

    c = String.class.getDeclaredConstructor(char[].class);
    assertNotNull(c);
    assertEquals("foo", c.newInstance(new char[] { 'f', 'o', 'o' }));

    c = String.class.getDeclaredConstructor(char[].class, int.class, int.class);
    assertNotNull(c);
    assertEquals("bar", c.newInstance(new char[] { 'f', 'o', 'o', 'b', 'a', 'r' }, 3, 3));
  }

  public void testConstructionFromCodePoints() throws Exception {
    String s = new String(new int[] { 0x10000 }, 0, 1);
    assertEquals("𐀀", s);
    char[] chars = s.toCharArray();
    assertEquals(2, chars.length);
    assertEquals(0xD800, chars[0]);
    assertEquals(0xDC00, chars[1]);

    try {
      int[] ints = null;
      new String(ints, 0, 1);
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      // expected.
    }

    try {
      new String(new int[1], 0, 2);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      // expected.
    }

    try {
      new String(new int[] { Integer.MAX_VALUE }, 0, 1);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // expected.
    }
  }

  // String.join() tests from libcore.java.lang.StringTest.
  public void testJoin_CharSequenceArray() {
      assertEquals("", String.join("-"));
      assertEquals("", String.join("-", ""));
      assertEquals("foo", String.join("-", "foo"));
      assertEquals("foo---bar---boo", String.join("---", "foo", "bar", "boo"));
      assertEquals("foobarboo", String.join("", "foo", "bar", "boo"));
      assertEquals("null-null", String.join("-", null, null));
      assertEquals("¯\\_(ツ)_/¯", String.join("(ツ)", "¯\\_", "_/¯"));
  }

  public void testJoin_CharSequenceArray_NPE() {
      try {
          String.join(null, "foo", "bar");
          fail();
      } catch (NullPointerException expected) {}
  }

  public void testJoin_Iterable() {
      ArrayList<String> iterable = new ArrayList<>();
      assertEquals("", String.join("-", iterable));

      iterable.add("foo");
      assertEquals("foo", String.join("-", iterable));

      iterable.add("bar");
      assertEquals("foo...bar", String.join("...", iterable));

      iterable.add("foo");
      assertEquals("foo-bar-foo", String.join("-", iterable));
      assertEquals("foobarfoo", String.join("", iterable));
  }

  public void testJoin_Iterable_NPE() {
      try {
          String.join(null, new ArrayList<String>());
          fail();
      } catch (NullPointerException expected) {}

      try {
          String.join("-", (Iterable<String>) null);
          fail();
      } catch (NullPointerException expected) {}
  }
}
