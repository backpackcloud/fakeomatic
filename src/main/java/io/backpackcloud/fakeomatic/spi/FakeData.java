/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Marcelo Guimarães <ataxexe@backpackcloud.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.backpackcloud.fakeomatic.spi;

/**
 * Defines a component that can produce fake data.
 * <p>
 * This is the root object of the templates.
 *
 * @author Marcelo Guimarães
 * @see Sample
 */
public interface FakeData {

  /**
   * Returns the Sample data associated with the given name.
   *
   * @param sampleName the key for locating the Sample object.
   * @return the Sample object associated with the given name.
   */
  Sample sample(String sampleName);

  /**
   * Returns a random data from the Sample associated with the given placeholder.
   *
   * @param placeholder the placeholder to get the sample.
   * @return a random data.
   */
  String randomFor(char placeholder);

  /**
   * Returns a random data from the Sample associated with the given name.
   *
   * @param sampleName the name of the Sample.
   * @return a random data.
   */
  String random(String sampleName);

  /**
   * Returns a number that is at least the {@code min} and at max the {@code max}.
   *
   * @param min the minimum value
   * @param max the maximum value
   * @return a random number.
   */
  int number(int min, int max);

  /**
   * Returns a long that is at least the {@code min} and less than the {@code max}.
   *
   * @param min the minimum value
   * @param max the maximum value (excluded)
   * @return a random number.
   */
  long number(long min, long max);

  /**
   * Generates a random expression looking for placeholders in the given expression.
   *
   * @param expression the expression to evaluate.
   * @return a random generated expression.
   * @see #randomFor(char)
   */
  default String expression(String expression) {
    StringBuilder builder = new StringBuilder(expression.length());

    for (int i = 0; i < expression.length(); i++) {
      builder.append(randomFor(expression.charAt(i)));
    }

    return builder.toString();
  }

  /**
   * {@link #expression(String) Evaluates} a random data from the Sample associated with the given name.
   *
   * @param sampleName the name of the Sample.
   * @return a random generated expression.
   */
  default String expressionFrom(String sampleName) {
    return expression(random(sampleName));
  }

}
