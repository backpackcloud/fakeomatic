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

package com.backpackcloud.fakeomatic.quarkus;

import com.backpackcloud.Configuration;
import com.backpackcloud.impl.jackson.ConfigurationDeserializer;
import com.backpackcloud.sampler.Sample;
import com.backpackcloud.sampler.SampleConfiguration;
import com.backpackcloud.sampler.Sampler;
import com.backpackcloud.sampler.impl.SamplerImpl;
import com.backpackcloud.sampler.impl.sample.*;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

//TODO create extensions for this
public class BuildSteps {

  @BuildStep
  void registerClasses(BuildProducer<ReflectiveClassBuildItem> reflectionClasses) {
    Class[] classes = new Class[]{
      Sample.class,
      Sampler.class,
      SamplerImpl.class,
      SampleConfiguration.class,

      CachedSample.class,
      CharSample.class,
      DateSample.class,
      ExpressionSample.class,
      HttpSample.class,
      JoiningSample.class,
      JsonValueSample.class,
      ListSample.class,
      RangeSample.class,
      SourceSample.class,
      TemplateSample.class,
      UuidSample.class,
      WeightedSample.class,

      Configuration.class,
      ConfigurationDeserializer.class,
    };
    
    for (Class klass : classes) {
      reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, klass));
    }
  }

}
