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

import java.util.Map;
import java.util.Random;

/**
 * Interface that holds the configuration options.
 *
 * @author Marcelo Guimarães
 */
public interface Config {

  EndpointConfig endpoint();

  GeneratorConfig generator();

  TemplateConfig template();

  interface EndpointConfig {

    String url();

    /**
     * @return The maximum number of concurrent requests to the endpoint.
     */
    int concurrency();

    boolean insecure();

    Map<String, String> headers();

  }

  interface GeneratorConfig {

    int buffer();

    /**
     * @return The number of generated payloads.
     */
    int total();

    /**
     * @return The Random object to use.
     */
    Random random();

    /**
     * @return The endpoint that will receive the generated payloads.
     */
    String[] configs();

  }

  interface TemplateConfig {

    /**
     * @return Where to locate the template for generating the payloads.
     */
    String path();

    /**
     * @return Which Content-Type to pass to the endpoint.
     */
    String type();

  }

}
